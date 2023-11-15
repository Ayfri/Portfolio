---
nav-title: Kore Introduction
title: Introduction to Datapack Creation with Kore in Kotlin
description: Learn how to create Minecraft datapacks using Kore in Kotlin.
date: 2023-11-13
root: .layouts.DocLayout
---

# Introduction to Datapack Creation with Kore in Kotlin

Welcome to the world of Minecraft datapack creation! If you've ever dreamed of creating your
own [Minecraft datapack](https://minecraft.fandom.com/wiki/Data_pack) but thought you were limited to using just the traditional tools, we
have exciting news for you. [Kore](https://github.com/Ayfri/Kore), a Kotlin-based library, is here to revolutionize the way you develop
datapacks for Minecraft.

Kore brings the power of [Kotlin](https://kotlinlang.org/) to the realm of Minecraft, allowing you to write cleaner, more maintainable code
with less effort. Whether you're a seasoned developer or just starting out, Kore provides a set of tools that simplifies the process of
datapack creation.

In this article, we'll introduce you to the basics of using Kore to create your very own Minecraft datapack.
You'll learn how to set up your environment, define your datapack's properties, and manage scoreboards, all within the comfort of Kotlin's
modern syntax.

## Setting Up Kore in Kotlin

To get started with creating Minecraft datapacks using Kore in Kotlin, you'll need to set up your development environment.
This process is straightforward and will allow you to harness the power of Kotlin for your Minecraft projects.

### Prerequisites

Before you begin, ensure you have the following installed:

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) 17 or later
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) or any preferred IDE that supports Kotlin
- [Gradle](https://gradle.org/) 7.6 or later
- [Kotlin](https://kotlinlang.org/) 1.9.20 or later

### Installation

1. Create a new Kotlin project in your IDE.
2. Add Kore as a dependency to your project.

    ```kotlin
    implementation("io.github.ayfri.kore:kore:VERSION")
    ```

3. Activate [Context Receivers](https://github.com/Kotlin/KEEP/issues/259) compiler option.

    ```kotlin
    kotlin {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }
    ```

## Creating a Datapack

Creating a datapack is a fantastic way to add new content to Minecraft, and with Kore, it's easier than ever.

```kotlin
val myDatapack = dataPack("my_datapack")
myDatapack.generate() // generates datapack in "out" folder
myDatapack.generateZip() // generates datapack in "out" folder and zips it
```

As you can see, creating a datapack is as simple as calling the `dataPack` function and passing in the name of your datapack.
Let's dive into how you can customize your datapack by changing its output folder and adding an icon.

#### Changing Output Folder

By default, datapacks are saved in an `out` folder, but you might want to change this to organize your projects better or to work directly
within a Minecraft world. Here's how you can specify a new output folder for your datapack:

```kotlin
dataPack("my_datapack") {
	path = Path("%appdata%/.minecraft/saves/my_world/datapacks")
}
```

This code sets the output folder to the `datapacks` directory of `my_world` in your `.minecraft` saves folder. Remember to
replace `my_world` with the name of your world.

#### Adding Icon

An icon adds a professional touch to your datapack and helps users identify it easily. Here's a simple way to set an icon for your datapack:

```kotlin
dataPack("my_datapack") {
	iconPath = Path("icon.png")
}
```

Place your `icon.png` file in the root of your datapack directory, and this code will link it as the icon for your datapack.

Here's an example of what your datapack will look like in-game with an icon:

![in-game screenshot of datapack with icon](/images/kore-introduction/datapack-icon.png)

## Creating your first function

Now that we've set up our environment and defined our datapack, let's create our first function. Here's how you can create a function:

```kotlin
dataPack("my_datapack") {
	function("my_function") {
		say("Hello, world!")
	}
}
```

This code creates a function named `my_function` that prints "Hello, world!" in the chat. Let's break down what's happening here:

- The `dataPack` function creates a new datapack with the name "my_datapack".
- The `function` function creates a new function named "my_function" within the datapack.
- The `say` function uses the `/say` command to print "Hello, world!" in the chat.

All of Minecraft commands are available as functions in Kore, so you can use them directly in your code. This makes it easy to create
functions that perform a variety of tasks, from printing messages to teleporting players.

For example, you could create a function that teleports every creeper to the player's location:

```kotlin
dataPack("my_datapack") {
	function("teleport_creeper") {
		execute {
			asTarget(allEntities { type = EntityType.CREEPER })
			run {
				teleportTo(nearestPlayer())
			}
		}
	}
}
```

## Managing Scoreboards with Kore

Now that we've seen how to create a datapack and functions, let's explore more advanced features of Kore. In this section, we'll learn how
to manage [scoreboards](https://minecraft.fandom.com/wiki/Scoreboard) using Kore, including adding objectives and creating scoreboard
displays.

Scoreboards are a powerful feature in Minecraft that allow you to keep track of player scores, objectives, and other game events. With Kore,
managing scoreboards becomes a seamless experience, leveraging Kotlin's concise syntax. Let's dive into how you can create and manage
scoreboards using Kore.

### Creating Scoreboards

To create a scoreboard in Kore, you can use the `scoreboard` command. Here's a simple example of how to add an objective to your scoreboard:

```kotlin
scoreboard {
	objectives.add("my_objective", ScoreboardCriteria.DUMMY)
}
```

This code snippet creates a new objective named "my_objective" with a criterion type of `DUMMY`, which is useful for objectives that are
manually updated by commands or plugins.

#### Creating Scoreboard Displays

Creating a display for your scoreboard objectives is straightforward. Here's how you can set up a display name, display slot, and render
type for your objective:

```kotlin
scoreboard {
	objective("my_objective") {
		add(ScoreboardCriteria.DUMMY, displayName = textComponent("My Objective", Color.GOLD))
		setRenderType(RenderType.INTEGER)
		setDisplaySlot(DisplaySlots.sidebar)
	}
}
```

In this example, we're adding an objective with a golden display name "My Objective". We then set the display slot to the sidebar and
specify that the scores should be rendered as integers.

![sidebar display screenshot](/images/kore-introduction/sidebar-display-screenshot.png)

Here's what the traditional `scoreboard` command would look like for this example:

```mcfunction
scoreboard objectives add my_objective dummy "My Objective"
scoreboard objectives modify my_objective displayname {"text":"My Objective","color":"gold"}
scoreboard objectives modify my_objective rendertype integer
scoreboard objectives setdisplay sidebar my_objective
```

As you can see, Kore's Kotlin-based syntax is much more concise and readable than the traditional command syntax.
Using Kotlin's features, we can extend any of these commands to simplify the process even further. For example, we can create a function
that adds an objective and sets the display slot and render type:

```kotlin
import io.github.ayfri.kore.commands.Scoreboard

fun Scoreboard.objective(
	name: String,
	criteria: ScoreboardCriteria,
	displayName: TextComponent,
	displaySlot: DisplaySlots,
	renderType: RenderType
) {
	objectives.add(name, criteria, displayName)
	objective(name) {
		setDisplaySlot(displaySlot)
		setRenderType(renderType)
	}
}

// usage
scoreboard.objective(
	"my_objective",
	ScoreboardCriteria.DUMMY,
	textComponent("My Objective", Color.GOLD),
	DisplaySlots.sidebar,
	RenderType.INTEGER
)
```

#### Limitations of Scoreboard Displays

While scoreboards are versatile, it's important to note that there are some limitations to their displays:

- Scoreboard displays are the same for all players; you can’t have individual displays for each player.
- You can differentiate displays by team color, but that's the extent of customization in that regard.
- There's a limit of 15 lines in a display.

By mastering these scoreboard management techniques with Kore, you can enhance your Minecraft datapacks with dynamic and interactive
elements. For more detailed information and advanced usage, check out
the [Kore documentation](https://github.com/Ayfri/Kore/wiki/Scoreboards) on Scoreboards.

## Going Further with Kore

Now that you have a solid foundation for creating datapacks with Kore, you may be wondering - what's next? How can I take my datapacks to
the next level? Here are some ideas for going further with Kore:

## Custom Recipes

Kore enables the creation of recipes beyond the standard options in vanilla Minecraft, such as specialized recipes for blasting and smoking.
The [recipe documentation](https://minecraft.fandom.com/wiki/Recipe) covers how to customize ingredients, results, shapeless recipes, shaped
recipes, and more.

## Custom Advancements

With Kore, all aspects of advancements can be customized like icons, frames, titles, descriptions, criteria and triggers. Players can be
rewarded for any achievement with things like experience and loot tables. See
the [advancement documentation](https://minecraft.fandom.com/wiki/Advancement) for more details.

## Custom Loot Tables

Kore's loot table system provides control over when and how items are generated from events like block breaking and mob drops. The number of
rolls, item pools, and loot conditions are configurable. Refer to
the [loot table documentation](https://minecraft.fandom.com/wiki/Loot_table) for more information.

## Custom World Generation

By integrating external libraries, Kore enables fully customized world generation. Unique overworlds, nether worlds, end worlds and more can
be procedurally created from scratch with custom terrain, structures and features. See
the [world generation documentation](https://minecraft.fandom.com/wiki/Custom_world_generation) for possibilities.

## Conclusion

In this article, we've explored the basics of creating Minecraft datapacks with the Kore library in Kotlin. We've seen how Kore simplifies
the process, allowing you to define datapacks and manage scoreboards with ease. By leveraging Kotlin's concise syntax and Kore's
straightforward API, you can create complex datapacks without getting bogged down in the details.

Here's a quick recap of what we covered:

- We set up Kore in a Kotlin project, readying our environment for datapack development.
- We learned how to define a datapack, change its output folder, and add an icon to personalize it.
- We explored how to create and manage scoreboards using Kore, including adding objectives and creating scoreboard displays.

Kore is a powerful tool that opens up a world of possibilities for Minecraft modding with Kotlin. Whether you're a seasoned developer or new
to the scene, Kore provides an accessible platform to bring your creative visions to life.<br>
It is also updated regularly, and compatible since Minecraft 1.20.1. Each new version of Minecraft _(including snapshots)_ is supported as
soon as possible.

For more detailed information and advanced features, be sure to check out the [Kore documentation](https://github.com/Ayfri/Kore/wiki) and
explore the [Kore repository](https://github.com/Ayfri/Kore) on GitHub.

We hope this article has inspired you to start using Kore for your Minecraft projects. Happy coding!
