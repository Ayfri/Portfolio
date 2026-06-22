---
nav-title: Kore Timer
title: Simple Timer Datapack with Kore
description: Create a timer datapack that displays a message every 20 seconds using the Kore library for Minecraft.
keywords: minecraft, datapack, kore, kotlin, tutorial, timer
date-created: 2024-05-19
date-modified: 2026-06-23
root: .layouts.ArticleLayout
routeOverride: /articles/kore-timer/index
---

Welcome to the world of Minecraft datapack creation! If you've ever wanted to create your own Minecraft datapack but felt limited by
traditional tools, Kore, a Kotlin-based library, is here to make the process easier and more efficient. In this article, we'll guide you
through creating a simple datapack that displays a message every 20 seconds using the "tick" function. We'll show you how to do this using
traditional commands and then how to achieve the same result using Kore.

## Prerequisites

Before we begin, ensure you have the following:

- Minecraft 1.21.11 installed
- Basic understanding of Minecraft commands
- Kotlin and Kore setup (refer to the [Kore Template](https://github.com/Kore-Minecraft/Kore-Template) for setup instructions)

Check [Kore Introduction](https://ayfri.com/articles/kore-introduction/) for more information on how to set up Kore in your Kotlin project.

## Creating the Timer Datapack with Commands

### Step 1: Setting Up the Datapack

1. **Create the Datapack Folder:**
	- Navigate to your Minecraft `saves` directory.
	- Open the folder of the world where you want to add the datapack.
	- Inside the world folder, create a new folder named `datapacks`.
	- Inside the `datapacks` folder, create a new folder for your datapack, e.g., `timer_datapack`.

2. **Create the `pack.mcmeta` File:**
	- Inside the `timer_datapack` folder, create a file named `pack.mcmeta`.
	- Add the following JSON content to the file:
   ```json
   {
	   "pack": {
		   "min_format": 94,
		   "max_format": 94,
		   "description": "A simple timer datapack"
	   }
   }
   ```
	- Since 1.21.9, `pack.mcmeta` uses `min_format`/`max_format` instead of the old `pack_format`; `94` is the data pack format for 1.21.11.

### Step 2: Adding the Timer Functionality

1. **Create the Functions Folder:**
	- Inside the `timer_datapack` folder, create a folder named `data`.
	- Inside the `data` folder, create another folder named `timer`.
	- Inside the `timer` folder, create a folder named `function`. _(Since 1.21 these directories are singular: `function`, not `functions`.)_

2. **Create the `tick.mcfunction` File:**
	- Inside the `function` folder, create a file named `tick.mcfunction`.
	- Add the following commands to the file:
   ```llvm
   scoreboard players add @a timer 1
   execute as @a[scores={timer=400..}] run say 20 seconds have passed!
   execute as @a[scores={timer=400..}] run scoreboard players set @a[scores={timer=400..}] timer 0
   ```

3. **Create the `load.mcfunction` File:**
	- Inside the `function` folder, create a file named `load.mcfunction`.
	- Add the following commands to the file:
   ```llvm
   scoreboard objectives add timer dummy
   ```

4. **Create the `tick.json` File:**
	- Inside the `data` folder, create a folder named `minecraft`.
	- Inside the `minecraft` folder, create a folder named `tags`.
	- Inside the `tags` folder, create a folder named `function`.
	- Inside the `function` folder, create a file named `tick.json`.
	- Add the following JSON content to the file:
   ```json
   {
	   "values": [
		   "timer:tick"
	   ]
   }
   ```

5. **Create the `load.json` File:**
	- Inside the `function` folder, create a file named `load.json`.
	- Add the following JSON content to the file:
   ```json
   {
	   "values": [
		   "timer:load"
	   ]
   }
   ```

### Step 3: Loading the Datapack

1. **Load the Datapack:**
	- Start Minecraft and open the world where you added the datapack.
	- Use the command `/reload` to load the datapack.
	- You should see a message in the chat confirming the datapack is loaded.

2. **Test the Timer:**
	- Wait for 20 seconds and you should see the message "20 seconds have passed!" in the chat.

## Creating the Timer Datapack with Kore

### Step 1: Setting Up the Project

1. **Clone the Kore Template:**
	- Clone the [Kore Template](https://github.com/Kore-Minecraft/Kore-Template) repository to your local machine.

2. **Open the Project:**
	- Open the project in your preferred Kotlin IDE (e.g., IntelliJ IDEA).

### Step 2: Adding the Timer Functionality

1. **Create the Timer Function:**
	- Inside the `src/main/kotlin` directory, create a new Kotlin file named `Timer.kt`.
	- Add the following Kotlin code to the file:
   ```kotlin
   import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
   import io.github.ayfri.kore.arguments.scores.score
   import io.github.ayfri.kore.arguments.types.literals.allPlayers
   import io.github.ayfri.kore.arguments.types.literals.self
   import io.github.ayfri.kore.commands.execute.execute
   import io.github.ayfri.kore.commands.say
   import io.github.ayfri.kore.commands.scoreboard.scoreboard
   import io.github.ayfri.kore.dataPack
   import io.github.ayfri.kore.functions.load
   import io.github.ayfri.kore.functions.tick
   import kotlin.io.path.Path

   fun main() {
	   val seconds = 20 // Change this value to the number of seconds you want to wait before the message is sent.
	   val datapack = dataPack("timer") {
		   load {
			   scoreboard.objectives.add("timer", ScoreboardCriteria.DUMMY)
		   }

		   tick {
			   scoreboard.objective("timer").add(allPlayers(), 1)
			   execute {
				   ifCondition {
					   score(self(), "timer") greaterThanOrEqualTo seconds * 20
				   }

				   run {
					   say("20 seconds have passed!")
					   scoreboard.objective("timer").reset(allPlayers())
				   }
			   }
		   }

		   path = Path("path/to/your/minecraft/saves/[Your World Name]/datapacks")
	   }

	   datapack.generateZip()
   }
   ```

### Step 3: Building and Loading the Datapack

1. **Build the Datapack:**
	- Use the build tool provided by the Kore template to build the datapack.
	- The built datapack will be located in the `build` directory.

2. **Load the Datapack:**
	- Copy the built datapack to the `datapacks` folder of your Minecraft world.
	- Start Minecraft and open the world where you added the datapack.
	- Use the command `/reload` to load the datapack.
	- You should see a message in the chat confirming the datapack is loaded.

3. **Test the Timer:**
	- Wait for 20 seconds and you should see the message "20 seconds have passed!" in the chat.

## A Simpler Approach: the Scheduler

The version above counts ticks by hand with a scoreboard, which is great for learning how timers work under the hood. But Kore ships a
`helpers` module with a **Scheduler** that wraps Minecraft's `/schedule` command, so you do not have to manage objectives, the `tick`
function, or `execute` checks at all.

First, add the `helpers` module to your dependencies (alongside the core `kore` one):

```kotlin
implementation("io.github.ayfri.kore:helpers:VERSION")
```

Then the whole timer becomes a few lines:

```kotlin
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.helpers.schedulerManager
import kotlin.io.path.Path

fun main() {
	val datapack = dataPack("timer") {
		schedulerManager {
			// Runs 20 seconds after load, then repeats every 20 seconds.
			addScheduler(20.seconds, 20.seconds) {
				say("20 seconds have passed!")
			}
		}

		path = Path("path/to/your/minecraft/saves/[Your World Name]/datapacks")
	}

	datapack.generateZip()
}
```

`addScheduler(delay, period)` takes a first delay and a repeat period, both as readable `TimeNumber` values like `20.seconds` or
`5.ticks`. Drop the `period` argument and it runs only once. No scoreboard, no `tick` function, no manual reset. For delayed tasks,
cancellation, and scheduling by function reference, see the [Scheduler docs](https://kore.ayfri.com/docs/helpers/scheduler).

> Need a per-entity countdown instead of a global loop (a respawn delay, an ability cooldown)? The `oop` module has a `registerTimer(...)`
> helper with `start(entity)` and `onComplete(entity) { ... }` methods, plus a boss-bar variant. Check the
> [OOP module docs](https://kore.ayfri.com/docs/oop/oop-utilities).

Congratulations! You've successfully created a simple timer datapack that displays a message every 20 seconds, first with traditional
commands, then with Kore's scoreboard DSL, and finally with the one-liner Scheduler from the `helpers` module. Happy coding!
