package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AppStyle
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.FooterStyle
import io.github.ayfri.components.HeaderStyle
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.localImage
import io.github.ayfri.markdownParagraph
import io.github.ayfri.utils.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

data class AboutMeSection(
	val content: String,
	val date: Int,
	val image: Boolean = false,
	val id: String,
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
		else H2({
			classes(AboutMeStyle.textIcon)
		}) {
			title()
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
		""".trimIndent(), 2014, id = "intro"
	) {
		TextIcon("Introduction to programming", FontAwesomeType.SOLID, "computer")
	},

	AboutMeSection(
		"""
			In 2015, I created my first project with a Graphical User Interface using the [Processing](https://processing.org) software and programming language.
			I heard about this program from watching videos from [The Coding Train](https://www.youtube.com/c/TheCodingTrain). I created a lot of little experiments using this library and learned some interesting things about 3D graphics, how it works, how to optimise it, etc.
			My biggest 3D experiment was a [Minecraft-like](https://github.com/Ayfri/ProceCraft) game, and my most significant program with Processing was a tool to convert an [image into Minecraft Pixel-Art](https://github.com/Ayfri/Image2Minecraft) using only blocks from Minecraft as pixels.

			After some years of making some Discord Bots in [JavaScript](https://developer.mozilla.org/docs/Web/JavaScript), I heard that a library in JavaScript existed that was exactly like Processing, [p5.js](https://p5js.org) _(which was created by the same foundation)_, and used it for some little projects, even created a [port](https://github.com/Ayfri/TypeCraft) of the Minecraft-like game in [TypeScript](https://www.typescriptlang.org).
			But it was too simplistic and not enough expandable for me. So I looked for another library to make my own games with more depth and customization.

			[PIXI.js](https://pixijs.com) was the library I stumbled upon, and I created a few projects with it for a long time. My first big game with it was a [2D Minecraft-like game](https://github.com/Ayfri/2d-minecraft) (again yes hehe).
			Then a [2D Portal game](https://github.com/Ayfri/portal-2d) experiment. Also, a [Cookie-Clicker-like](https://github.com/Ayfri/atom-clicker) game about atoms, Atom Clicker. And recently I’m recreating my [2D Minecraft-Game](https://github.com/Ayfri/Minekraft-2D) in Kotlin, still using PIXI, named Minekraft-2D.
		""".trimIndent(), 2015, id = "processing"
	) {
		TextIcon("Processing", "https://upload.wikimedia.org/wikipedia/commons/c/cb/Processing_2021_logo.svg")
	},

	AboutMeSection(
		"""
			After creating my own [Discord server](https://discord.gg/BySjRNQ9Je) for my community from [YouTube](https://www.youtube.com/c/Ayfri), I wanted to create a Discord Bot. So I created a bot in [JavaScript](https://developer.mozilla.org/docs/Web/JavaScript) using [Node.JS](https://nodejs.org) in early 2018 by following tutorials, I created CommunAyBot.
			Back in the days, it was not that common to create a bot for your own community, and Discord was not so reputed. Because of that, there were no great tutorials. I got a lot of help from a Discord Server named Obelia Dev _(which doesn't even exist today)_, my friends Ghom, Loockeeer, Felons, and some others helped me a lot to understand and create my bot.

			About a year later, I was pretty good at creating a bot, but I realized that my project was kind of... ugly. So I recreated it, [AyBot 2](https://github.com/Ayfri/AyBot-2) was born in early 2019. It was pretty clean, and I wanted people to use my bot, so I changed a lot of the code, so it was working on multiple servers with configurations, etc.
			
			At that time, I created some other little utilities bots or test bots.
			AyBot 2 was taken down at the end of 2019 because of [Galileo](#galileo).
		""".trimIndent(), 2018, id = "second-intro"
	) {
		TextIcon("Second Introduction to Programming", FontAwesomeType.BRAND, "discord", Color("#5865f2"))
	},

	AboutMeSection(
		"""
			At my high school, for 3 years, I used arduino a lot, only creating big projects in the last year but still. It has let me learn a lot on low-level programming, [C++](https://cplusplus.com) basics and microcontrollers.
			By doing my own research, I learned a lot about C++ compilation, assembler, reverse engineering, optimization and other low-level subjects.
			I even tried a bit of writing assembler x86. I created some little programs using C++.
			
			Back in 2015, I followed a [tutorial to create a Minecraft-like game](https://www.youtube.com/watch?v=GACpZp8oquU) in C++ using [OpenGL](https://www.opengl.org/) and a bunch of libraries.
			It was interesting to go after some years, back on this project and fix some issues and finally understand the code and learn more about OpenGL and low-level graphics processing.

			I still have my own arduino that I bought during high-school.
		""".trimIndent(), 2018, id = "arduino"
	) {
		TextIcon("Arduino", localImage("arduino.svg"))
	},

	AboutMeSection(
		"""
			[Minecraft](https://minecraft.net) is my favorite game of all time, I started playing it back in 2010, I know it by heart.
			Heard about it randomly on an old computer blog that my dad was subscribed to where on an article they spoke about _"this new game with an interesting concept"_.

			Since I own the game, I played hundreds of hours in Minecraft, created numerous maps, technical maps using command blocks, datapacks, mods, even created mods since around 2018. In [Java](https://www.java.com) using [Forge](https://files.minecraftforge.net/net/minecraftforge/forge), [Fabric](https://fabricmc.net) since 2020 and in [Kotlin](https://kotlinlang.org) since 2021.

			I know a lot about the concepts of Minecraft and how they are programmed, this is what motivated me to create a Minecraft-like game, _4 times_. Approaching more what I want and performances needed for a game like this each time.
		""".trimIndent(), 2018, id = "minecraft"
	) {
		TextIcon("Minecraft", localImage("minecraft.png"))
	},

	AboutMeSection(
		"""
			During this time, I was administrator (and I’m still) on a [big Discord server](https://discord.gg/sDT7W8mNmq) at the time about sharing your server or website. And I was the friend of the other administrator, Antow, who also created his own bot, I was helping him sometimes.
			After some discussions, we agreed to merge our bots, as we were both passionate about astronomy, we named it [Galileo](https://github.com/Galileo-Bot/galileo).

			But these times were complicated for me personally and due to a lack of time and motivations because I was making almost only discord bots since about 2 years. I stopped developing it and moved away to other interesting projects.
		""".trimIndent(), 2019, id = "galileo"
	) {
		TextIcon("Galileo", localImage("galileo.png"))
	},

	AboutMeSection(
		"""
			After getting graduated from high-school, I got accepted into a private school named [Ynov](https://ynov.com).
			We learned so far [GoLang](https://go.dev), [Python](https://www.python.org), pretty complex [C++](https://cplusplus.com) and OOP into C++, some network basics, how to use [REST API](https://wikipedia.org/wiki/Representational_state_transfer)s, team JS/HTML/CSS, databases with [MySQL](https://www.mysql.com), and other useful technologies.
			I greatly appreciated GoLang’s GoHTML Templates, C++, and my teacher, who I discussed a lot with, and understanding a lot more how computer network works.
	
			We created a bunch of projects, starting with little training projects in GoLang/<wbr>Python/<wbr>C++/<wbr>JavaScript, done some websites using only front, but also using an API, and using MySQL creating an entire forum.
	
			It is for me a great experience being in this school, and I’m excited for the next 3 years !
		""".trimIndent(), 2021, id = "ynov"
	) {
		TextIcon("Post-Bac and Ynov", localImage("ynov-icon.png"))
	},

	AboutMeSection(
		"""
			My school required me to have an internship of 6 weeks in any enterprise to have a first professional experience.
			I got in contact with [BlueFrog](https://www.bluefrog.fr/index.html), a company creating websites, and after some interviews got accepted !
	
			I learned [PHP](https://www.php.net) and [WordPress](https://wordpress.org) and created a few websites & plugins during my internship, and it was a great experience for me.
			Learning a bunch of useful technologies, the difference between personal project and real projects and seeing a developer's an everyday job.
		""".trimIndent(), 2022, id = "first-internship"
	) {
		TextIcon("BlueFrog", "https://www.bluefrog.fr/images/logo.png")
	},

	AboutMeSection(
		"""
			During my second year at Ynov, I though about creating an utility library for creating Minecraft Datapacks.
			I was on a Discord community about Minecraft Datapacks since 2 years, and I was searching for any project that I could make in Kotlin.
			So I started creating [Datapack-DSL](https://github.com/Ayfri/Datapack-DSL) in around november.
			
			This project has been a great experience for me, I learned a lot about Datapacks and Minecraft internal working, it was really a pleasure to create that.
			The project contains functions (DSLs) for every commands in Minecraft, every JSONs Minecraft uses, Datapack creation, etc.
			The project is not yet finished or published as of August 2023. However, it is very close to completion.
			I am currently working on the documentation and searching for the right identity for the project, including a better name and icon.
			Feel free to help me !
		""".trimIndent(), 2022, id = "datapack-dsl"
	) {
		TextIcon("Datapack-DSL", localImage("minecraft-new.png"))
	},

	AboutMeSection(
		"""
			Ynov added a new system of projects named YBoosts in end of 2022 where students from first & second years can create a project in a team 7 or more people.
			We created a team of 7 people, named Defensive Realms, and though about creating a game.
			This is how [Cat'aClysm: Claw Of The Dead](https://github.com/Ayfri/Cat-aclysm-Claw-of-the-Dead) was born.
			
			We collaborated with another team to use the same universe for the story, and we created a Tower Defense game in 2D using [Godot](https://godotengine.org) 4.0.
			This was my first experience with Godot, this was really exciting and interesting, Godot is really a good game engine (I prefer it over Unity).
			We created our own sprites and animations, the sounds and musics were used from diverse sources from the internet.
			The game was finished in its first Alpha in around the end of May 2023, and it is planned to create Beta the next year.
			The beta will be recreated from scratch to be more organized and have a better codebase.
		""".trimIndent(), 2023, id = "cat-aclysm"
	) {
		TextIcon("Cat'aClysm: Claw Of The Dead", localImage("cat-aclysm.png"))
	},

	AboutMeSection(
		"""
			AI has been a crucial subject these last years, a lot more since the release of [ChatGPT](https://chat.openai.com/).
			I have always been interested in AI, and I wanted to create my own AI or at least a project using AI.
			During June, I've made a little project using [Kotlin](https://kotlinlang.org), [Compose for Desktop](https://www.jetbrains.com/fr-fr/lp/compose-multiplatform/) and the [GPT-4 API](https://platform.openai.com/docs/api-reference), name [Artificial-Infiltration](https://github.com/Ayfri/Artificial-Infiltration).
			The concept is a chat room where you can talk with five other people, but an AI is also in the room, and you have to find who is the AI.
			This was really interesting and amusing to use GPT API, but at that time I was not a pro in prompt engineering, so the AI was not that good until we switched to GPT-4, which is about 20 times more costly.
			
			For validating my second year at Ynov, I had to found a stage in a company for 6 weeks or more.
			But the school also proposed a stage organized by the Data Engineering teacher, so I applied for it and got accepted.
			We've done a lot of searches about ChatGPT, autonomous agents (like [AutoGPT](https://news.agpt.co/), [SuperAGI](https://superagi.com/)) and expanding context of GPT (like [ChatGPT-Memory](https://github.com/continuum-llms/chatgpt-memory)).
			Finally, we found other technologies to better create our project, and we started working on it.

			The project is named ScriptGraf, its purpose is to automatically create posts for your company's social media.
			We are working on it since July 2023, and we are planning to finish as soon as we can.
		""".trimIndent(), 2023, id = "scriptgraf"
	) {
		TextIcon("ScriptGraf", localImage("ChatGPT.png"))
	})

const val TIMELINE_DEFAULT_OFFSET = 125.0

@Page("/about-me/index")
@Composable
fun AboutMe() {
	PageLayout("About Me") {
		Style(AboutMeStyle)

		var timelineOffset by mutableStateOf(TIMELINE_DEFAULT_OFFSET)
		var roundSelected by mutableStateOf(0)

		Aside({
			classes(AboutMeStyle.timeline)
			style {
				top(timelineOffset.px)
			}

			window.addEventListener("scroll", {
				val footerOffset =
					document.querySelector(".${FooterStyle.footer}")?.asDynamic()?.offsetTop as Double? ?: return@addEventListener

				if (window.scrollY + window.innerHeight < footerOffset) {
					timelineOffset = window.scrollY + TIMELINE_DEFAULT_OFFSET * .8
				}
			})
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

		val callback = callback@{
			sections.forEachIndexed { index, section ->
				if (index == roundSelected) return@forEachIndexed
				val element = document.querySelector("#${section.id}") ?: return@forEachIndexed
				val elementOffset = element.asDynamic().offsetTop as Double - TIMELINE_DEFAULT_OFFSET * 2
				val elementHeight = element.asDynamic().offsetHeight as Double

				val elementRange = elementOffset..(elementOffset + elementHeight)

				if (window.scrollY in elementRange) {
					roundSelected = index
					return@callback
				}
			}
		}

		window.addEventListener("resize", { callback() })
		window.addEventListener("scroll", { callback() })
		document.addEventListener("DOMContentLoaded", { callback() })
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
	const val BACKGROUND_SECTION_ODD_COLOR = "#363636"
	const val BACKGROUND_SECTION_EVEN_COLOR = "#2e2e2e"
	const val TIMELINE_BG_GRADIANT_END_COLOR = "#71136D"
	const val TIMELINE_BG_GRADIANT_START_COLOR = "#4B4F9D"

	val timelineSize by variable<CSSSizeValue<*>>()
	val timelineOffset by variable<CSSSizeValue<*>>()

	init {
		"html" {
			property("scroll-padding-top", HeaderStyle.navbarHeight.value() + TIMELINE_DEFAULT_OFFSET.px)
		}

		id("main") style {
			timelineSize(max(8.cssRem, 10.vw))
			timelineOffset(1.5.cssRem)
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			id("main") style {
				timelineSize(max(4.cssRem, 5.vw))
				timelineOffset(0.3.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			id("main") style {
				timelineSize(0.px)
				timelineOffset((-5).cssRem)
			}
		}

		id("cat-aclysm") style {
			property("image-rendering", "pixelated")
		}

		"h1" style {
			fontSize(3.cssRem)
			margin(0.px)
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
			backgroundPosition("200% 0%")
		}

		to {
			backgroundPosition("0% 0%")
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val timeline by style {
		val thickness = 1.vh
		val length = sections.size * 0.45.vh
		val roundSize = 3.vh

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)

		position(Position.Absolute)
		left(timelineOffset.value().unsafeCast<CSSLengthValue>())
		height(90.percent)
		width(timelineSize.value())

		child(self, universal) style {
			background(linearGradient {
				stop(Color(TIMELINE_BG_GRADIANT_START_COLOR))
				stop(Color(TIMELINE_BG_GRADIANT_END_COLOR))
			})
			backgroundAttachment("fixed")

			marginTop((-.2).cssRem)
		}

		className("round") style {
			size(roundSize)
			borderRadius(10.cssRem)
			position(Position.Relative)

			transitions {
				defaultDelay(.4.s)
				defaultTimingFunction(AnimationTimingFunction.cubicBezier(.47, 2.0, .41, .8))
				properties("transform", "box-shadow")
			}

			group(hover(self), self + className("selected")) style {
				boxShadow(Color("#00000070"), offset = 0.px, blur = .8.cssRem)
				zIndex(3)
			}

			hover(self) style {
				transform { scale(1.1) }
				cursor(Cursor.Pointer)
			}

			self + className("selected") style {
				transform { scale(1.2) }
				border {
					color(Color.white)
					style(LineStyle.Solid)
					width(2.px)
				}

				self + after style {
					property("content", "attr(data-date)")
					fontWeight(700)

					position(Position.Absolute)
					top(0.px)
					left((-3).cssRem)
					height(100.percent)
					width(minContent)

					animation(appearLeft) {
						duration(.4.s)
						timingFunction(AnimationTimingFunction.cubicBezier(.47, 2.0, .41, .8))
					}
				}
			}
		}

		className("separator") style {
			height(length)
			width(thickness)
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			className("round") + className("selected") + after style {
				left(50.percent)
				transform { translateX((-50).percent) }
				property("top", "unset")
				bottom((-2.5).cssRem)

				backgroundColor(Color("#00000070"))
				padding(.2.cssRem, .4.cssRem)
				borderRadius(.5.cssRem)

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

	@OptIn(ExperimentalComposeWebApi::class)
	val content by style {
		val titleHeight by variable<CSSSizeValue<*>>()

		marginLeft(timelineSize.value())

		"section" {
			titleHeight(max(2.cssRem, 3.vw))

			backgroundColor(Color(BACKGROUND_SECTION_ODD_COLOR))
			fontFamily(AppStyle.MONO_FONT_FAMILY)
			padding(1.5.cssRem, titleHeight.value())
			position(Position.Relative)

			transitions {
				defaultDuration(.3.s)
				defaultTimingFunction(AnimationTimingFunction.EaseInOut)
				properties("transform")
			}

			self + className("selected") style {
				val offset = 4.px
				transform {
					scaleX(1.005)
					translateX(-offset)
				}

				borderRadius(topLeft = 5.px, bottomLeft = 5.px, topRight = 0.px, bottomRight = 0.px)

				overflow(Overflow.Hidden)
				animation(sectionSelection) {
					delay(.1.s)
					duration(.4.s)
					timingFunction(AnimationTimingFunction.EaseInOut)
				}

				property("background-size", "200% 100%")
				backgroundImage(linearGradient(90.deg) {
					stop(Color("#50435A"))
					stop(Color("#28273e"))
					stop(Color.transparent)
				})
				backgroundRepeat("no-repeat")
			}

			self + nthChild(Nth.Even) style {
				backgroundColor(Color(BACKGROUND_SECTION_EVEN_COLOR))
			}

			"h2" {
				group(desc(self, type("img")), desc(self, type("i"))) style {
					val iconPadding = .4.cssRem
					val iconHeight = titleHeight.value() + (iconPadding * 2)

					backgroundColor(Color("#00000015"))
					borderRadius(.75.cssRem)
					height(iconHeight)
					lineHeight(iconHeight)
					padding(iconPadding)
					width(auto)
				}

				fontSize(titleHeight.value())
				margin(0.cssRem, 0.px, 1.5.cssRem)
			}

			"p" {
				fontSize(1.05.cssRem)
				lineHeight(1.6.cssRem)
				margin(0.px)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				marginLeft(timelineSize.value() * 1.2)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			self {
				titleHeight(1.5.cssRem)
				marginLeft(0.px)

				"section" {
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
		gap(1.5.cssRem)

		media(mediaMaxWidth(686.px)) {
			self {
				flexDirection(FlexDirection.Column)
				textAlign(TextAlign.Center)
			}
		}
	}
}
