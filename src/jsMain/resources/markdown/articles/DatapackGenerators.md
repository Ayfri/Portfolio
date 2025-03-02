---
nav-title: Datapack Generators  
title: Datapack Generators in 2025  
description: Learn about the most used and well-known datapack generators in 2025.  
keywords: minecraft, datapack, generators, kore, sandstone, beet  
date-created: 2025-03-02  
date-modified: 2025-03-02  
root: .layouts.DocLayout  
routeOverride: /articles/datapack-generators/index  
---

DDatapacks have become a fundamental method for customizing Minecraft gameplay, but manually writing them in raw JSON and mcfunction files can quickly become repetitive and tedious. Over the years, the Minecraft community has introduced numerous **datapack generators** – useful tools and versatile frameworks designed to streamline pack development ([Rationale - Beet documentation](https://mcbeet.dev/rationale/#:~:text=As%20creators%20become%20more%20and,blown%20programming%20languages)). Now, from simple online utilities to sophisticated programming frameworks, datapack creators have a broad range of choices available. In this article, we'll explore *the most prominent and widely used datapack generators in 2025*, analyzing their respective features and design philosophies. Our focus will mainly be on **Kore**, a Kotlin-based solution renowned for its **completeness**, **readability**, **maintenance**, and **extensibility** strengths. Additionally, we’ll review popular alternatives like Sandstone, Beet, and JMC to understand how Kore measures up. Whether you're an experienced datapack developer or new to Minecraft scripting, continue reading to find the ideal tool for your needs.

*(Note: This article aims specifically at advanced code-based datapack generators. If your goal is to quickly create simple JSON-based files, resources such as [Misode’s Datapack Generators](https://misode.github.io/) or similar online tools could be more helpful. Here, however, I'm focusing on comprehensive frameworks that let you directly **program** your datapacks.)*

## Kore – Kotlin Datapack Generator Extraordinaire

[Kore](https://kore.ayfri.com) ([Home - Kore, library for making Datapacks](https://kore.ayfri.com/#:~:text=Rethink%20your%20datapack%20development%20experience,with%20Kore)) is an innovative, open-source Kotlin library that is transforming the Minecraft datapack ecosystem. Kore allows you to implement datapack logic in **pure Kotlin code** instead of manually editing JSON configurations or `.mcfunction` files ([Home - Kore, library for making Datapacks](https://kore.ayfri.com/#:~:text=Rethink%20your%20datapack%20development%20experience,with%20Kore)). It leverages the full potential of Kotlin, empowering developers to write datapacks in a *type-safe*, object-oriented manner. This means you benefit from features such as **auto-completion**, robust **compile-time error checking**, and the organizational strengths of a structured programming language, substantially reducing cryptic in-game syntax errors. With Kore, your datapack creation process becomes smoother and more predictable.

**Completeness:** One of Kore’s most notable qualities is its extensive support of Minecraft features. Kore’s API covers **all current Minecraft commands (including subcommands and various syntaxes), as well as all JSON-based elements** such as advancements, loot tables, recipes, and more ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=,Blocks%2C%20Items%2C%20Entities%2C%20Advancements%2C)). Simply put, whatever you'd normally accomplish manually in a datapack can be seamlessly achieved through Kore’s API. It even includes predefined lists of game entities (blocks, items, mobs, etc.) and utility classes for handling common concepts such as colors, vectors, and rotations ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=,datapacks%2C%20even%20with%20existing%20zips)). Thanks to this feature set, developers rarely—if ever—need to resort to raw Minecraft files. For instance, creating custom recipes or advancements in Kore is both intuitive and entirely supported out-of-the-box. Moreover, Kore supports multiple datapack formats: generating standalone directories, ZIP archives, or even packaging datapacks in `.jar` formats compatible with popular mod loaders ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=,Advancements%2C%20Loot%20Tables%2C%20Recipes%2C)). This flexibility simplifies integrating the generated datapack into mods or distributing them as self-contained packages.

**Readability:** Datapack code written with Kore remains notably readable and straightforward to maintain. Kore’s **Kotlin DSL (Domain Specific Language)** closely mirrors Minecraft concepts, significantly improving readability. For example, defining a function in Kore may look like this:

```kotlin
function("welcome") {
    tellraw(allPlayers(), textComponent("Hello World!"))
}
```

This snippet speaks for itself—it creates a Minecraft function broadcasting "Hello World!" to all connected players. Using Kotlin’s expressive syntax, builders and context receivers, Kore offers code that reads almost like plain English, a clear improvement over the traditional mcfunction scripting experience. *Writing Minecraft functions with Kore becomes notably easier and more intuitive*, leveraging Kotlin's highly expressive syntax ([GitHub - WingedSeal/jmc: A compiler for JMC (JavaScript-like Minecraft Function), a mcfunction extension language for making Minecraft Datapack](https://github.com/WingedSeal/jmc#:~:text=)). Advanced high-level constructs (loops, conditionals, reusable functions) automatically compile down to optimized Minecraft commands. Developers describe Kore as a tool allowing them to **"write cleaner and easier-to-maintain code typically requiring significantly less effort"** ([Kore Introduction - Pierre Roy's Blog](https://ayfri.com/articles/kore-introduction/#:~:text=Kore%20brings%20the%20power%20of,the%20process%20of%20datapack%20creation)), compared to traditional hand-crafted scripting techniques.

**Maintenance:** Migrating datapack creation to Kore means better maintainability—especially essential in large projects. Because Kore datapacks are plain Kotlin projects, standard coding practices such as package-based organization, version control, and unit testing become feasible. Kore's approach notably simplifies refactoring: updating your datapacks to newer Minecraft versions or implementing substantial changes requires modifying code in one clearly defined area rather than searching through endless function files. Furthermore, Kore offers built-in utilities such as an **inventory and scheduler manager** or a powerful **scoreboard display manager** ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=,blocks%2C%20items%2C%20entities%2C)). These high-level helpers abstract away repetitive tasks—such as managing scoreboard objectives—into reusable modules, saving precious time and significantly reducing bugs. Kore also features integrated debugging capabilities, facilitating datapack logic testing by embedding debug commands directly into your code ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=,blocks%2C%20items%2C%20entities%2C)).

**Extensibility:** Kore excels in terms of extensibility. Designed for expansion and customization, all of its internal APIs—for commands, selectors, tags, and more—are publicly accessible and open for contributors ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=)). Thus, extending Kore by adding extra features or custom commands represents a straightforward process involving Kotlin's extension functions. Kore additionally supports **macros** and an experimental OOP module to further improve code structuring ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=,OOP%20module%20%28experimental)). Overall, Kore isn't a closed-off solution; it's actively developed software ready to adapt to future Minecraft updates or custom user requirements.

*Kore Summary:* If you're searching for a comprehensive, scalable datapack solution suitable for projects ranging from modest modifications to ambitious creations, Kore constitutes an exceptional choice. Actively maintained, supported by an enthusiastic community (for more information, visit the official Kore [website](https://kore.ayfri.com) and documentation), and reflecting cutting-edge coding standards, Kore ensures robust datapack code where the only limit is your imagination.

### Example of a Kore datapack

```kotlin
// Example of generating a datapack with Kore containing a function, advancement and crafting recipe.
fun main() {
    val datapack = dataPack("my_first_datapack") {
        pack.description = textComponent("My first Kore datapack!", Color.AQUA)

        function("hello_world") {
            tellraw(allPlayers(), textComponent("Hello, world!", Color.GREEN))
        }

        recipes.craftingShaped("diamond_from_coal") {
            pattern("CCC","CDC","CCC")
            key("C", Items.COAL_BLOCK)
            key("D", Items.DIRT)
            result(Items.DIAMOND)
        }

        advancements.grantAdvancement(allPlayers(), "my_datapack:root")
    }

    datapack.generateZip()
}
```

*(For more on Kore, check out the [Kore documentation](https://kore.ayfri.com) and GitHub repo. You can also join the [Ayfri Discord](https://discord.gg/BySjRNQ9Je) for support and discussions.)*

## Sandstone – Datapack Framework in TypeScript

Another popular datapack generator in 2025 is **Sandstone**, a framework that allows you to create Minecraft datapacks using **TypeScript**. Sandstone appeals especially to web developers and JavaScript/TypeScript enthusiasts, as it brings datapack scripting into the Node.js ecosystem. Like Kore, Sandstone’s goal is to eliminate the need to write raw mcfunction files or JSON by hand. Instead, you use the familiar syntax of TypeScript (or JavaScript) along with Sandstone’s API to define your pack’s content. The project bills itself as a "Next Generation Framework for Minecraft" ([GitHub - sandstone-mc/sandstone: Sandstone | Next Generation Framework for Minecraft](https://github.com/sandstone-mc/sandstone#:~:text=Sandstone%20,for%20Minecraft)) and provides a powerful set of tools to streamline datapack creation.

**Approach and Features:** Sandstone focuses on making the developer experience as smooth as possible. Being in TypeScript, it provides **intelligent autocomplete and hints for Minecraft commands** while you code ([GitHub - sandstone-mc/sandstone: Sandstone | Next Generation Framework for Minecraft](https://github.com/sandstone-mc/sandstone#:~:text=match%20at%20L295%20Sandstone%20tells,autocompletes%20complicated%20arguments%20for%20you)). The framework knows what each command expects for arguments, so as you type commands, it will suggest the correct parameters (e.g., coordinate types, selectors, etc.). This is a huge help, especially for those who haven’t memorized every detail of command syntax. *"Sandstone tells you what a command expects, and autocompletes complicated arguments for you. You don't need to remember commands syntax anymore."* ([GitHub - sandstone-mc/sandstone: Sandstone | Next Generation Framework for Minecraft](https://github.com/sandstone-mc/sandstone#:~:text=match%20at%20L295%20Sandstone%20tells,autocompletes%20complicated%20arguments%20for%20you)) the documentation proudly states. This is similar in spirit to Kore’s approach, although achieved through the TypeScript type system and editor integration.

Sandstone allows you to write code that closely resembles normal JavaScript logic. It supports high-level constructs like **if/else conditions and loops** in your TypeScript code, which the framework will compile down into the necessary Minecraft commands (using functions, predicates, scoreboards behind the scenes to mimic those structures) ([GitHub - sandstone-mc/sandstone: Sandstone | Next Generation Framework for Minecraft](https://github.com/sandstone-mc/sandstone#:~:text=Sandstone%20includes%20common%20and%20heavily,optimized%20abstractions)). For example, you could write something akin to an `if(playerIsOnGround)` check in TypeScript, and Sandstone will handle creating the predicate or execute command needed in the actual datapack. It also has boolean logic (`and`, `or`, `not`) that is far more intuitive to use in code than nesting a bunch of command conditions ([GitHub - sandstone-mc/sandstone: Sandstone | Next Generation Framework for Minecraft](https://github.com/sandstone-mc/sandstone#:~:text=Sandstone%20includes%20common%20and%20heavily,optimized%20abstractions)). This means your datapack logic can be written in a *declarative, structured way*, and Sandstone figures out the low-level implementation details.

Another strength of Sandstone is its flexibility in organizing your project. You are not forced to follow the vanilla convention of one function per file or rigid folder structures. Instead, **Sandstone lets you organize your code however you like** and then it will produce the standard datapack structure on compile ([GitHub - sandstone-mc/sandstone: Sandstone | Next Generation Framework for Minecraft](https://github.com/sandstone-mc/sandstone#:~:text=match%20at%20L308%20can%20keep,without%20sticking%20to%20Mojang%27s%20conventions)). This is nice for large projects because you might want to group logic by theme or mechanic rather than worry about file naming upfront. The framework also touts an ecosystem of extensions – common tasks like raycasting or custom tellraw formatting can be achieved via community extensions, so you *"finally stop reinventing the wheel"* ([GitHub - sandstone-mc/sandstone: Sandstone | Next Generation Framework for Minecraft](https://github.com/sandstone-mc/sandstone#:~:text=match%20at%20L335%20like%20raycasting%2C,ecosystem%20grows%20by%20the%20day)). And since it’s all JavaScript, sharing code on NPM or bundling libraries for datapacks becomes possible, which opens up collaborative opportunities.

**Status and Comparison:** It’s worth noting that Sandstone, while powerful, was (as of early 2025) still in **beta** development. The project has made great strides, but it’s labeled as *v1.0.0-beta* ([GitHub - sandstone-mc/sandstone: Sandstone | Next Generation Framework for Minecraft](https://github.com/sandstone-mc/sandstone#:~:text=v1.0.0,testers%20Latest%20Dec%201%2C%202023)) and is not yet at a 1.0 stable release. The community considers it an "incomplete" project ([Minecraft function precompilers · GitHub](https://gist.github.com/Ellivers/db296c438f9f87bbf9c79d24f940fe03#:~:text=Incomplete)), meaning not every single Minecraft feature may be fully wrapped or there may be rough edges. In practice, this means you might encounter a newer game feature that isn’t directly supported by the API and you’d have to use a workaround or contribute a fix. However, many developers are already using Sandstone for 1.19+ datapacks and reporting good results. Its GitHub is active, and the promise of a full 1.0 release suggests that completeness will continue improving.

When comparing **Sandstone vs. Kore**, a lot comes down to your language preference and project needs. Sandstone’s use of TypeScript makes it very accessible if you already know JavaScript or if you prefer a dynamically-typed feeling (though TS is typed, you can opt-out when needed). It doesn’t require setting up a JVM or Kotlin – just a Node.js environment – which some might find simpler. The development workflow (using `npm` to install, writing code in VS Code, etc.) will feel familiar to web developers. Sandstone’s strength is definitely the *developer experience* with autocompletion and the ability to use the vast NPM ecosystem for any supporting tasks (for instance, you could pull in a library for noise generation to make a world-gen datapack, and run it as part of your pack build).

However, **Kore holds some advantages**. Kore’s API is arguably more **complete at this stage**, covering *all* commands and JSON components ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=,Blocks%2C%20Items%2C%20Entities%2C%20Advancements%2C)), whereas Sandstone might lag slightly on the newest MC features until updated. Also, being a beta, Sandstone might have a few more bugs or missing pieces. In terms of performance of the generator, both Kotlin and Node are plenty fast for generating datapacks, so that’s not a big concern. Readability is subjective – some will find Kotlin DSL more readable, others will prefer the JavaScript-like style of Sandstone. 

One notable difference is that Kore, by virtue of Kotlin’s static typing and null-safety, catches errors at compile time that TypeScript might only catch at runtime or not at all (if using JS). For example, if you mistype an item name in Kore, the code won’t compile because `Items.DIAMOND_SWORD` is a defined constant whereas a typo `Items.DIAMOND_SWROD` would be an error. In Sandstone, if you pass a wrong string where an item is expected, you might only realize in-game when the function fails. That said, TypeScript’s type definitions for Minecraft commands mitigate this to some extent by restricting values to known ones (they likely have enums or string literal types for things like effect names, etc.). 

In terms of **maintainability and extensibility**, both frameworks allow you to break your code into multiple files/modules and reuse logic. Sandstone uses the flexibility of JS/TS for extension – you can monkey-patch or contribute to its GitHub if needed. Kore uses Kotlin’s extension functions or open APIs to let you add new DSL features. Sandstone might be easier to hack for those without Java/Kotlin experience, since JavaScript is widely known. On the other hand, complex codebases in JavaScript can become hard to maintain if not careful, whereas Kotlin’s structure can impose more discipline. It really depends on the developer’s comfort zone.

In summary, **Sandstone** is an excellent choice if you’re entrenched in the JavaScript/TypeScript world. It offers a highly approachable way to script datapacks with a low barrier to entry and great quality-of-life features like command autocomplete. As the framework matures, it’s likely to incorporate even more features and optimizations. However, if you need something battle-tested today with every feature covered, or if you prefer the reliability of a statically-typed, compiled language, **Kore might be the better fit**. Many datapack creators actually experiment with both – you might try out Sandstone for a smaller project, and use Kore for a bigger one that demands its full-featured approach. Both aim to save you from the pain of raw commands, and both succeed in making datapack development more fun and productive.

### Example

```typescript
// Example of creating a function with an if/else condition.

import { condition, execute, functionCmd, raw, say, _, MCFunction } from "sandstone"; // Import the necessary functions from Sandstone.

MCFunction("myFirstFunction", () => { // Create a function named "myFirstFunction".
    _.if(condition.entity("@s[tag=has_item]"), () => { // If the player has the tag "has_item"...
        say("You have the item!"); // ... display this message.
    }).else(() => { // Otherwise...
        say("You don't have the item."); // ... display this other message.
    })
}

// execute.as('@a').at('@s').run(()=> { // Execute the following function as all players, at their position.  Not strictly necessary, but a good practice.
//    myFirstFunction()
//})
```

*(For more on Sandstone, check out the [Sandstone documentation](https://sandstone.dev) and GitHub repo. The community Discords (like the Minecraft Commands Discord) often have discussions on using Sandstone effectively, as it’s a rising star in the datapack scene.)*

## Beet – Python Toolkit and Datapack Development Kit

Moving on from Kotlin and TypeScript, we come to **Beet**, a powerful toolkit for creating datapacks (and resource packs) using **Python**. Beet takes a slightly different approach compared to Kore and Sandstone – it’s often described not just as a library, but as a *"Minecraft pack development kit"* ([GitHub - mcbeet/beet: The Minecraft pack development kit.](https://github.com/mcbeet/beet#:~:text=The%20Minecraft%20pack%20development%20kit)). Beet tries to **unify the tooling** around datapack creation by providing a single pipeline in which you can generate, transform, and assemble packs ([Beet documentation](https://mcbeet.dev/index.html#:~:text=Beet%20documentation%20The%20beet%20project,tooling%20into%20a%20single%20pipeline)). In essence, Beet acts as a build system specialized for Minecraft content, where Python is used as the scripting language to orchestrate everything.

**What Beet Is:** At its core, Beet is a **CLI tool and library** that can be extended with plugins. You write a configuration (in a `pyproject.toml` or using a Python script) that tells Beet what to do – this can include reading in function files, applying transformations, and outputting a datapack. Because Beet is so flexible, it can work as a foundation for many different workflows. Some people use Beet to run **preprocessors** on their code: for example, you could write your own mini-language or use one of Beet’s existing plugins to support a language, and Beet will compile it to mcfunction files. Others use Beet as a way to manage large mixed projects – it can handle resource pack assets, datapack functions, loot tables, etc., all in one go ([General overview - Beet documentation](https://mcbeet.dev/overview/#:~:text=Data%20packs%20and%20resource%20packs)) ([General overview - Beet documentation](https://mcbeet.dev/overview/#:~:text=from%20beet%20import%20DataPack)). It treats a datapack kind of like an object model (namespaces, files, etc.) which you can manipulate with Python code before writing the final result, giving a high degree of control ([General overview - Beet documentation](https://mcbeet.dev/overview/#:~:text=Data%20packs%20and%20resource%20packs)) ([General overview - Beet documentation](https://mcbeet.dev/overview/#:~:text=from%20beet%20import%20DataPack)).

One way to think of Beet is like **a fusion of a build tool (e.g., Make/Gradle) and a library**. It doesn’t impose a new language (you still use Python), but it provides an API and structure to generate pack files. For example, using Beet’s Python API, you can do something like: 

```python
from beet import DataPack, Function

pack = DataPack()
pack["demo"][Function]["hello"] = Function(["say Hello from Beet"])
pack.save(".")  # saves the datapack to current directory
``` 

This snippet programmatically creates a datapack with one namespace "demo" and one function file containing a say command ([General overview - Beet documentation](https://mcbeet.dev/overview/#:~:text=from%20beet%20import%20DataPack)) ([General overview - Beet documentation](https://mcbeet.dev/overview/#:~:text=associated%20file%20instance)). Of course, in practice you would leverage more advanced features – often Beet users write Python *plugins* that automate things.

**Plugins and Extensions:** Beet’s true power comes from its plugin system. There is a growing ecosystem of Beet plugins (sometimes collectively referred to as the **mcbeet** ecosystem) that add functionality like compiling other languages or performing common tasks. For instance, **Bolt** is a term you might hear – Bolt is a preprocessor that works with Beet to add a more convenient syntax for commands (it’s covered in some Smithed talks about speeding up datapack coding). Another example is a plugin that can read a spreadsheet or JSON and generate functions from it – with Beet, you can incorporate such steps easily. Essentially, Beet acts as the **glue** that lets all these tools work together. This addresses a problem noted in the community: many tools have different strengths, but using them together was hard; Beet tries to allow interoperability by being that one pipeline ([Rationale - Beet documentation](https://mcbeet.dev/rationale/#:~:text=It%27s%20also%20important%20to%20understand,would%20provide%20the%20ideal%20workflow)).

Because of this plugin architecture, Beet is very **extensible and flexible**. If you have a unique need, you can write a Python function to do it and hook it into the pack generation process. The Python language is an asset here: it’s easy to write quick scripts in Python, and there’s a vast number of libraries available. Need to do some math or algorithm to generate a structure? Python has libraries for days. Want to fetch data from a web API to include in your pack (imagine pulling real-world weather data into Minecraft via a datapack) – Python makes that feasible within Beet’s pipeline. In short, if you can imagine some automation or generation task, Beet gives you the canvas to implement it, whereas more specialized frameworks might not support that out of the box.

**Usage and Popularity:** Beet is especially popular among more advanced datapack creators and those with programming backgrounds. It might have a steeper learning curve for someone who just wants a straightforward "write code, get pack" experience, since you have to set up the Beet environment and perhaps configure plugins. However, the effort pays off for large projects. Notably, some well-known community projects have used Beet. For example, the Gamemode 4 team (a group that creates modular datapacks) has experimented with Beet to generate parts of their packs ([Gamemode 4 Toolbox - New Beet Development Pipeline - YouTube](https://www.youtube.com/watch?v=YBHn1oLarZs#:~:text=YouTube%20www,with%20python%21%20Checkout%20Beet)). And at the Smithed community (which focuses on datapack development tools), there have been panels like *"Supercharging Datapacks with Beet"* and *"Generating Datapacks with Python"* – indicating that creators are actively sharing knowledge on using Beet effectively. One showcase was a Note Block Studio datapack generation, where *"the pack generation is powered by beet, a powerful toolkit that serves as an authoring tool for data packs using the Python programming language"* ([Note Block Studio data pack + resource pack for Smithed Summit 2024](https://github.com/OpenNBS/SmithedSummit24#:~:text=2024%20github,the%20Python%20programming%20language)). This demonstrates Beet’s capability to handle even music or complex data-driven packs by automating the creation of many commands.

When comparing **Beet to Kore (and others)**, the differences are clear: Kore and Sandstone give you a higher-level, opinionated DSL to write your content, while Beet gives you a lower-level but highly flexible foundation to build your own DSL or process. If Kore is like getting a fully built sports car, Beet is like getting a configurable engine that you can put in any vehicle you design. Beet doesn’t enforce how you write an if-statement or loop – you’d either write it in Python (and have it generate multiple commands) or use a plugin that provides that functionality. This means **Beet requires you to think more like a programmer/builder**, whereas Kore/Sandstone let you think more like a Minecraft scripter using familiar constructs directly. For some, Beet’s approach is incredibly empowering; for others, it might be overkill if they just want to quickly code some functions.

**Maintenance:** In terms of maintainability, Beet-based projects can be very maintainable, but it largely depends on how clean your Python code and plugin usage is. Since Beet is not a singular DSL but rather a collection of tools, you as the developer have to enforce structure and organization. It’s possible (though not necessary) to end up mixing a lot of concerns in a single Python script if not careful. By contrast, a framework like Kore encourages a certain structure from the start (datapack -> namespace -> function definitions, etc., in Kotlin). That said, you can absolutely write well-structured Beet projects and even unit test your generation logic if needed. It’s just a different paradigm.

**When to Use Beet:** If you have a project that requires capabilities beyond just writing standard functions – say you want to procedurally generate content, or you want to integrate pack building into a larger software project – Beet is a fantastic choice. Also, if you’re already comfortable in Python, you’ll find Beet quite nice to work with. Python’s simple syntax can make the code for generating packs very concise. On the flip side, if you primarily want to write straightforward gameplay logic (commands reacting to events, etc.) and don’t need the extra bells and whistles, a DSL like Kore might get you to the result faster with less setup. 

Many advanced creators actually use Beet in conjunction with other languages: e.g., you might use a language like JMC or MCScript (which we’ll discuss next) to write the logic in a nicer syntax, and then use Beet to manage compiling that and merging with other pack data. This again shows Beet’s strength as a unifier rather than a competitor to those languages.

In summary, **Beet** stands out as the go-to **Python datapack generator** and build system. It emphasizes extensibility and integration, allowing you to tailor the tool to your project’s needs. It’s widely respected in the community and has an active development team (the mcbeet GitHub org). For those who love Python or have complex generation needs, Beet can significantly *supercharge* your datapack development process.

### Example

```python
# Example of creating a simple datapack with Beet.

from beet import Context, DataPack, Function # Import the necessary classes from Beet.

def my_datapack(ctx: Context):  # Define a function that takes a Beet context.

    ctx.data["my_namespace"] = DataPack() # Initialise the namespace
    ctx.data["my_namespace"]["my_function"] = Function(["say Hello from Beet!"]) # Add a function to the datapack.
    # Alternatively:
    # ctx.data.functions["my_namespace:my_function"] = Function(["say Hello from Beet!"])
    ctx.project_name = "MyBeetDatapack"
    ctx.project_description = "A simple datapack created with Beet."


# To run this code, you would need a configuration file `beet.json`,
# or use the default configuration, and run `beet build` in the terminal.

# beet.json (basic example)
"""
{
  "name": "My First Pack",
  "description": "Learning beet!",

  "pipeline": [
    "my_script.my_datapack"
  ],

  "output": "build"
}
```

*(To learn more about Beet, visit the [official Beet documentation](https://mcbeet.dev) which covers its architecture and lists available plugins. There’s also a dedicated Beet Discord for support ([General overview - Beet documentation](https://mcbeet.dev/overview/#:~:text=,Beet%20Discord)), reflecting a growing community around this toolkit.)*

## JMC – JavaScript-Like Language for Datapacks

Rounding out our list of popular generators is **JMC**, which stands for *"JavaScript-like Minecraft Function"*. JMC is a custom language and compiler created by WingedSeal that allows you to write Minecraft functions in a more familiar, high-level syntax (reminiscent of JavaScript) and then compiles them into standard datapack functions. In essence, **JMC is a language extension for mcfunction** – it adds programming constructs that make your function files more powerful and easier to manage, while ultimately producing vanilla-compatible output. As WingedSeal describes it, *"JMC is a mcfunction extension language for making Minecraft Datapacks"* ([GitHub - WingedSeal/jmc: A compiler for JMC (JavaScript-like Minecraft Function), a mcfunction extension language for making Minecraft Datapack](https://github.com/WingedSeal/jmc#:~:text=%28JavaScript)). If you’re comfortable with JavaScript or C-style languages, JMC’s syntax will feel quite natural.

**Why JMC Exists:** Minecraft’s `.mcfunction` files are essentially scripts of commands, but they lack things like variables, loops, or conditional statements (beyond what you hack together with scoreboards and `execute` ifs). This can make complex logic difficult to write and even harder to read. JMC addresses this by allowing you to write your datapack logic in a single `.jmc` file with higher-level constructs, then handle translating that to multiple mcfunction files, scoreboard operations, etc. The key goal is improved **readability and writability**: *"JMC allows you to write Minecraft functions in a better language (.jmc) which is more readable and easier to write."* ([GitHub - WingedSeal/jmc: A compiler for JMC (JavaScript-like Minecraft Function), a mcfunction extension language for making Minecraft Datapack](https://github.com/WingedSeal/jmc#:~:text=)) By using JMC, you could, for example, write a `for` loop to iterate over a list of numbers, or use a `if ... else` block to conditionally execute commands, and the compiler will expand these into the necessary low-level commands and calls in the output datapack.

**Features of JMC:** Some of the features JMC provides include: variables (so you can store values or entities in a variable instead of repeatedly writing selectors), arithmetic operations, if/else conditions, loops, and function inlining. It basically feels like writing a simple program. JMC’s syntax is quite straightforward. For instance, you might write something like:

```javascript
var count = 0;
while (count < 5) {
    summon ArmorStand ~ ~ ~ {CustomName:"LoopTest"};
    count++;
}
```

This is not actual JavaScript running at runtime in Minecraft, but JMC would compile this into a series of function files and scoreboard commands that achieve the same result: summoning 5 armor stands in a loop. The variable `count` would likely correspond to a scoreboard behind the scenes. The benefit to you as the pack developer is that you didn’t have to manually set up that scoreboard and write multiple functions for the loop – JMC’s compiler did it for you.

Another handy aspect is **function organization**. In vanilla, if you want to reuse code, you have to make separate functions and call them. In JMC, you could define a function or just write code that gets reused inline. The compiler takes care of splitting your code into multiple mcfunction files as needed (since each will have the 20Hz limit and such). JMC essentially does a lot of the repetitive work automatically.

**Integration with Other Tools:** JMC is typically used by writing .jmc files and then running the JMC compiler to produce a datapack. It can be used as a standalone tool – you don’t necessarily need to integrate it with something like Beet, though you can. Some developers use JMC alongside frameworks: for instance, you might have a Kore project, but for one particularly complex function, you write it in JMC and then include the generated output in your pack. However, many use JMC on its own to manage entire datapacks (especially those who prefer a lighter weight solution than a full framework). It’s particularly attractive to those who know a bit of programming but don’t want to dive into setting up Kotlin or dealing with Node.js. With JMC, you just need the JMC compiler (a Java program, since it’s written in Java) and your text editor.

**Comparison:** Comparing JMC to Kore/Sandstone/Beet is interesting because JMC operates at a slightly different level. Kore and Sandstone are frameworks that include their own API for items, blocks, etc., whereas JMC works at the command level (it doesn’t come with an API for game concepts, it assumes you write commands, just in a smarter way). So JMC won’t automatically give you constants for item names or methods for creating advancements – you still write those as commands (or JSON data). What it gives you is the convenience of programming structures to manage the flow of those commands. In a way, JMC could be seen as complementary to something like Beet (and indeed, Beet could run JMC as a plugin). But it’s also perfectly fine on its own for making packs. 

**Strengths:** The strength of JMC lies in its **simplicity and focus**. It does one thing – making mcfunctions easier to write – and does it well. It’s maintained by a dedicated developer (WingedSeal) and has a community of users on the Minecraft Commands Discord providing feedback (you can see on the GitHub that it has an active issue tracker with suggestions). If you appreciate writing in a language that looks like JavaScript but want it to turn into a working datapack, JMC is ideal. It’s also a great introduction to the concept of compiled datapack languages: if someone finds Kore or Beet overwhelming, they might start with JMC to get a taste of generated packs.

**Weaknesses:** Since JMC is focused on just the function logic, you’ll still handle things like resources, advancements, loot tables by other means (hand-written or other generators). It’s not a full framework, so it’s less *complete* than Kore in terms of covering all datapack file types. Also, because JMC is its own language, you need to learn its syntax and nuances (which, while similar to JS, has its own rules and limitations given it must compile to vanilla mechanics). Some advanced patterns might not be easily expressible in JMC if they require very specific command interactions. In such cases, you might drop down to raw mcfunction or use a different tool. Performance of the generated pack is usually fine, but if you misuse the language (like creating extremely large unrolled loops), you might end up with lots of commands – basically, JMC won’t magically make things super efficient; it just makes them easier to write correctly.

When choosing between JMC and something like Kore, consider the scope of your project. For a small-medium datapack that mostly revolves around command logic and you want quick development, JMC is a strong candidate. For a huge project or one that also needs to programmatically generate lots of JSON files or integrate with external data, a more full-featured framework might serve you better.

**JMC vs Sandstone vs Beet quick take:** JMC is to *programming* what Sandstone is to *scripting* and Beet is to *building*. JMC gives you programming structures (compiled to script), Sandstone gives you a scripting environment with some programming feel (interpreted by the framework at build time), and Beet gives you a toolbox to build whatever. It’s great that the community has all these options, as it caters to different preferences.

### Example

```javascript
// Example of a JMC function with a variable and a loop.

function my_jmc_function() {
    $counter = 0; // Define a variable (which will be a scoreboard score).

    while ($counter < 5) { // Loop that executes as long as the counter is less than 5.
        Text.tellraw(@a, "Loop iteration: &<blue>$counter"); // Display a message with the counter value.
        $counter++; // Increment the counter.
    }
}

// function load()
//{
//	tellraw(@a, "JMC datapack loaded");
//}
```

*(If you’re interested in JMC, see the [JMC GitHub](https://github.com/WingedSeal/jmc) and WingedSeal’s site for documentation. There are also examples on the repo that show how .jmc code translates to vanilla functions, which can help you understand the compilation process.)*

## Conclusion – Choosing the Right Tool and the Case for Kore

As we’ve seen, Minecraft datapack creators in 2025 are spoiled for choice when it comes to generation tools. We covered **Kore, Sandstone, Beet,** and **JMC**, each of which brings something unique to the table:

- **Kore** – A Kotlin-based DSL that offers completeness and strong typing, making large datapack projects more manageable and less error-prone. It excels in readability and maintainability, effectively turning datapack writing into a software engineering task (with all the benefits of modern IDEs and languages). If you want a solution that "just works" for any feature Minecraft throws at you and prefer a robust, structured approach, Kore is a top pick.

- **Sandstone** – A TypeScript framework that provides a familiar environment for web developers. With autocompletion and flexible code organization, Sandstone makes writing datapacks feel like writing a web app. It’s rapidly improving and is a great choice for those who love JavaScript/TypeScript. Just keep in mind it’s still in beta, so you might hit a snag or two, but the community support is there.

- **Beet** – A Python toolkit/development kit that is extremely powerful for those who need it. Beet shines for complex or large-scale automation of pack content. It might require more setup and Python knowledge, but it rewards you with unparalleled flexibility. For ambitious projects that push the boundaries of what datapacks can do (or how they’re made), Beet is often the secret sauce behind the scenes.

- **JMC** – A focused, high-level language that makes writing functions easier by introducing classic programming structures. It’s simple to adopt and can drastically reduce the headache of writing complicated command sequences. JMC doesn’t cover everything in a datapack, but what it does, it does very well. It’s perfect for those who want a lightweight solution to tame their command block (or rather, function file) logic.

In practice, many developers mix and match these tools. The ecosystem is not one-size-fits-all, and as the **Minecraft Commands Community (MCC)** often says, *"use the right tool for the job"*. That said, you also can’t go wrong picking one and sticking with it, especially for learning purposes. Each of these generators has documentation and community examples to help you get started.

So, **why put Kore first** in this comparison? Simply put, Kore offers a **balance of power and simplicity** that is hard to match. It provides the high-level comfort that JMC and Sandstone aim for, while also giving the extensibility and completeness that you’d otherwise look to Beet for. By leveraging Kotlin, Kore benefits from a mature language ecosystem, and many developers find that once they start a project with Kore, they never feel the need to switch – Kore can handle it all. The fact that Kore can generate not just datapack files but even a mod Jar if you want to integrate with Fabric/Forge mods ([GitHub - Ayfri/Kore: A Kotlin library to generate Datapacks for Minecraft Java.](https://github.com/Ayfri/Kore#:~:text=,Advancements%2C%20Loot%20Tables%2C%20Recipes%2C)) points to its forward-thinking design. Its emphasis on clean code and maintainability means that even if your datapack grows into a huge, multi-system creation, you won’t be pulling your hair out trying to debug spaghetti functions or cryptic errors.

Another strength in Kore’s favor is its **community and maintenance**. As an open-source project driven by a passionate developer (Ayfri) and already having garnered interest (GitHub stars, Reddit discussion, etc.), Kore is under active development. It’s not a stagnant tool – it keeps up with Minecraft updates (notably supporting 1.20+ features quickly) and incorporates user feedback. This reliability is crucial; you don’t want to invest in a framework only to find it abandoned when the next Minecraft version arrives. Kore’s GPL license and open nature also ensure it will remain freely available and community-driven.

In conclusion, the **Minecraft datapack development landscape in 2025** is rich and evolving. **Kore** stands out as a flagship solution exemplifying **completeness, readability, maintainability, and extensibility**, thanks in large part to the advantages of Kotlin and thoughtful library design. Meanwhile, **Sandstone, Beet, and JMC** are prominent players each catering to different niches – be it TypeScript fans, Python power-users, or those who just want simpler function syntax. By exploring discussions on GitHub, the MCC Discord, and communities like Smithed, one can see that these tools are frequently mentioned and recommended, often with healthy debate about which is "best." The truth is, "best" depends on you and your project. But if you ask us for a recommendation to start with, we’d happily point you to **Kore** for a well-rounded, future-proof experience in datapack creation.

Whichever tool you choose, the fact that such generators exist means you can focus more on **what** you want to create in Minecraft, and less on the nitty-gritty of **how** to implement it. And that ultimately means more awesome datapacks and innovation in the community. Happy coding, and happy crafting!
