---
nav-title: Kore Hello World
title: Creating an Hello World Datapack with Kore
description: Learn how to create a simple Minecraft datapack that displays a "Hello World" message using the Kore library.
keywords: minecraft, datapack, kore, kotlin, tutorial
date-created: 2024-05-19
date-modified: 2024-05-19
root: .layouts.DocLayout
routeOverride: /articles/kore-hello-world/index
---

# Hello World Datapack with Kore

Welcome to the world of Minecraft datapack creation! If you've ever wanted to create your own Minecraft datapack but felt limited by
traditional tools, Kore, a Kotlin-based library, is here to make the process easier and more efficient. In this article, we'll guide you
through creating a simple datapack that displays a "Hello World" message in the chat when the game loads. We'll show you how to do this
using traditional commands and then how to achieve the same result using Kore.

## Prerequisites

Before we begin, make sure you have the following:

- Minecraft Java Edition installed.
- A basic understanding of Minecraft commands.
- Kotlin and a Kotlin-compatible IDE (like IntelliJ IDEA or Pycharm) installed.
- Kore library set up in your Kotlin project.

Check [Kore Introduction](https://ayfri.com/articles/kore-introduction/) for more information on how to set up Kore in your Kotlin project.

## Creating the Datapack with Commands

### Step 1: Setting Up the Datapack Folder

1. Navigate to your Minecraft saves folder. This is usually located
   at `C:\\Users\\[Your Username]\\AppData\\Roaming\\.minecraft\\saves\\[Your World Name]`.
2. Inside your world folder, create a new folder named `datapacks`.
3. Inside the `datapacks` folder, create a new folder for your datapack. Let's name it `hello_world`.

### Step 2: Creating the Datapack Files

1. Inside the `hello_world` folder, create a file named `pack.mcmeta` with the following content:
   ```json
   {
        "pack": {
            "pack_format": 41,
            "description": "Hello World Datapack"
        }
   }
   ```

2. Create a folder named `data` inside the `hello_world` folder.
3. Inside the `data` folder, create another folder named `minecraft`.
4. Inside the `minecraft` folder, create a folder named `tags`.
5. Inside the `tags` folder, create a folder named `functions`.
6. Inside the `functions` folder, create a file named `load.json` with the following content:
   ```json
   {
        "values": [
            "hello_world:load"
        ]
   }
   ```

### Step 3: Creating the Function File

1. Inside the `data` folder, create a new folder named `hello_world`.
2. Inside the `hello_world` folder, create a folder named `functions`.
3. Inside the `functions` folder, create a file named `load.mcfunction` with the following content:
    ```llvm
    tellraw @a {"text":"Hello World","color":"green"}
    ```

### Step 4: Loading the Datapack

1. Open Minecraft and load your world.
2. Use the command `/reload` to load the new datapack.
3. You should see the "Hello World" message in the chat.

## Creating the Datapack with Kore

### Step 1: Setting Up Your Kotlin Project

1. Create a new Kotlin project in your IDE.
2. Add the Kore library to your project. You can find the setup instructions on
   the [Kore README](https://github.com/Ayfri/Kore/blob/master/README.md#getting-started). Or use
   the [Kore Template](https://github.com/Ayfri/Kore-Template).

### Step 2: Creating the Datapack with Kore

1. Create a new Kotlin file in your project. Let's name it `HelloWorldDatapack.kt`.
2. Add the following code to create the datapack:
   ```kotlin
   import io.github.ayfri.kore.arguments.chatcomponents.textComponent
   import io.github.ayfri.kore.arguments.colors.Color
   import io.github.ayfri.kore.arguments.types.literals.allPlayers
   import io.github.ayfri.kore.commands.tellraw
   import io.github.ayfri.kore.dataPack
   import io.github.ayfri.kore.functions.load
   import io.github.ayfri.kore.pack.pack
   import kotlin.io.path.Path

   fun main() {
	   val datapack = dataPack("hello_world") {
		   pack {
			   description = textComponent("Hello World Datapack")
			   format = 41
		   }

		   load {
			   tellraw(allPlayers(), textComponent {
				   text ="Hello World"
				   color = Color.GREEN
			   })
		   }

		   path = Path("path/to/your/minecraft/saves/[Your World Name]/datapacks")
	   }

	   datapack.generateZip()
   }
   ```

### Step 3: Running the Kotlin Code

1. Run the `HelloWorldDatapack.kt` file in your IDE.
2. This will generate the datapack files and save them to the specified path.

### Step 4: Loading the Datapack

1. Open Minecraft and load your world.
2. Use the command `/reload` to load the new datapack.
3. You should see the "Hello World" message in the chat.

### Under the Hood

Kore will generate the necessary files for your datapack based on the code you provide. The `load` function will create a function with a
random name and add the `tellraw` command to it. So you don't need to worry about naming the function or creating the function file, this
simplifies the process for beginners. The `pack` function will create the `pack.mcmeta` file with the specified description and format.
The `dataPack` function will create the necessary folders and files for the datapack. The `generateZip` function will generate a zip file
containing the datapack files.

## Conclusion

In this article, we've shown you how to create a simple Minecraft datapack that displays a "Hello World" message in the chat when the game
loads. We demonstrated how to achieve this using traditional commands and then how to simplify the process using the Kore library in Kotlin.
Kore makes it easier to write cleaner, more maintainable code for your Minecraft datapacks. For more detailed information and advanced
features, be sure to check out the Kore documentation and explore the Kore repository on GitHub. Happy coding!
