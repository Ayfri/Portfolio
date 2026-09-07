package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.max
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AppStyle
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.HeaderStyle
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.localImage
import io.github.ayfri.markdownParagraph
import io.github.ayfri.passiveListener
import io.github.ayfri.utils.gradientBorderBackground
import io.github.ayfri.utils.pageBackground
import io.github.ayfri.utils.size
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

data class AboutMeSection(
	val content: String,
	val date: Int,
	val image: Boolean = false,
	val id: String,
	/** Shown as a badge next to the title, where the timeline only ever shows the starting year. */
	val dateLabel: String = date.toString(),
	val tags: List<String> = emptyList(),
	val title: (@Composable AboutMeSection.() -> Unit),
) {
	var additionalContent: (@Composable () -> Unit)? = null
	var htmlElement: HTMLElement? = null

	@Composable
	fun Display(selected: Boolean = false) = Section({
		if (image) classes(AboutMeStyle.withImage)
		if (selected) classes("selected")
		id(id)

		ref {
			htmlElement = it
			onDispose {}
		}
	}) {
		if (image) title()
		else Div({
			classes(AboutMeStyle.header)
		}) {
			H2({
				classes(AboutMeStyle.textIcon)
			}) {
				title()
			}

			Span({
				classes(AboutMeStyle.dateBadge, AppStyle.monoFont)
			}) {
				Text(dateLabel)
			}
		}

		if (additionalContent != null) {
			Div({
				classes(AboutMeStyle.additionalContent)
			}) {
				additionalContent!!()
				P({
					markdownParagraph(content, true)
				})
			}
		} else {
			P({
				markdownParagraph(content, true)
			})
		}

		if (tags.isNotEmpty()) Div({
			classes(AboutMeStyle.tags)
		}) {
			tags.forEach { tag ->
				Span({ classes(AboutMeStyle.tag, AppStyle.monoFont) }) { Text(tag) }
			}
		}
	}
}

val sections = listOf(
	AboutMeSection(
		MAIN_PRESENTATION.trimIndent().trimIndent(), 2002, true, id = "me"
	) {
		Img(localImage("avatar@300x300.webp"), "avatar") {
			classes(AppStyle.avatar)
		}

		additionalContent = {
			H1 {
				Text("About me")
			}
		}
	},

	AboutMeSection(
		"""
			I started programming in 2014 by attending a conference about programming in a library.
			I first tried some beginner projects in Python with a [book](https://www.eyrolles.com/Informatique/Livre/python-pour-les-kids-9782212140880) that my dad bought me, did some beginner projects, etc.

			After that, I created random projects I found on YouTube, such as programming a Minecraft-like game in C++ or making some little games in Unity.
			However, I found myself mostly copying the tutorials without attempting to create projects on my own, and therefore didn't have a deep understanding of what I was doing.
		""".trimIndent(), 2014, id = "intro", tags = listOf("Python", "C++", "Unity")
	) {
		TextIcon("Introduction to programming", FontAwesomeType.SOLID, "computer")
	},

	AboutMeSection(
		"""
			In 2015, I created my first project with a Graphical User Interface using the [Processing](https://processing.org) software and programming language.
			I heard about this program from watching videos from [The Coding Train](https://www.youtube.com/c/TheCodingTrain). I created a lot of little experiments using this library and learned some interesting things about 3D graphics, how it works, how to optimise it, etc.
			My biggest 3D experiment was a [Minecraft-like](https://github.com/Ayfri/ProceCraft) game, and my most significant program with Processing was a tool to convert an [image into Minecraft Pixel-Art](https://github.com/Ayfri/Image2Minecraft) using only blocks from Minecraft as pixels.

			After some years of making Discord bots in [JavaScript](https://developer.mozilla.org/docs/Web/JavaScript), I heard that a library in JavaScript existed that was exactly like Processing, [p5.js](https://p5js.org) _(which was created by the same foundation)_, and used it for some little projects, even created a [port](https://github.com/Ayfri/TypeCraft) of the Minecraft-like game in [TypeScript](https://www.typescriptlang.org).
			But it was too simplistic and not expandable enough for me, so I looked for another library to make my own games with more depth and customization.

			[PIXI.js](https://pixijs.com) was the library I stumbled upon, and I created a few projects with it for a long time. My first big game with it was a [2D Minecraft-like game](https://github.com/Ayfri/2d-minecraft) (again yes hehe), then a [2D Portal game](https://github.com/Ayfri/portal-2d) experiment, and later a rewrite of that 2D Minecraft game in Kotlin, [Minekraft-2D](https://github.com/Ayfri/Minekraft-2D).
		""".trimIndent(), 2015, id = "processing", tags = listOf("Processing", "p5.js", "PIXI.js", "TypeScript")
	) {
		TextIcon("Processing", "https://upload.wikimedia.org/wikipedia/commons/c/cb/Processing_2021_logo.svg")
	},

	AboutMeSection(
		"""
			After creating my own [Discord server](https://discord.gg/invite/BySjRNQ9Je) for my community from [YouTube](https://www.youtube.com/c/Ayfri), I wanted to create a Discord bot. So I created one in [JavaScript](https://developer.mozilla.org/docs/Web/JavaScript) using [Node.JS](https://nodejs.org) in early 2018 by following tutorials, and CommunAyBot was born.
			Back in the days, it was not that common to create a bot for your own community, and Discord was not so reputed. Because of that, there were no great tutorials. I got a lot of help from a Discord server named Obelia Dev _(which doesn't even exist today)_, my friends Ghom, Loockeeer, Felons, and some others helped me a lot to understand and create my bot.

			About a year later, I was pretty good at creating a bot, but I realized that my project was kind of... ugly. So I recreated it, [AyBot 2](https://github.com/Ayfri/AyBot-2) was born in early 2019. It was pretty clean, and I wanted people to use my bot, so I changed a lot of the code, so it was working on multiple servers with configurations, etc.

			At the same time I was, and still am, an administrator on a [big Discord server](https://discord.gg/invite/sDT7W8mNmq) about sharing your server or website, together with another administrator, Antow, who had his own bot. We were both passionate about astronomy, so we merged our two bots into [Galileo](https://github.com/Galileo-Bot/galileo).
			That was the end of this era for me: after about two years of writing almost nothing but Discord bots, I ran out of motivation for them and moved on to other things.
		""".trimIndent(), 2018, id = "second-intro", dateLabel = "2018 - 2019", tags = listOf("JavaScript", "Node.js", "Discord API")
	) {
		TextIcon("Discord bots", FontAwesomeType.BRAND, "discord", Color("#5865f2"))
	},

	AboutMeSection(
		"""
			At my high school, for 3 years, I used Arduino a lot, only creating big projects in the last year but still. It let me learn a lot about low-level programming, [C++](https://cplusplus.com) basics and microcontrollers.
			By doing my own research, I learned a lot about C++ compilation, assembler, reverse engineering, optimization and other low-level subjects. I even tried writing a bit of x86 assembler.

			Back in 2015, I followed a [tutorial to create a Minecraft-like game](https://www.youtube.com/watch?v=GACpZp8oquU) in C++ using [OpenGL](https://www.opengl.org/) and a bunch of libraries.
			Coming back to that project years later, fixing its issues and finally understanding the code taught me a lot about OpenGL and low-level graphics processing.
		""".trimIndent(), 2018, id = "arduino", tags = listOf("C++", "Arduino", "OpenGL", "x86 ASM")
	) {
		TextIcon("Arduino", localImage("arduino.svg"))
	},

	AboutMeSection(
		"""
			[Minecraft](https://minecraft.net) is my favorite game of all time, I started playing it back in 2010, I know it by heart.
			I heard about it randomly on an old computer blog my dad was subscribed to, where an article spoke about _"this new game with an interesting concept"_.

			Since I own the game, I played hundreds of hours in Minecraft, created numerous maps, technical maps using command blocks, datapacks and mods. I have been creating mods since around 2018, in [Java](https://www.java.com) with [Forge](https://files.minecraftforge.net/net/minecraftforge/forge), with [Fabric](https://fabricmc.net) since 2020 and in [Kotlin](https://kotlinlang.org) since 2021.

			I know a lot about the concepts of Minecraft and how they are programmed, and this is what motivated me to create a Minecraft-like game _4 times_, getting closer to what I want and to the performance such a game needs each time.
		""".trimIndent(), 2018, id = "minecraft", tags = listOf("Java", "Kotlin", "Fabric", "Forge")
	) {
		TextIcon("Minecraft", localImage("minecraft.avif"))
	},

	AboutMeSection(
		"""
			After graduating from high school, I got accepted into a private computer science school named [Ynov](https://ynov.com), on the [Aix-en-Provence](https://www.ynov.com/campus/aix-en-provence) campus, and I graduated from its [Data Scientist](https://www.ynov.com/metiers/data-scientist) Master's programme in summer 2026.

			The first years covered [GoLang](https://go.dev), [Python](https://www.python.org), pretty complex [C++](https://cplusplus.com) and OOP, network basics, [REST APIs](https://wikipedia.org/wiki/Representational_state_transfer), front-end work and databases with [MySQL](https://www.mysql.com), through a lot of projects: little training programs, websites with and without an API, and an entire forum. I greatly appreciated GoLang's GoHTML templates, C++, and my teacher, who I discussed a lot with.
			The last years went towards data and AI: statistics, machine learning, data engineering, and a research thesis to finish the degree.

			The school also pushed me into things I would not have done alone: team projects with real deadlines, two internships, and the research project that ended up shaping my career.
		""".trimIndent(), 2021, id = "ynov", dateLabel = "2021 - 2026", tags = listOf("Go", "Python", "C++", "MySQL", "Machine Learning")
	) {
		TextIcon("Post-Bac and Ynov", localImage("ynov-icon.avif"))
	},

	AboutMeSection(
		"""
			My school required a 6-week internship in any company to get a first professional experience.
			I got in contact with [BlueFrog](https://www.bluefrog.fr/index.html), a company creating websites, and after some interviews got accepted!

			I learned [PHP](https://www.php.net) and [WordPress](https://wordpress.org) and created a few websites and plugins during those two months. It was a great first look at the difference between a personal project and a real one, and at a developer's everyday job.
		""".trimIndent(), 2022, id = "first-internship", dateLabel = "Summer 2022", tags = listOf("PHP", "WordPress", "CSS")
	) {
		TextIcon("BlueFrog", "https://www.bluefrog.fr/images/logo.png")
	},

	AboutMeSection(
		"""
			In November 2022, during my second year at Ynov, I started a Kotlin library to generate Minecraft datapacks without writing a single JSON or `mcfunction` file by hand.
			It was named Datapack-DSL back then, it is [Kore](https://kore.ayfri.com) today, and it is the project I have maintained the longest.

			The core DSL covers every command with all its subcommands, selectors, NBT tags, chat components, and every data-driven file of the game: advancements, loot tables, recipes, world generation, plus the lists of all registries.
			Around it, the library is split into modules published on [Maven Central](https://central.sonatype.com/artifact/io.github.ayfri.kore/kore): `oop` for gameplay abstractions like teams, boss bars, scoreboards, timers and spawners, `helpers` for raycasts, text renderers, scoreboard math and VFX, and `bindings` to import an existing datapack and generate type-safe Kotlin from it.

			The project grew way past the library itself: a Gradle plugin, a KSP processor, a [project template](https://github.com/Kore-Minecraft/Kore-Template), a documentation website built with [Kobweb](https://kobweb.varabyte.com) _(the same framework as this portfolio)_, LLM-friendly documentation and a [skills pack](https://github.com/Kore-Minecraft/Kore-Skill) so AI agents write correct Kore code, and the ability to generate a datapack as a mod for Fabric or NeoForge.

			It also stopped being a solo project: Kore has its own [#kore channel](https://kotlinlang.slack.com/archives/C066G9BF66A) on the Kotlin Slack, outside contributors, and people publishing their own libraries and datapacks built on top of it.
		""".trimIndent(), 2022, id = "kore", dateLabel = "Since 2022", tags = listOf("Kotlin", "DSL", "Gradle", "KSP", "Maven Central")
	) {
		TextIcon("Kore", localImage("logos/kore.webp"))
	},

	AboutMeSection(
		"""
			Ynov added a project system named YBoosts at the end of 2022, where students from the first and second years build a project in a team of 7 or more.
			We created a team of 7 named Defensive Realms and made [Cat'aClysm: Claw Of The Dead](https://github.com/Cat-aclsym/Cat-aclsym_Claw_of_the_dead), a 2D tower defense where cats hold a city against a horde of zombies, using [Godot](https://godotengine.org) 4.
			It was my first experience with Godot, and I still prefer it over Unity by a wide margin. We drew our own sprites and animations, and finished a first alpha at the end of May 2023.

			Then we threw that codebase away. In October 2023 we restarted the game from scratch, with a bigger team _(around a dozen people over time)_ and a much larger scope. Everything is data-driven now: levels, waves, towers, traps and enemies are described in JSON files, so the game can be balanced without touching the code.

			It currently has 8 levels split into two story arcs, towers and traps with their own upgrade paths, 17 optional per-level challenges, an armory progression tree unlocked with stars, a dialogue system, English and French localisation, and an in-game debug console with its own commands.
			It is by far the biggest team project I have worked on, and the one that taught me the most about keeping a codebase readable for people who are not me.
		""".trimIndent(), 2023, id = "cat-aclysm", dateLabel = "Since 2023", tags = listOf("Godot 4", "GDScript", "Team project")
	) {
		TextIcon("Cat'aClysm: Claw Of The Dead", localImage("logos/cataclysm.webp"))
	},

	AboutMeSection(
		"""
			[PokéCards-Collector](https://pokecards-collector.ayfri.com) started in October 2023 with friends: browse the entire Pokémon Trading Card Game catalogue, English and Japanese, and keep track of the cards you own and the ones you are still hunting.

			It runs on [SvelteKit](https://svelte.dev/docs/kit) with [Svelte 5](https://svelte.dev/docs/svelte/what-are-runes) runes, [Tailwind CSS](https://tailwindcss.com) 4 and TypeScript, served from a [Supabase](https://supabase.com) Postgres database on [Cloudflare Workers](https://workers.cloudflare.com).
			Cards, sets and prices come from [TCGdex](https://tcgdex.dev) and Pokédex entries from [PokéAPI](https://pokeapi.co), pulled by a scraper CLI and refreshed every week by a Cloudflare Workflow.

			On top of the card browser and the collection, there are digital binder pages you can export as an image, pages by artist, set and Pokémon, public profiles, and two daily games: guess the card of the day, or guess its market price.
		""".trimIndent(), 2023, id = "pokecards", dateLabel = "Since 2023", tags = listOf("SvelteKit", "Svelte 5", "Supabase", "Cloudflare")
	) {
		TextIcon("PokéCards-Collector", localImage("logos/pokecards.webp"))
	},

	AboutMeSection(
		"""
			AI became impossible to ignore, and I wanted to build with it rather than read about it.
			In June 2023 I made [Artificial-Infiltration](https://github.com/Ayfri/Artificial-Infiltration), a small game in [Kotlin](https://kotlinlang.org) with [Compose for Desktop](https://www.jetbrains.com/lp/compose-multiplatform/) and the GPT API: you chat in a room with five other people, one of them is the AI, and you have to find which one.

			The same summer, I did an internship at Ynov organised by the Data Engineering teacher, researching [ChatGPT](https://chatgpt.com), autonomous agents and ways to expand a model's context. That research became ScriptGraf, a tool that writes a company's social media posts on its own.

			That prototype became a company. ScriptGraf is now [Link2Brain](https://www.link2brain.com/), a Marseille startup selling the finished product, live with paying clients, and I joined it in September 2025 as lead full-stack and AI developer.
			Most of my work is [Python](https://www.python.org) on the AI side, building agents with [RAG](https://en.wikipedia.org/wiki/Retrieval-augmented_generation) and state-of-the-art models that generate text, images and short videos, plus the API and database layers, the [Vue](https://vuejs.org) and [Nuxt](https://nuxt.com) front-end, and the [Docker](https://www.docker.com) and [Dokploy](https://docs.dokploy.com) deployments.
			Taking a school prototype I saw at step zero all the way to real marketing teams using it every day is the part I value most.
		""".trimIndent(), 2023, id = "link2brain", dateLabel = "Since 2023", tags = listOf("Python", "RAG", "Nuxt", "Docker", "DevOps")
	) {
		TextIcon("ScriptGraf to Link2Brain", localImage("logos/link2brain.webp"))
	},

	AboutMeSection(
		"""
			[Atom Clicker](https://atom-clicker.ayfri.com) started in October 2024 as a small Cookie-Clicker-like about splitting atoms, and became one of my biggest side projects, now played by more than a thousand people.

			You click an atom, buy buildings that go from molecules up to cosmic structures, and climb a skill tree. Then come the prestige layers, each with its own currency, power-ups that stack, automation, achievements and daily quests.
			Two extra dimensions unlock later: a Photon Realm, and a Radiation Realm where you run a nuclear reactor and manage its control rods.

			It is written in [SvelteKit](https://svelte.dev/docs/kit) with Svelte 5 runes and TypeScript, drawn on a Canvas, with [Supabase](https://supabase.com) for accounts and the global leaderboard, and deployed on [Cloudflare Workers](https://workers.cloudflare.com).
			Incremental games are a fun constraint: enormous numbers, saves you must never corrupt, and an interface that has to stay smooth while everything on screen updates at once.
		""".trimIndent(), 2024, id = "atom-clicker", dateLabel = "Since 2024", tags = listOf("Svelte 5", "TypeScript", "Supabase", "Canvas")
	) {
		TextIcon("Atom Clicker", localImage("logos/atom-clicker.svg"))
	},

	AboutMeSection(
		"""
			[GPT Images](https://gpt-images.ayfri.com) came out of wanting OpenAI's image models without the product wrapped around them: you bring your own API key, it never leaves your browser, and you get prompts, reference images, batch generation, a gallery and usage tracking that tells you what a batch actually costs.

			Same stack as most of my recent side projects, [SvelteKit](https://svelte.dev/docs/kit), TypeScript and [Tailwind CSS](https://tailwindcss.com), with everything stored client-side in IndexedDB.
			These days a good part of my work also goes into making AI agents useful on my own projects, from the Kore skills pack to the tooling I use daily.
		""".trimIndent(), 2025, id = "gpt-images", tags = listOf("SvelteKit", "OpenAI API", "IndexedDB")
	) {
		TextIcon("GPT Images", localImage("logos/gpt-images.webp"))
	})

const val TIMELINE_DEFAULT_OFFSET = 125.0

@Page("/about-me/index")
@Composable
fun AboutMe() {
	PageLayout(
		"About Me",
		description = "The story of Pierre Roy (Ayfri): from first steps in Python in 2014 to Kotlin, AI, and Minecraft development today.",
		keywords = "Pierre Roy story, Ayfri biography, self-taught developer, programming journey, full-stack developer France, Minecraft developer",
	) {
		Style(AboutMeStyle)

		var roundSelected by remember { mutableStateOf(0) }

		// A single passive listener drives the selected round; registering it from the composition (as attrs or bare
		// calls) leaked a new listener on every scroll-triggered recomposition.
		DisposableEffect(Unit) {
			val onScroll = EventListener {
				sections.forEachIndexed { index, section ->
					if (index == roundSelected) return@forEachIndexed
					val element = document.querySelector("#${section.id}") ?: return@forEachIndexed
					val elementOffset = element.asDynamic().offsetTop as Double - TIMELINE_DEFAULT_OFFSET * 2
					val elementHeight = element.asDynamic().offsetHeight as Double

					if (window.scrollY in elementOffset..(elementOffset + elementHeight)) roundSelected = index
				}
			}

			window.addEventListener("scroll", onScroll, passiveListener)
			window.addEventListener("resize", onScroll, passiveListener)
			onScroll.handleEvent(Event("scroll"))

			onDispose {
				window.removeEventListener("scroll", onScroll)
				window.removeEventListener("resize", onScroll)
			}
		}

		Div({
			classes(AboutMeStyle.layout)
		}) {
			// Sticky instead of scroll-positioned: the browser clamps the timeline to the bottom of the section list
			// on its own, where the old JS offset kept dragging it down into the footer.
			Aside({
				classes(AboutMeStyle.timeline)
			}) {
				sections.forEachIndexed { index, section ->
					if (index > 0) {
						Div({
							classes("separator")
						})
					}

					Div({
						attr("data-date", section.date.toString())
						classes("round")
						if (index == roundSelected) classes("selected")

						onClick {
							window.location.hash = "#${section.id}"
						}
					}) {
						A(href = "#${section.id}")
					}
				}
			}

			Div({
				classes(AboutMeStyle.content)
			}) {
				sections.forEachIndexed { index, it -> it.Display(index == roundSelected) }
			}
		}
	}
}

@Composable
fun TextIcon(text: String, icon: String) {
	Img(icon, "icon")
	Text(text)
}

@Composable
fun TextIcon(text: String, fontAwesomeType: FontAwesomeType, icon: String, color: CSSColorValue? = null) {
	io.github.ayfri.components.I(fontAwesomeType, icon) {
		color?.let { color(it) }
	}
	Text(text)
}

object AboutMeStyle : StyleSheet() {
	const val BACKGROUND_SECTION_ODD_COLOR = "#1A1225"
	const val BACKGROUND_SECTION_EVEN_COLOR = "#1E1535"
	const val TIMELINE_BG_GRADIANT_END_COLOR = "#FF0080"
	const val TIMELINE_BG_GRADIANT_START_COLOR = "#00D4FF"

	val timelineSize by variable<CSSSizeValue<*>>()

	init {
		"html" {
			scrollPaddingTop(HeaderStyle.navbarHeight.value() + TIMELINE_DEFAULT_OFFSET.px)
		}

		id("main") style {
			timelineSize(max(6.cssRem, 8.vw))
			paddingBottom(2.cssRem)

			pageBackground()
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			id("main") style {
				timelineSize(3.5.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			id("main") style {
				timelineSize(0.px)
			}
		}

		"h1" {
			fontSize(3.cssRem)
			margin(0.px)

			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
			property("-moz-text-fill-color", "transparent")
			property("-moz-background-clip", "text")
			property("text-shadow", "0 0 20px rgba(0, 212, 255, 0.5)")
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	private fun generateKeyframe(
		fromOpacity: Double = 0.0,
		fromTransform: TransformBuilder.() -> Unit,
		toOpacity: Double = 1.0,
		toTransform: TransformBuilder.() -> Unit,
	) = keyframes {
		from {
			opacity(fromOpacity)
			transform(fromTransform)
		}

		to {
			opacity(toOpacity)
			transform(toTransform)
		}
	}

	val appearLeft by generateKeyframe(fromTransform = { translateX(3.cssRem) }) { translateX(0.px) }

	val appearBottom by generateKeyframe(fromTransform = {
		translateY((-2).cssRem)
		translateX((-50).percent)
	}) {
		translateY(0.px)
		translateX((-50).percent)
	}

	val appearTop by generateKeyframe(fromTransform = {
		translateY(2.cssRem)
		translateX((-50).percent)
	}) {
		translateY(0.px)
		translateX((-50).percent)
	}

	val sectionSelection by keyframes {
		from {
			backgroundPosition(BackgroundPosition.of(CSSPosition(200.percent, 0.percent)))
		}

		to {
			backgroundPosition(BackgroundPosition.of(CSSPosition(0.percent, 0.percent)))
		}
	}

	val layout by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		alignItems(AlignItems.Stretch)
		// Left padding keeps room for the year label rendered on the left side of the selected round.
		padding(0.px, 1.5.cssRem, 0.px, 1.cssRem)

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			self {
				padding(0.px)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val timeline by style {
		val thickness = 4.px
		val roundSize = 1.1.cssRem

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)

		position(Position.Sticky)
		property("align-self", "flex-start")
		top((HeaderStyle.navbarHeight.value() + 2.cssRem).unsafeCast<CSSLengthValue>())
		height(80.vh)
		width(timelineSize.value())
		flexShrink(0)

		child(self, universal) style {
			backgroundImage(linearGradient {
				add(Color(TIMELINE_BG_GRADIANT_START_COLOR))
				add(Color(TIMELINE_BG_GRADIANT_END_COLOR))
			})
			backgroundAttachment(BackgroundAttachment.Fixed)
			property("box-shadow", "0 0 15px rgba(0, 212, 255, 0.4)")
		}

		className("round") style {
			size(roundSize)
			borderRadius(10.cssRem)
			position(Position.Relative)
			flexShrink(0)

			transitions {
				defaultDelay(.4.s)
				defaultTimingFunction(AnimationTimingFunction.cubicBezier(.47, 2.0, .41, .8))
				properties("transform", "box-shadow")
			}

			group(hover(self), self + className("selected")) style {
				boxShadow(color = Color("#FF008080"), blurRadius = .8.cssRem)
				zIndex(3)
			}

			hover(self) style {
				transform { scale(1.15) }
				cursor(Cursor.Pointer)
			}

			self + className("selected") style {
				transform { scale(1.35) }
				border {
					color(Color("#00D4FF"))
					style(LineStyle.Solid)
					width(2.px)
				}
				property("box-shadow", "0 0 20px rgba(255, 0, 128, 0.6)")

				self + after style {
					content("attr(data-date)".unsafeCast<Content>())
					fontWeight(700)
					fontSize(.75.cssRem)

					// Anchored on the round's left edge instead of a fixed offset, so the year never spills out of the page.
					position(Position.Absolute)
					top(0.px)
					property("right", "calc(100% + .5rem)")
					height(100.percent)
					width(Width.MinContent)

					animation(appearLeft) {
						duration(.4.s)
						timingFunction(AnimationTimingFunction.cubicBezier(.47, 2.0, .41, .8))
					}
				}
			}
		}

		// The separators grow instead of having a fixed length, so the whole timeline always fits its sticky height,
		// however many sections the list has.
		className("separator") style {
			flex("1 1 0")
			minHeight(0.px)
			width(thickness)
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			className("round") + className("selected") + after style {
				property("right", "auto")
				left(50.percent)
				transform { translateX((-50).percent) }
				top(Top.Unset)
				bottom((-2.5).cssRem)

				padding(.2.cssRem, .4.cssRem)
				borderRadius(.5.cssRem)
				border {
					width(1.px)
					style(LineStyle.Solid)
					color(Color.transparent)
				}
				gradientBorderBackground(Color("#00000090"))

				animation(appearBottom) {
					duration(.3.s)
					timingFunction(AnimationTimingFunction.EaseInOut)
				}
			}

			className("round") + className("selected") + lastOfType + after style {
				bottom(1.cssRem)

				animation(appearTop) {
					duration(.3.s)
					timingFunction(AnimationTimingFunction.EaseInOut)
				}
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			style {
				display(DisplayStyle.None)
			}
		}
	}

	val withImage by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		alignItems(AlignItems.Center)
		gap(2.5.cssRem)

		media(mediaMaxWidth(768.px)) {
			self {
				flexDirection(FlexDirection.Column)
			}
		}
	}

	val additionalContent by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(2.cssRem)
	}

	val header by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.SpaceBetween)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
		marginBottom(1.5.cssRem)
	}

	val dateBadge by style {
		backgroundColor(Color("#FFFFFF10"))
		border(1.px, LineStyle.Solid, Color("#00D4FF40"))
		borderRadius(1.cssRem)
		color(Color("#FFFFFFCC"))
		fontSize(.8.cssRem)
		fontWeight(600)
		padding(.25.cssRem, .8.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val tags by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.4.cssRem)
		marginTop(1.25.cssRem)
	}

	val tag by style {
		backgroundColor(Color("#FFFFFF12"))
		borderRadius(1.cssRem)
		color(Color("#FFFFFFDD"))
		fontSize(.75.cssRem)
		padding(.25.cssRem, .7.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val content by style {
		val titleHeight by variable<CSSSizeValue<*>>()

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.5.cssRem)
		flex(1)
		minWidth(0.px)

		"section" {
			titleHeight(max(1.75.cssRem, 2.4.vw))

			fontFamily(AppStyle.MONO_FONT_FAMILY)
			padding(2.cssRem, max(2.cssRem, 3.vw))
			position(Position.Relative)
			borderRadius(1.cssRem)
			border {
				width(2.px)
				style(LineStyle.Solid)
				color(Color.transparent)
			}
			gradientBorderBackground(Color(BACKGROUND_SECTION_ODD_COLOR))
			property("box-shadow", "0 0 20px rgba(0, 212, 255, 0.1)")

			transitions {
				defaultDuration(.3.s)
				defaultTimingFunction(AnimationTimingFunction.EaseInOut)
				properties("transform", "box-shadow")
			}

			self + className("selected") style {
				val offset = 4.px
				transform {
					scaleX(1.005)
					translateX(-offset)
				}

				borderRadius(topLeft = 1.cssRem, bottomLeft = 1.cssRem, topRight = 1.cssRem, bottomRight = 1.cssRem)
				property("box-shadow", "0 0 30px rgba(255, 0, 128, 0.3)")

				overflow(Overflow.Hidden)
				animation(sectionSelection) {
					delay(.1.s)
					duration(.4.s)
					timingFunction(AnimationTimingFunction.EaseInOut)
				}

				backgroundImage(linearGradient(90.deg) {
					add(Color("#2A1B3D"))
					add(Color("#1A1225"))
					add(Color.transparent)
				})
				backgroundRepeat(BackgroundRepeat.NoRepeat)
				backgroundSize(BackgroundSize.of(200.percent, 100.percent))
			}

			self + nthChild(Nth.Even) style {
				gradientBorderBackground(Color(BACKGROUND_SECTION_EVEN_COLOR))
			}

			"h2" {
				backgroundImage(linearGradient(45.deg) {
					add(Color("#00D4FF"))
					add(Color("#FF0080"))
				})
				property("-webkit-background-clip", "text")
				property("-webkit-text-fill-color", "transparent")
				property("-moz-text-fill-color", "transparent")
				property("-moz-background-clip", "text")

				group(desc(self, type("img")), desc(self, type("i"))) style {
					val iconPadding = .4.cssRem
					val iconHeight = titleHeight.value() + (iconPadding * 2)

					borderRadius(.75.cssRem)
					height(iconHeight)
					lineHeight(iconHeight)
					padding(iconPadding)
					width(auto)
					border {
						width(1.px)
						style(LineStyle.Solid)
						color(Color.transparent)
					}
					property("background", """
						transparent padding-box,
						linear-gradient(45deg, #00D4FF, #FF0080) border-box
					""")
				}

				fontSize(titleHeight.value())
				margin(0.px)
			}

			"p" {
				color(Color("#FFFFFFE6"))
				fontSize(1.05.cssRem)
				lineHeight(1.8.number)
				margin(0.px)
			}

			"a" {
				color(Color("#7FE3FF"))
				property("text-decoration-color", "#7FE3FF60")
				property("text-underline-offset", "3px")

				hover(self) style {
					color(Color("#FF6FB5"))
					property("text-decoration-color", "#FF6FB5")
				}
			}

			"code" {
				backgroundColor(Color("#FFFFFF12"))
				borderRadius(.35.cssRem)
				fontSize(.9.cssRem)
				padding(.1.cssRem, .35.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			self {
				titleHeight(1.4.cssRem)

				"section" {
					padding(1.25.cssRem)

					self + className("selected") style {
						property("transform", "none")
					}
				}
			}
		}
	}

	val textIcon by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Start)
		flexDirection(FlexDirection.Row)
		gap(1.25.cssRem)

		media(mediaMaxWidth(686.px)) {
			self {
				gap(.8.cssRem)
			}
		}
	}
}
