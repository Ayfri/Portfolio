package pages

import FontAwesomeType
import FooterStyle
import I
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import kotlinx.browser.window
import localImage
import markdownParagraph
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import style.AppStyle
import style.utils.*

data class AboutMeSection(val content: String, val date: Int, val image: Boolean = false, val id: String, val title: (@Composable () -> Unit)? = null) {
	var htmlElement: HTMLElement? = null
	
	@Composable
	fun Display(selected: Boolean = false) {
		Section({
			if (image) classes(AboutMeStyle.withImage)
			if (selected) classes("selected")
			id(id)
			
			ref {
				htmlElement = it
				onDispose {}
			}
		}) {
			title?.let {
				if (image) it()
				else H2({
					classes(AboutMeStyle.textIcon)
				}) {
					it()
				}
			}
			
			P({
				markdownParagraph(content)
			})
		}
	}
}

val sections = listOf(
	AboutMeSection(
		"""
			Hi, it’s me, Pierre Roy, I am a first year IT student at [Ynov Aix school](https://www.ynov.com/campus/aix-en-provence/), and I am passionate about computer science and especially programming.
			I’m making all sort of projects and programming by myself since years and this is my portfolio, welcome !
		""".trimIndent(), 2002, true, "me"
	) {
		Img(localImage("avatar.jpg"), "avatar") {
			classes(AppStyle.avatar)
		}
	},
	
	AboutMeSection(
		"""
			First started programming in 2014 by randomly watching a conference about programming in a library.
			I first tried some experiments in Python with a [book](https://www.eyrolles.com/Informatique/Livre/python-pour-les-kids-9782212140880) that my dad paid me, done some beginner projects, etc.

			After that, created some random projects I found on YouTube about like programming a Minecraft-Like game in C++ or making some little games in Unity.
			But I was not understanding a lot what I was doing and was almost only copying the tutorials without trying myself to create things.
		""".trimIndent(), 2014, id = "intro"
	) {
		TextIcon("Intro to programming", FontAwesomeType.SOLID, "computer")
	},
	
	AboutMeSection(
		"""
			After creating my own [Discord server](https://discord.gg/BySjRNQ9Je) for my community from [Youtube](https://www.youtube.com/c/Ayfri), I wanted to create a Discord Bot. So I created a bot in [JavaScript](https://developer.mozilla.org/docs/Web/JavaScript) using [Node.JS](https://nodejs.org) in early 2018 by following tutorials, I created CommunAyBot.
			Back in the days it was not that common to create a bot for your own community and Discord was not so reputed. Because of that, there was no really great tutorials and I got a lot of help from a Discord Server named Obelia Dev _(which doesn't even exist today)_, my friends Ghom, Loockeeer, Felons and some others helped me a lot to understand and create my bot.

			After about a year I was pretty good at creating a bot, but I realised that my project was kinda... ugly. So I recreated it, [AyBot 2](https://github.com/Ayfri/AyBot-2) was born in early 2019. It was pretty clean, and I wanted people to use my bot, so I changed a lot of the code, so it was working on multiple servers with configurations etc.
			
			At this time I created some other little utilities bots or test bots.
			AyBot 2 was taken down in end of 2019 because of [Galileo](#galileo).
		""".trimIndent(), 2018, id = "second-intro"
	) {
		TextIcon("Second Intro to Programming", FontAwesomeType.BRAND, "discord", Color("#5865f2"))
	},
	
	AboutMeSection(
		"""
			At my high-school, for 3 years, I used arduino a lot, only creating big projects on the last year but still. It has let me learn a lot on low-level programming, [C++](https://cplusplus.com) basics and microcontrollers.
			By doing my own researches I learned a lot about C++ compilation, assembler, reverse engineering, optimisation and other low-level subjects.
			I even tried a bit writing assembler x86. I created some little programs using C++.
			
			Back in 2015 I followed a [tutorial to create a Minecraft-Like game](https://www.youtube.com/watch?v=GACpZp8oquU) in C++ using [OpenGL](https://www.opengl.org/) and a bunch of libraries.
			It was interesting to go after some years, back on this project and fix some issues and finally understand the code and learn more about OpenGL and low-level graphics processing.

			I currently still have my own arduino that I bought during my high-school.
		""".trimIndent(), 2018, id = "arduino"
	) {
		TextIcon("Arduino", localImage("arduino.svg"))
	},
	
	AboutMeSection(
		"""
			At this time I was administrator (and I’m still) on a [big Discord server](https://discord.gg/sDT7W8mNmq) at the time about sharing your server or website. And I was friend with the other administrator, Antow, which also created his own bot, I was helping him sometimes.
			After some discussions, we accorded to merge our bots together, as we were both passionate about astronomy, we named it [Galileo](https://github.com/Galileo-Bot/galileo).

			But these times were complicated for me personally and due to a lack of time and motivations because I was making almost only discord bots since about 2 years. I stopped developing it and moved away to other interesting projects.
		""".trimIndent(), 2019, id = "galileo"
	) {
		TextIcon("Galileo", localImage("galileo.png"))
	},
	
	AboutMeSection(
		"""
			In 2015, I created my first graphic programs using [Processing](https://processing.org) and [Java](https://www.java.com).
			I heard about this program from watching videos from [The Coding Train](https://www.youtube.com/c/TheCodingTrain). I created a lot of little experiments using it and learned a lot of interesting concepts.
			I learned some interesting things about 3D graphics, how it works, how to optimise it etc. My biggest 3D expriment was a [Minecraft-Like](https://github.com/Ayfri/ProceCraft) game and my greatest program with Processing was a program to convert an [image to a Minecraft Pixel-Art](https://github.com/Ayfri/Image2Minecraft) using only blocks from Minecraft as pixels.

			After some years and making some Discord Bots in [JavaScript](https://developer.mozilla.org/docs/Web/JavaScript), I heard that a library in JavaScript existed that was exactly like Processing, [p5.js](https://p5js.org) _(which was created by the same foundation)_, and used it for some little projects, even created a [port](https://github.com/Ayfri/TypeCraft) of the Minecraft-Like game in [TypeScript](https://www.typescriptlang.org).
			But it was too simple and not enough expandable for me. So I searched another library for making my own games with more depth and customisation.

			[PIXI.js](https://pixijs.com) was the library I stepped on, and I programmed a lot with it, I’m still actually using it for saying. My first big game with it was a [2D Minecraft-Like game](https://github.com/Ayfri/2d-minecraft) (again yes hehe).
			Then a [2D Portal game](https://github.com/Ayfri/portal-2d) experiment. Also a [Cookie-Clicker-Like](https://github.com/Ayfri/atom-clicker) game about atoms, Atom Clicker. And recently I’m recreating my [2D Minecraft-Game](https://github.com/Ayfri/Minekraft-2D) in Kotlin, still using PIXI, named Minekraft-2D.
		""".trimIndent(), 2015, id = "processing"
	) {
		TextIcon("Processing", "https://upload.wikimedia.org/wikipedia/commons/c/cb/Processing_2021_logo.svg")
	},
	
	AboutMeSection(
		"""
			[Minecraft](https://minecraft.net) is my favorite game of all time, I started playing it back in 2010, I know it by heart.
			Heard about it randomly on an old computer blog that my dad was subscribed to where on an article they spoked about _"this new game with an interesting concept"_.

			I played a hundred of hours on Minecraft, created numerous maps, technical maps using CommandBlocks, Datapacks, mods, even created mods since around 2018. In [Java](https://www.java.com) using [Forge](https://files.minecraftforge.net/net/minecraftforge/forge), [Fabric](https://fabricmc.net) since 2020 and in [Kotlin](https://kotlinlang.org) since 2021.

			I know a lot about the concepts of Minecraft and how they are programmed, this is what motivated me to create a Minecraft-Like game, _4 times_. Approaching more what I want and performances needed for a game like this each time.
		""".trimIndent(), 2018, id = "minecraft"
	) {
		TextIcon("Minecraft", localImage("minecraft.svg"))
	},
	
	AboutMeSection(
		"""
			After getting graduated from high-school, I got accepted into a private school named [Ynov](https://ynov.com).
			We learned so far [GoLang](https://go.dev), [Python](https://www.python.org), pretty complex [C++](https://cplusplus.com) and POO into C++, some network basics, how to use [REST API](https://wikipedia.org/wiki/Representational_state_transfer)s, the team JS/HTML/CSS, databases and [MySQL](https://www.mysql.com), and other useful technologies.
			I was already knowing some, but I appreciated a lot GoLang’s GoHTML Templates, C++ and my teacher who I chatted a lot with and understanding a lot more how network work.
	
			We created a bunch of projects, starting with little training projects in GoLang/Python/C++/JavaScript, done some websites using only front, but also using an API, and using MySQL creating an entire forum.
	
			It is for me a great experience being in this school, and I’m excited for the next 4 years !
		""".trimIndent(), 2021, id = "ynov"
	) {
		TextIcon("Post-Bac and Ynov", "https://www.ynov.com/wp-content/themes/ynov/assets/images/favicons/apple-touch-icon.png")
	},
	
	AboutMeSection(
		"""
		My school required me to have an internship of 6 weeks in any enterprise to have a first professional experience.
		I got in contact with [BlueFrog](https://www.bluefrog.fr/index.html), a company creating websites, and after some interviews got accepted !

		I learned [PHP](https://www.php.net) and [WordPress](https://wordpress.org) and created a few websites during my traineeship, and it was a great experience for me.
		Learning a bunch of useful technologies, the difference between personal project and real projects and a view of what is the job of developer.
	""".trimIndent(), 2022, id = "first-internship"
	) {
		TextIcon("BlueFrog", "https://www.bluefrog.fr/images/logo.png")
	},
).sortedBy { it.date }

const val timelineDefaultOffset = 125.0

@Composable
fun AboutMe() {
	Style(AboutMeStyle)
	
	var timelineOffset by mutableStateOf(timelineDefaultOffset)
	var roundSelected by mutableStateOf(0)
	
	Div({
		classes(AboutMeStyle.timeline)
		style {
			top(timelineOffset.px)
		}
		
		window.addEventListener("scroll", {
			val footerOffset = document.querySelector(".${FooterStyle.footer}")?.asDynamic()?.offsetTop as Double? ?: return@addEventListener
			
			if (window.scrollY + window.innerHeight < footerOffset) {
				timelineOffset = window.scrollY + timelineDefaultOffset
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
			val elementOffset = element.asDynamic().offsetTop as Double - timelineDefaultOffset * 2
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

@Composable
fun TextIcon(text: String, icon: String) {
	Img(icon, "icon")
	Text(text)
}

@Composable
fun TextIcon(text: String, fontAwesomeType: FontAwesomeType, icon: String, color: CSSColorValue? = null) {
	I(fontAwesomeType, icon) {
		color?.let { color(it) }
	}
	Text(text)
}


object AboutMeStyle : StyleSheet() {
	const val backgroundSectionOddColor = "#363636"
	const val backgroundSectionEvenColor = "#2e2e2e"
	const val timelineBgGradiantEndColor = "#71136D"
	const val timelineBgGradiantStartColor = "#4B4F9D"
	
	val timelineSize by variable<CSSSizeValue<*>>()
	val timelineOffset = 1.5.cssRem
	
	init {
		"html" {
			property("scroll-behavior", "smooth")
			property("scroll-padding-top", AppStyle.navbarHeight.value() + timelineDefaultOffset.px)
		}
		
		id("main") style {
			timelineSize(max(8.cssRem, 10.vw))
		}
	}
	
	@OptIn(ExperimentalComposeWebApi::class)
	val appearLeft by keyframes {
		from {
			opacity(0.0)
			transform { translateX(3.cssRem) }
		}
		
		to {
			opacity(1.0)
			transform { translateX(0.px) }
		}
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
		val length = 8.vh
		val roundSize = 3.vh
		
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		
		position(Position.Absolute)
		left(timelineOffset)
		height(90.percent)
		width(timelineSize.value())
		
		child(self, universal) style {
			background(linearGradient {
				stop(Color(timelineBgGradiantStartColor))
				stop(Color(timelineBgGradiantEndColor))
			})
			backgroundAttachment("fixed")
			
			marginTop((-.2).cssRem)
		}
		
		className("round") style {
			size(roundSize)
			borderRadius(10.cssRem)
			position(Position.Relative)
			
			transitions {
				delay(.4.s)
				ease(AnimationTimingFunction.cubicBezier(.47, 2.0, .41, .8))
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
	
	@OptIn(ExperimentalComposeWebApi::class)
	val content by style {
		marginLeft(timelineSize.value())
		
		"section" {
			val height = 2.8.cssRem
			
			backgroundColor(Color(backgroundSectionOddColor))
			fontFamily(AppStyle.monoFontFamily)
			padding(1.5.cssRem, height)
			position(Position.Relative)
			
			transitions {
				duration(.3.s)
				ease(AnimationTimingFunction.EaseInOut)
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
				backgroundColor(Color(backgroundSectionEvenColor))
			}
			
			"h2" {
				group(desc(self, type("img")), desc(self, type("i"))) style {
					val iconPadding = .4.cssRem
					val iconHeight = height + (iconPadding * 2)
					
					backgroundColor(Color("#00000015"))
					borderRadius(.75.cssRem)
					height(iconHeight)
					lineHeight(iconHeight)
					padding(iconPadding)
					width(auto)
				}
				
				fontSize(height)
				margin(0.cssRem, 0.px, 1.5.cssRem)
			}
			
			"p" {
				fontSize(1.05.cssRem)
				lineHeight(1.6.cssRem)
				margin(0.px)
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