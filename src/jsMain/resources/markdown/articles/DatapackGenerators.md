---
nav-title: Datapack Generators
title: Datapack Generators
description: A modern overview of datapack generators and why Kore stands out for ambitious projects.
keywords: minecraft, datapack, generators, kore, sandstone, beet, stewbeet
date-created: 2025-03-02
date-modified: 2026-02-14
root: .layouts.ArticleLayout
routeOverride: /articles/datapack-generators/index
---

Datapacks are no longer a pile of handcrafted JSON. With the Game Drops cadence, new registries (like `timeline` or `world_clock`) and the widespread use of item components, manual authoring quickly becomes fragile and expensive. That is exactly why the ecosystem pivoted to **datapack generators** and frameworks that treat packs like real software ([Rationale - Beet documentation](https://mcbeet.dev/rationale/#:~:text=As%20creators%20become%20more%20and,blown%20programming%20languages)).

This article focuses on the most used tools today, with a clear priority: **put Kore front and center**. The goal is simple: show why Kore is the best option even if you are already invested in Sandstone, Beet/StewBeet, or JMC.

*(Note: this article intentionally focuses on code-based generators. If your only goal is to quickly create simple JSON files, tools like [Misode’s Datapack Generators](https://misode.github.io/) are still perfect.)*

## Kore – Kotlin Datapack Generator Extraordinaire

[Kore](https://kore.ayfri.com) is an open-source Kotlin library that turns a datapack into a **real software project**: a typed DSL, readable builders, and stable generation of Minecraft JSON/functions. In short: you write clean Kotlin, compile, and get a reliable pack.

### Why Kore leads the pack

- **Type safety and readability**: Kotlin typing prevents silent errors (invalid tags, misspelled components, wrong registry keys). Autocomplete and compile-time errors save hours.
- **Broad coverage**: commands, functions, macros, predicates, loot tables, recipes, advancements, worldgen, dialogues, item components, timelines. Kore tracks new features without forcing you back into raw JSON.
- **Built-in helpers**: scheduler, inventory manager, display entities, mannequins, scoreboard utilities, and more. Repetitive patterns are already modeled.
- **Flexible build outputs**: generate to a folder, ZIP, or JAR using `.generate()`, `.generateZip()`, and `.generateJar()`.
- **Interoperability**: experimental bindings for importing existing datapacks and compatibility with pipelines that combine assets and logic.
- **Clear requirements**: Java 21+ and a Kotlin environment, with official docs to get you started quickly.

### Minimal example

```kotlin
fun main() {
    dataPack("example") {
        function("display_text") {
            tellraw(allPlayers(), textComponent("Hello World!"))
        }
    }.generateZip()
}
```

### Example: item components without the JSON pain

```kotlin
val midasSword = item(Items.GOLDEN_SWORD) {
    display {
        name = textComponent("Midas Sword")
    }
    components {
        attackRange(base = 5.0, correction = 0.5)
        weapon {
            kinetic(damage = 4.0)
            shockwave(radius = 1.5, destructible = false)
        }
        customData {
            "kore_rpg" {
                "id" put "midas_sword"
                "tier" put 5
            }
        }
    }
}
```

### Example: timeline builder for environmental control

```kotlin
timeline("blood_moon_cycle") {
    interpolated(0..24000) {
        keyframe(6000, SkyColor(0x87CEEB))
        keyframe(12000, SkyColor(0xFF4500)) {
            easing = Easing.CUBIC_IN
        }
        keyframe(18000, SkyColor(0x8B0000))
        keyframe(23000, SkyColor(0x87CEEB))
    }
}
```

### Already using another tool?

Kore does not force you to throw everything away. Keep your asset pipeline (Beet/StewBeet), import existing datapacks via bindings, and migrate your critical logic gradually to code that is **more maintainable and safer**.

### Why Kore is better long-term for big projects

- **Scales with complexity**: as systems grow (classes, progression, AI, world events), Kore’s typed DSL keeps changes local and reduces cascading breakage.
- **Refactor-friendly**: Kotlin refactors (rename, extract, inline) are reliable, and the compiler catches dead paths and mismatched types that would silently fail in JSON.
- **API coverage keeps you moving**: when new game features land, Kore’s typed builders prevent the “stringly-typed” regressions that add tech debt in large packs.
- **Maintainable collaboration**: Kotlin + Gradle supports code review, CI, and consistent formatting. Large teams benefit from clear abstractions and tests around generators.
- **Longevity**: big packs live for years. Kore minimizes “tribal knowledge” by baking knowledge into types and helpers instead of scattered JSON conventions.

*(Documentation and examples: [Kore site](https://kore.ayfri.com) and [Kore docs](https://kore.ayfri.com/docs/home).)*

## Sandstone – TypeScript Datapack Framework

Sandstone targets web developers: TypeScript, NPM, and ergonomics that feel like a front-end project. It is great for fast prototyping and leveraging the JavaScript ecosystem (tools, libraries, hot reload). The tradeoff is that typing is less strict than Kotlin, and the newest features can arrive with a short delay.

### Example: a simple function with variables and tags

```ts
import { MCFunction, tag, raw } from "sandstone";

MCFunction("join_announce", () => {
  raw("scoreboard objectives add joined dummy");
  raw("scoreboard players set @a joined 1");
  raw("tellraw @a {\"text\":\"Welcome!\",\"color\":\"gold\"}");
});

tag("minecraft:tick", "join_announce");
```

### Example: generate a loot table via JSON helpers

```ts
import { LootTable } from "sandstone";

LootTable("chests/ruby_cache", {
  pools: [
    {
      rolls: 1,
      entries: [{ type: "item", name: "minecraft:emerald", weight: 3 }],
    },
  ],
});
```

**When to choose it?** If your team is fully JavaScript/TypeScript and wants a native Node workflow. **When to prefer Kore?** When stability, completeness, and long-term maintenance matter most.

## Beet / StewBeet – Python Asset Pipeline

Beet is a **pack development kit**: an extensible Python pipeline that assembles datapack and resource pack. StewBeet builds on top with a “definitions to generation” approach, perfect for producing dozens of items, blocks, recipes, and language files without drowning in JSON.

### Example: a StewBeet block definition (YAML)

```yaml
blocks:
  ruby_ore:
    type: custom_block
    material: stone
    hardness: 3.0
    textures:
      all: "block/ruby_ore"
    drops:
      - item: "stewbeet:ruby"
        min: 1
        max: 3
        fortune: true
    recipe:
      - " S "
      - "R R"
      - " S "
      key:
        S: minecraft:stone
        R: minecraft:redstone
    lang:
      en_us: "Ruby Ore"
```

### Example: Beet pipeline hook (Python)

```python
from beet import Context

def beet_default(ctx: Context):
    ctx.require("beet.contrib.render")
    ctx.data["minecraft:tags/functions/tick.json"] = {
        "values": ["my_pack:tick"]
    }
```

**When to choose it?** Content-heavy projects, mass asset production, automation of models/loot/recipes. **Why it complements Kore:** Kore handles logic and systems, Beet/StewBeet handles assets. Together, they are extremely effective.

## JMC – Compact Scripting Language

JMC is a language that compiles to `mcfunction`. Its main value: write cleaner functions with loops, variables, and conditions without adopting a full framework. It is lightweight and quick to learn, but limited to command logic (no rich API for other datapack files).

### Example: loop + score logic

```jmc
scoreboard objectives add timer dummy

function tick() {
  timer = timer + 1
  if (timer >= 20) {
    tellraw @a "One second passed"
    timer = 0
  }
}
```

**When to choose it?** For fast prototypes or simple packs centered on functions. **When to prefer Kore?** As soon as the project grows or you want to centralize *all* resources and systems in one strongly typed codebase.

## Conclusion – Why Kore is still the best bet

The ecosystem offers several solid options, but **Kore** stands out for completeness, safety, and its ability to absorb game changes without forcing you back into fragile JSON. It combines the rigor of a real language, powerful helpers, and clear documentation.

For long-term, large-scale projects, Kore’s biggest advantage is **maintainability**: a typed API that survives refactors, shared abstractions that scale to large teams, and a workflow that supports CI, reviews, and consistent style. When your pack turns into a real product with ongoing updates, those qualities make the difference between constant firefighting and predictable growth.

If you already use Sandstone, Beet/StewBeet, or JMC, you do not need to throw everything away: adopt Kore first for critical logic, then expand gradually. This smooth transition is exactly why Kore is a credible choice **even for teams already equipped with other tools**.
