package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.scale
import com.varabyte.kobweb.compose.css.translateY
import com.varabyte.kobweb.core.Page
import io.github.ayfri.*
import io.github.ayfri.components.A
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.I
import io.github.ayfri.components.Span
import io.github.ayfri.data.DataStyle
import io.github.ayfri.data.HomeCard
import io.github.ayfri.data.gitHubData
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType.Enum.Screen
import org.jetbrains.compose.web.css.CSSMediaQuery.Only
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import kotlin.js.Date

inline val years get() = (Date.now() - Date("2002-10-15").getTime()) / 1000 / 60 / 60 / 24 / 365

const val MAIN_PRESENTATION = """
Hi, I'm Pierre Roy, an IT student at [Ynov Aix school](https://www.ynov.com/campus/aix-en-provence), and I'm passionate about computer science and especially programming.
I'm making all sorts of projects and programming by myself for years. This is my portfolio, welcome!
"""

const val PORTFOLIO_SUMMARY = """
This portfolio showcases my journey as a developer, my skills, and the projects I've worked on. 
Built with Kotlin and Compose for Web, it represents both my technical abilities and my passion for clean, functional design.
"""

const val EXPERIENCE_SUMMARY = """
From internships at [BlueFrog](https://www.bluefrog.fr/) where I developed WordPress sites, to working on AI projects like ScriptGraf at [Ynov](https://www.ynov.com/), 
I've gained valuable experience in various technologies and collaborative environments.
"""

@Page("/index")
@Composable
fun Home() {
	PageLayout("Home") {
		val homeRepositories = gitHubData.repos.sortedBy { it.stargazersCount }.reversed().take(3)
		val featuredSkills = skills.sortedWith(
			compareByDescending<Skill> { it.language.level }.thenByDescending { it.language.since }
		).take(8)

		Style(HomeStyle)
		Style(DataStyle)

		Section({
			classes(HomeStyle.top)
		}) {
			Div({
				classes(HomeStyle.topInfo)
			}) {
				Img(localImage("avatar@300x300.webp"), "avatar") {
					classes(AppStyle.avatar)
					height(300)
					width(300)
				}

				H1 {
					Text("Pierre Roy")
					Span("alias")
					Span(" Ayfri")
				}

				H2 {
					Text("IT Student")
				}

				H2 {
					Text("France")
				}

				H3 {
					Text("Born ")

					Span({
						classes(AppStyle.monoFont, AppStyle.numberColor)
					}) {
						Text(years.toInt().toString())
					}

					Text(" years ago")
				}
			}

			P({
				markdownParagraph(MAIN_PRESENTATION.trimIndent(), true, AppStyle.monoFont)
				style {
					lineHeight(1.5.cssRem)
					fontSize(1.1.cssRem)
					maxWidth(800.px)
					margin(1.cssRem, auto)
				}
			})

			// Featured Projects Section
			Section({
				classes(HomeStyle.section)
			}) {
				H2({
					classes(HomeStyle.sectionTitle)
				}) {
					I(FontAwesomeType.SOLID, "code") {
						marginRight(0.5.cssRem)
					}
					Text("Featured Projects")
				}

				Div({
					classes("list", "repos")
				}) {
					homeRepositories.forEachIndexed { index, repository ->
						Div({
							classes(DataStyle.homeCard, "repo")
							style {
								property("animation-delay", (index * 0.2).s)
							}
						}) {
							HomeCard(repository)
						}
					}
				}

				A("/projects/", "See all projects", AppStyle.button)
			}

			// Skills Section
			Section({
				classes(HomeStyle.section)
			}) {
				H2({
					classes(HomeStyle.sectionTitle)
				}) {
					I(FontAwesomeType.SOLID, "laptop-code") {
						marginRight(0.5.cssRem)
					}
					Text("Top Skills")
				}

				Div({
					classes("list", "skills")
				}) {
					featuredSkills.forEachIndexed { index, skill ->
						A("/skills/#${skill.language.name}", {
							classes(HomeStyle.skill)
							style {
								property("animation-delay", (index * 0.25 + 0.75).s)
							}
						}) {
							skill.DisplaySimple()
						}
					}
				}

				A("/skills/", "See all skills", AppStyle.button)
			}

			// Portfolio Section
			Section({
				classes(HomeStyle.section, HomeStyle.portfolioSection)
			}) {
				H2({
					classes(HomeStyle.sectionTitle)
				}) {
					I(FontAwesomeType.SOLID, "briefcase") {
						marginRight(0.5.cssRem)
					}
					Text("My Portfolio")
				}

				Div({
					classes(HomeStyle.portfolioContent)
				}) {
					Div({
						classes(HomeStyle.portfolioText)
					}) {
						P({
							markdownParagraph(PORTFOLIO_SUMMARY.trimIndent(), true, AppStyle.monoFont)
							style {
								marginBottom(2.cssRem)
							}
						})

						A("/portfolio/", "Learn more about this portfolio", AppStyle.button)
					}

					Img(localImage("portfolio-3-small.png"), "Portfolio screenshot") {
						classes(HomeStyle.portfolioImage)
					}
				}
			}

			// Experience Section
			Section({
				classes(HomeStyle.section, HomeStyle.experienceSection)
			}) {
				H2({
					classes(HomeStyle.sectionTitle)
				}) {
					I(FontAwesomeType.SOLID, "graduation-cap") {
						marginRight(0.5.cssRem)
					}
					Text("Professional Experience")
				}

				Div({
					classes(HomeStyle.experienceContent)
				}) {
					Img(localImage("minecraft-new.png"), "Experience illustration") {
						classes(HomeStyle.experienceImage)
					}

					Div({
						classes(HomeStyle.experienceText)
					}) {
						P({
							markdownParagraph(EXPERIENCE_SUMMARY.trimIndent(), true, AppStyle.monoFont)
							style {
								marginBottom(2.cssRem)
							}
						})

						A("/experiences/", "View my experiences", AppStyle.button)
					}
				}
			}

			// About Me Section
			Section({
				classes(HomeStyle.section, HomeStyle.aboutSection)
			}) {
				H2({
					classes(HomeStyle.sectionTitle)
				}) {
					I(FontAwesomeType.SOLID, "user") {
						marginRight(0.5.cssRem)
					}
					Text("About Me")
				}

				P({
					markdownParagraph("""
						Discover my journey in programming, from my first steps with Python in 2014 to my current projects with Kotlin and AI.
						Learn about my passion for Minecraft, my experience with various technologies, and my educational path.
					""".trimIndent(), true, AppStyle.monoFont)
				})

				A("/about-me/", "Read my story", AppStyle.button)
			}

			// Contact Section
			Section({
				classes(HomeStyle.section, HomeStyle.contactSection)
			}) {
				H2({
					classes(HomeStyle.sectionTitle)
				}) {
					I(FontAwesomeType.SOLID, "envelope") {
						marginRight(0.5.cssRem)
					}
					Text("Get In Touch")
				}

				Div({
					classes(HomeStyle.contactLinks)
				}) {
					A("https://github.com/Ayfri", {
						classes(HomeStyle.contactLink)
					}) {
						I(FontAwesomeType.BRAND, "github") {
							fontSize(1.75.cssRem)
						}
						Span("GitHub")
					}

					A("https://www.linkedin.com/in/pierre-roy-ayfri/", {
						classes(HomeStyle.contactLink)
					}) {
						I(FontAwesomeType.BRAND, "linkedin") {
							fontSize(1.75.cssRem)
						}
						Span("LinkedIn")
					}

					A("mailto:pierre@ayfri.com", {
						classes(HomeStyle.contactLink)
					}) {
						I(FontAwesomeType.SOLID, "envelope") {
							fontSize(1.75.cssRem)
						}
						Span("Email")
					}
				}
			}
		}
	}
}

object HomeStyle : StyleSheet() {
	init {
		id("main") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			background(linearGradient(180.deg) {
				stop(Color("#1D1D1E"), (-3).percent)
				stop(Color("#111629"), 14.percent)
				stop(Color("#29183F"), 65.percent)
				stop(Color("#302F39"), 90.percent)
			})

			padding(1.cssRem, 8.5.vw)
		}

		media(Only(MediaType(Screen), mediaMaxWidth(AppStyle.mobileFirstBreak))) {
			id("main") style {
				paddingLeft(5.vw)
				paddingRight(5.vw)
			}
		}

		media(Only(MediaType(Screen), mediaMaxWidth(AppStyle.mobileFourthBreak))) {
			id("main") style {
				paddingLeft(2.vw)
				paddingRight(2.vw)
			}
		}
	}

	val top by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign(TextAlign.Center)
		maxWidth(100.percent)
	}

	val topInfo by style {
		height(80.percent)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)

		universal style {
			marginTop(.2.cssRem)
			marginBottom(.2.cssRem)
		}

		"h1" style {
			fontSize(2.cssRem)
			display(DisplayStyle.Flex)
			alignItems(AlignItems.Baseline)
			gap(.5.cssRem)

			universal style {
				fontSize(1.5.cssRem)
				fontWeight(400)
			}

			lastChild style {
				fontSize(1.8.cssRem)
				fontWeight(700)
			}
		}

		"h2" style {
			fontSize(1.6.cssRem)
			fontWeight(400)
		}

		"h3" {
			fontWeight(400)
			marginBottom(1.cssRem)
		}
	}

	val sectionTitle by style {
		background(linearGradient(20.deg) {
			stop(Color(PortfolioStyle.TITLE_GRADIENT_START))
			stop(Color(PortfolioStyle.TITLE_GRADIENT_END))
		})

		fontSize(2.2.cssRem)
		marginTop(0.px)
		marginBottom(1.5.cssRem)
		textAlign(TextAlign.Center)

		property("-webkit-background-clip", "text")
		property("-webkit-text-fill-color", "transparent")
		property("-moz-text-fill-color", "transparent")
		property("-moz-background-clip", "text")

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				fontSize(1.8.cssRem)
			}
		}
	}

	val section by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.5.cssRem)
		marginTop(3.cssRem)
		padding(1.5.cssRem)
		width(100.percent)
		maxWidth(1200.px)
		borderRadius(1.cssRem)
		backgroundColor(Color("#ffffff10"))
		boxShadow(Color("#00000040"), 0.px, 4.px, 12.px)

		className("skills") style {
			flexWrap(FlexWrap.Wrap)
			gap(1.cssRem)
		}

		className("repos") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Row)
			justifyContent(JustifyContent.Center)
			gap(1.5.cssRem)
			width(100.percent)
		}

		className("list") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Row)
			justifyContent(JustifyContent.Center)
			flexWrap(FlexWrap.Wrap)
			width(100.percent)
		}

		media(Only(MediaType(Screen), mediaMaxWidth(AppStyle.mobileFirstBreak))) {
			self {
				padding(1.cssRem)
			}

			className("repos") style {
				flexDirection(FlexDirection.Column)
				alignItems(AlignItems.Center)

				className("repo") style {
					maxWidth(95.percent)
				}
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val skill by style {
		animation(AnimationsStyle.appear) {
			duration(1.s)
			fillMode(AnimationFillMode.Forwards)
			timingFunction(AnimationTimingFunction.EaseInOut)
		}
		opacity(0)

		backgroundColor(Color("#252525"))
		borderRadius(.4.cssRem)
		color(Color.white)
		padding(.3.cssRem, .5.cssRem)
		transitions {
			defaultDuration(0.3.s)
			properties("all")
		}

		hover(self) style {
			backgroundColor(Color("#1D1D1E"))
			boxShadow(Color("#00000060"), 0.px, 4.px, 8.px)
			scale(1.05)
		}

		"img" {
			borderRadius(.4.cssRem)
			size(3.5.cssRem)
		}
	}

	val portfolioSection by style {
		backgroundColor(Color("#ffffff10"))
	}

	val portfolioContent by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		alignItems(AlignItems.Center)
		gap(2.cssRem)
		width(100.percent)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				flexDirection(FlexDirection.Column)
			}
		}
	}

	val portfolioText by style {
		flex(1)
		"p" {
			lineHeight(1.5.cssRem)
		}
	}

	val portfolioImage by style {
		borderRadius(0.8.cssRem)
		property("box-shadow", "0px 0px 10px 2px #71A0E8")
		maxWidth(40.percent)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				maxWidth(80.percent)
			}
		}
	}

	val experienceSection by style {
		backgroundColor(Color("#ffffff10"))
	}

	val experienceContent by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		alignItems(AlignItems.Center)
		gap(2.cssRem)
		width(100.percent)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				flexDirection(FlexDirection.Column)
			}
		}
	}

	val experienceText by style {
		flex(1)
		"p" {
			lineHeight(1.5.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val experienceImage by style {
		maxWidth(30.percent)
		borderRadius(0.8.cssRem)
		filter {
			dropShadow(offsetX = 2.px, offsetY = 0.px, blurRadius = 4.px, color = Color("#71A0E8"))
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				maxWidth(60.percent)
			}
		}
	}

	val aboutSection by style {
		backgroundColor(Color("#ffffff10"))
	}

	val contactSection by style {
		backgroundColor(Color("#ffffff10"))
	}

	val contactLinks by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		justifyContent(JustifyContent.Center)
		gap(2.cssRem)
		marginTop(1.cssRem)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				flexDirection(FlexDirection.Column)
				gap(1.cssRem)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val contactLink by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(0.5.cssRem)
		padding(1.cssRem)
		borderRadius(0.5.cssRem)
		backgroundColor(Color("#252525"))
		color(Color.white)
		textDecoration("none")

		transitions {
			defaultDuration(0.3.s)
			properties("all")
		}

		hover(self) style {
			backgroundColor(Color("#1D1D1E"))
			translateY((-5).px)
			boxShadow(Color("#00000060"), 0.px, 4.px, 8.px)
		}
	}
}
