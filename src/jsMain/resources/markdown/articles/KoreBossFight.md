---
nav-title: Kore Boss Fight
title: Build a Boss Fight Datapack with Kore
description: Build a complete Minecraft boss fight in Kotlin with Kore - a legendary weapon with item components, a custom boss, an OOP boss bar, particle VFX, and a state machine driving the whole fight.
keywords: minecraft, datapack, kore, kotlin, tutorial, boss fight, boss bar, item components, particles, state machine
date-created: 2026-06-23
date-modified: 2026-06-23
root: .layouts.ArticleLayout
routeOverride: /articles/kore-boss-fight/index
---

The first Kore tutorials on this site stay small on purpose: a [Hello World](https://ayfri.com/articles/kore-hello-world/), a
[timer](https://ayfri.com/articles/kore-timer/), some [zone detection](https://ayfri.com/articles/kore-zone-detection/). They are great to
learn the syntax, but they never show what Kore is actually built for: **big systems that would be a nightmare in raw JSON**.

So this time we go bigger. We are going to build a full boss fight, end to end, in pure Kotlin:

- A **legendary weapon** crafted with item components (name, lore, enchantments, attribute modifiers).
- A **custom boss** with boosted health and a name.
- An **OOP boss bar** that tracks the fight phases.
- **Particle VFX** for the arena intro and the victory burst.
- A **state machine** that ties the whole flow together (`lobby` to `fighting` to `victory`).

Everything below is real Kore code. By the end you will have a single `main()` that generates a datapack you can drop into a world and play.

## Prerequisites

Before we begin, make sure you have:

- Minecraft Java Edition 1.21.11.
- A basic understanding of Minecraft commands.
- Kotlin and a Kotlin-compatible IDE (IntelliJ IDEA or PyCharm).
- Kore **2.0 or later**. The OOP boss bar and state machine helpers we use here landed in the 2.x line, so anything older will not compile.
  If you have not set Kore up yet, check the [Kore Introduction](https://ayfri.com/articles/kore-introduction/) or just clone the
  [Kore Template](https://github.com/Kore-Minecraft/Kore-Template).

This project uses three Kore modules. The core `kore` module does most of the work, and we pull in `oop` for the boss bar and state machine,
plus `helpers` for the particle shapes:

```kotlin
dependencies {
	implementation("io.github.ayfri.kore:kore:VERSION")
	implementation("io.github.ayfri.kore:oop:VERSION")
	implementation("io.github.ayfri.kore:helpers:VERSION")
}
```

Kore versions are tagged per Minecraft version, so for 1.21.11 you want a `2.0.x-1.21.11` build (or newer). Grab the exact latest from
[Maven Central](https://central.sonatype.com/artifact/io.github.ayfri.kore/kore) or the [releases page](https://github.com/Ayfri/Kore/releases).

## What we are building

The flow is simple and readable, which is exactly the point. A player runs a command, the arena lights up, the boss spawns with a boss bar,
and once it dies everyone gets a victory burst. We model that flow as three states and let Kore generate every function, tag, and JSON file
behind it.

Let's build it piece by piece, then assemble everything in one `main()` at the end.

## Step 1: Forging the legendary weapon

In vanilla, giving a player a fully customized item means writing a wall of nested JSON inside a `/give` command, with zero autocomplete and
zero safety. One typo in a component name and the command silently does nothing.

With Kore, an item is just a typed builder. Here is the sword our hero uses against the boss:

```kotlin
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.item.Rarities
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.arguments.types.literals.randomUUID

val dragonSlayer = Items.NETHERITE_SWORD {
	customName(textComponent("Dragon Slayer", Color.GOLD) { bold = true })

	lore(
		textComponent("Forged to end the fight.", Color.GRAY) +
			text(" Hits harder than it should.", Color.DARK_GRAY)
	)

	enchantments(mapOf(Enchantments.SHARPNESS to 5, Enchantments.FIRE_ASPECT to 2))
	unbreakable()
	rarity(Rarities.EPIC)

	attributeModifiers {
		modifier(
			type = Attributes.ATTACK_DAMAGE,
			amount = 6.0,
			name = "Slayer Strike",
			operation = AttributeModifierOperation.ADD_VALUE,
			uuid = randomUUID(),
		)
	}
}
```

Every single line here is checked at compile time. `Enchantments.SHARPNESS`, `Attributes.ATTACK_DAMAGE`, the `Rarities` enum: they all come
from generated registries, so you cannot misspell a key or feed a value that does not exist. Refactor the name once and your whole codebase
follows.

For the full catalog of components (food, fireworks, custom data, profiles, and more), check the
[Components guide](https://kore.ayfri.com/docs/concepts/components).

## Step 2: The fight state machine

Instead of juggling scoreboard scores by hand, we use the OOP module's state machine. We declare the states once and Kore generates the
objective and the initial state for us:

```kotlin
import io.github.ayfri.kore.gamestate.registerGameStates

val fight = registerGameStates {
	state("lobby")
	state("fighting")
	state("victory")
}
```

From now on, switching phases is one readable call, `fight.transitionTo("fighting")`, and reacting to a phase is `fight.whenState("...") { }`.
No magic numbers, no manual objective bookkeeping. See the
[Game State Machine docs](https://kore.ayfri.com/docs/oop/game-state-machine) for the integration helpers (timers, spawners, cooldowns).

## Step 3: The boss bar

The OOP module wraps Minecraft boss bars into a config-plus-handle pattern. You register the bar once with its initial settings, and Kore
generates a load function that creates it:

```kotlin
import io.github.ayfri.kore.bossbar.registerBossBar
import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.commands.BossBarStyle

val bossBar = registerBossBar("dragon_bar") {
	displayName = textComponent("The Ender Warden", Color.LIGHT_PURPLE)
	color = BossBarColor.PURPLE
	style = BossBarStyle.NOTCHED_10
	max = 100
	value = 100
}
```

Later, from any function, we just call `bossBar.setValue(...)`, `bossBar.show()`, or `bossBar.hide()`. One place to configure, reused
everywhere. Full method list in the [Boss Bars docs](https://kore.ayfri.com/docs/oop/boss-bars).

## Step 4: Arena VFX

A boss fight needs presence. The `helpers` VFX engine pre-computes geometric particle shapes at generation time and bakes them into a
reusable function, so you describe the shape once instead of writing dozens of `/particle` lines:

```kotlin
import io.github.ayfri.kore.helpers.vfx.drawShape
import io.github.ayfri.kore.helpers.vfx.Shape
import io.github.ayfri.kore.generated.Particles

fun DataPack.arenaIntro() = drawShape("arena_intro") {
	shape = Shape.SPIRAL
	particle = Particles.SOUL_FIRE_FLAME
	radius = 4.0
	points = 60
	height = 6.0
	turns = 5
}

fun DataPack.victoryBurst() = drawShape("victory_burst") {
	shape = Shape.SPHERE
	particle = Particles.HAPPY_VILLAGER
	radius = 3.0
	points = 80
}
```

Each call returns a function reference we can run wherever we want. Check the
[VFX Particles docs](https://kore.ayfri.com/docs/helpers/vfx-particles) for every shape and parameter.

## Step 5: Wiring the fight together

Now the fun part. We have a weapon, a state machine, a boss bar, and effects. Let's connect them into actual gameplay functions.

### Starting the fight

When the player starts the fight, we give them the weapon, play the intro spiral, spawn the boss, and flip into the `fighting` state:

```kotlin
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.literals.vec3
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Entities
import io.github.ayfri.kore.utils.nbt

fun DataPack.startFight() = function("start_fight") {
	give(allPlayers(), dragonSlayer)

	function(arenaIntro()) // run the generated spiral effect

	// Summon a beefed-up Warden tagged so we can find it later.
	summon(Entities.WARDEN, vec3(), nbt {
		this["CustomName"] = textComponent("The Ender Warden", Color.LIGHT_PURPLE)
		this["Tags"] = listOf("boss")
		// Java 1.21+ uses the lowercase "attributes" list with "id" + "base".
		this["attributes"] = listOf(nbt {
			this["id"] = "minecraft:max_health"
			this["base"] = 100.0
		})
		this["Health"] = 100f
	})

	bossBar.apply {
		setPlayers(allPlayers())
		setValue(100)
		show()
	}

	fight.transitionTo("fighting")
}
```

The `summon` builder takes the entity type, a position, and an NBT block. Because Kore exposes the NBT DSL directly, you build the boss data
with the same typed `nbt { }` you use everywhere else, no string concatenation.

### The fight loop

Each tick, while we are in the `fighting` state, we keep the boss bar in sync and check whether the boss is still alive. When the `boss` tag
no longer matches any entity, the fight is won:

```kotlin
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.tick

fun DataPack.fightLoop() = tick {
	fight.whenState("fighting") {
		// While at least one boss exists, keep the bar shown.
		execute {
			asTarget(allEntities { tag = "boss" })
			run {
				bossBar.show()
			}
		}

		// No more boss tagged entity -> the fight is over.
		execute {
			unlessCondition {
				entity(allEntities { tag = "boss" })
			}
			run {
				function(winFight())
			}
		}
	}
}
```

### Winning

Victory hides the bar, fires the celebratory sphere of particles, congratulates the players, and resets the state so the fight can be
replayed:

```kotlin
import io.github.ayfri.kore.commands.tellraw

fun DataPack.winFight() = function("win_fight") {
	bossBar.hide()
	function(victoryBurst())
	tellraw(allPlayers(), textComponent("The Ender Warden has fallen!", Color.GREEN) { bold = true })
	fight.transitionTo("victory")
}
```

## Step 6: Assembling the datapack

All the pieces are independent functions hanging off `DataPack`, so the final `main()` reads like a table of contents. That readability is
the whole reason to build datapacks this way:

```kotlin
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.pack.pack
import kotlin.io.path.Path

fun main() {
	val datapack = dataPack("boss_fight") {
		pack {
			description = textComponent("Boss Fight", Color.GOLD) +
				text(" powered by Kore", Color.AQUA)
		}

		// Gameplay. The effect functions are generated lazily the first time
		// startFight() and winFight() reference them, so we only wire these two.
		startFight()
		fightLoop()

		path = Path("path/to/your/minecraft/saves/[Your World Name]/datapacks")
	}

	datapack.generateZip()
}
```

Run it, load the world, `/reload`, then trigger the start function with `/function boss_fight:start_fight`. The arena lights up, the Warden
spawns under a purple boss bar, and the moment it dies everyone gets a victory burst. The whole thing is a few dozen lines of Kotlin instead
of a folder full of fragile JSON.

## Going further

This is a foundation you can push in a lot of directions:

- **Phases**: drop the boss bar color and value at health thresholds, and use `fight.whenState(...)` to spawn adds or change attacks.
- **A start menu**: instead of a raw `/function` call, open a [Dialog](https://kore.ayfri.com/docs/data-driven/dialogs) (1.21.6+) so players
  pick a difficulty before the fight begins.
- **Rewards**: hand out a custom loot table or advancement on victory, again fully typed with components.
- **Real health tracking**: store the boss health into a scoreboard each tick and feed it straight into `bossBar.setValue(...)` for a smooth
  bar instead of phase steps.

Every one of those additions is a small, local change because the logic lives in typed builders, not scattered strings. That is the part that
matters when a pack stops being a toy and becomes a project you maintain for months.

## Conclusion

We built a complete boss fight without touching a single `.mcfunction` or `.json` file by hand. A legendary weapon from item components, a
custom boss, an OOP boss bar, baked particle VFX, and a state machine driving the flow, all in one Kotlin file that you can read top to bottom
and actually understand a month later.

That is the real pitch for Kore: the small tutorials show you the syntax, but the payoff is everything you stop having to track in your head
once your pack gets ambitious. For the full API, head to the [Kore documentation](https://kore.ayfri.com/docs/home) and the
[Kore repository](https://github.com/Ayfri/Kore) on GitHub.

Now go make something that hits back. Happy coding!
