package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.radialGradient
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
import io.github.ayfri.utils.margin
import io.github.ayfri.utils.n
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType.Enum.Screen
import org.jetbrains.compose.web.css.CSSMediaQuery.Only
import org.jetbrains.compose.web.css.JustifyContent
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

const val ARTICLES_SUMMARY = """
Explore my blog where I share insights, tutorials, and experiences in programming, particularly focusing on Kotlin, Minecraft modding,
and technical deep dives. Learn about my journey and discoveries in software development.
"""

@Page("/index")
@Composable
fun Home() {
	PageLayout("Home") {
		val homeRepositories = gitHubData.repos.sortedBy { it.stargazersCount }.reversed().take(3)
		val featuredSkills =
			skills.sortedWith(compareByDescending<Skill> { it.language.level }.thenByDescending { it.language.since }).take(8)

		Style(HomeStyle)
		Style(DataStyle)

		// Decorative background elements
		Div({
			classes(HomeStyle.backgroundDecorations)
		}) {
			// Create floating elements with predefined positions
			val positions = listOf(
				Pair(10, 20), Pair(80, 15), Pair(15, 70), Pair(90, 60),
				Pair(30, 10), Pair(70, 80), Pair(5, 45), Pair(85, 30),
				Pair(40, 65), Pair(60, 25), Pair(20, 85), Pair(75, 50),
				Pair(50, 40), Pair(35, 75), Pair(65, 10), Pair(25, 55),
				Pair(95, 75), Pair(45, 20), Pair(55, 90), Pair(12, 35)
			)

			positions.forEachIndexed { index, (left, top) ->
				Div({
					classes(HomeStyle.floatingElement)
					style {
						property("animation-delay", "${(index * 0.5)}s")
						left(left.percent)
						top(top.percent)
					}
				})
			}
		}

		Section({
			classes(HomeStyle.top)
		}) {
			// Hero background with glowing effects
			Div({
				classes(HomeStyle.heroBackground)
			}) {
				Div({ classes(HomeStyle.glowOrb, HomeStyle.glowOrb1) })
				Div({ classes(HomeStyle.glowOrb, HomeStyle.glowOrb2) })
				Div({ classes(HomeStyle.glowOrb, HomeStyle.glowOrb3) })
			}

			Div({
				classes(HomeStyle.topInfo)
			}) {
				Div({
					classes(HomeStyle.avatarContainer)
				}) {
					Img(localImage("avatar@300x300.webp"), "avatar") {
						classes(AppStyle.avatar, HomeStyle.enhancedAvatar)
						height(300)
						width(300)
					}
					Div({
						classes(HomeStyle.avatarGlow)
					})
				}

				H1({
					classes(HomeStyle.mainTitle)
				}) {
					Text("Pierre Roy")
					Span("alias")
					Span(" Ayfri")
				}

				H2({
					classes(HomeStyle.subtitle)
				}) {
					Text("IT Student")
				}

				H2({
					classes(HomeStyle.subtitle)
				}) {
					Text("France")
				}

				H3({
					classes(HomeStyle.ageInfo)
				}) {
					Text("Born ")

					Span({
						classes(AppStyle.monoFont, AppStyle.numberColor, HomeStyle.ageNumber)
					}) {
						Text(years.toInt().toString())
					}

					Text(" years ago")
				}
			}

			P({
				markdownParagraph(MAIN_PRESENTATION.trimIndent(), true, AppStyle.monoFont)
				classes(HomeStyle.introText)
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
					markdownParagraph(
						"""
                        Discover my journey in programming, from my first steps with Python in 2014 to my current projects with Kotlin and AI.
                        Learn about my passion for Minecraft, my experience with various technologies, and my educational path.
                    """.trimIndent(), true, AppStyle.monoFont
					)
				})

				A("/about-me/", "Read my story", AppStyle.button)
			}

			// Articles Section
			Section({
				classes(HomeStyle.section, HomeStyle.articlesSection)
			}) {
				H2({
					classes(HomeStyle.sectionTitle)
				}) {
					I(FontAwesomeType.SOLID, "newspaper") {
						marginRight(0.5.cssRem)
					}
					Text("My Articles")
				}

				P({
					markdownParagraph(ARTICLES_SUMMARY.trimIndent(), true, AppStyle.monoFont)
					style {
						marginBottom(2.cssRem)
					}
				})

				Div({
					classes("list", "articles")
				}) {
					val recentArticles = articlesEntries.sortedByDescending { it.date }.take(3)
					recentArticles.forEachIndexed { index, article ->
						Div({
							classes(HomeStyle.articlePreview)
							style {
								property("animation-delay", (index * 0.2).s)
							}
						}) {
							A(article.path, {
								classes(HomeStyle.articleLink)
							}) {
								H3 {
									Text(article.navTitle)
								}

								P({
									classes(HomeStyle.articleDesc)
								}) {
									Text(article.desc)
								}

								Div({
									classes(HomeStyle.articleTags)
								}) {
									article.keywords.take(3).forEach { keyword ->
										Span({
											classes(HomeStyle.articleTag)
										}) {
											Text(keyword)
										}
									}

									if (article.keywords.size > 3) {
										Span({
											classes(HomeStyle.articleTagMore)
										}) {
											Text("+${article.keywords.size - 3}")
										}
									}
								}
							}
						}
					}
				}

				A("/articles/", "Read all articles", AppStyle.button)
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
	// CSS Animations keyframes
	@OptIn(ExperimentalComposeWebApi::class)
	val floatUpDown by keyframes {
		0.percent {
			opacity(0.4)
			transform {
				translateY(0.px)
				rotate(0.deg)
			}
		}
		50.percent {
			transform {
				translateY((-20).px)
				rotate(180.deg)
			}
		}
		100.percent {
			transform {
				translateY(0.px)
				rotate(360.deg)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val orbFloat by keyframes {
		0.percent {
			transform {
				translate(0.px, 0.px)
				scale(1)
			}
		}
		33.percent {
			transform {
				translate(30.px, (-30).px)
				scale(1.1)
			}
		}
		66.percent {
			transform {
				translate((-20).px, 20.px)
				scale(0.9)
			}
		}
		100.percent {
			transform {
				translate(0.px, 0.px)
				scale(1)
			}
		}
	}

	val avatarGlowAnimation by keyframes {
		0.percent {
			opacity(0.3)
		}
		50.percent {
			opacity(0.7)
		}
		100.percent {
			opacity(0.3)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val fadeInUp by keyframes {
		from {
			opacity(0)
			transform {
				translateY(30.px)
			}
		}
		to {
			opacity(1)
			transform {
				translateY(0.px)
			}
		}
	}

	init {
		id("main") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			backgroundImage(linearGradient(180.deg) {
				add(Color("#0A0A0F"), (-3).percent)
				add(Color("#1A1225"), 14.percent)
				add(Color("#2A1B3D"), 65.percent)
				add(Color("#1E1535"), 90.percent)
			})
			position(Position.Relative)
			minHeight(100.vh)
			overflow(Overflow.Hidden)

			padding(0.5.cssRem, 8.5.vw, 2.cssRem)
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
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(0.1.cssRem)

		"h2, h3" {
			margin(0.px)
		}
	}

	val sectionTitle by style {
		backgroundClip(BackgroundClip.Text)
		backgroundImage(linearGradient(45.deg) {
			add(Color("#00D4FF"))
			add(Color("#FF0080"))
		})

		fontSize(2.2.cssRem)
		marginTop(0.px)
		marginBottom(1.5.cssRem)
		textAlign(TextAlign.Center)
		property("text-shadow", "0 0 20px rgba(0, 212, 255, 0.5)")

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
		backgroundColor(Color("#1A1225"))
		backdropFilter(BackdropFilter.list(BackdropFilter.of(blur(10.px))))

		border(2.px, LineStyle.Solid, Color.transparent)
		backgroundImage("""
			linear-gradient(#1A1225, #1A1225) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		boxShadow("0 0 30px rgba(0, 212, 255, 0.15), inset 0 1px 1px rgba(255, 255, 255, 0.05)")

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
		border(1.px, LineStyle.Solid, Color.transparent)
		backgroundImage("""
			linear-gradient(#252525, #252525) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		transitions {
			defaultDuration(0.3.s)
			properties("all")
		}

		hover(self) style {
			backgroundColor(Color("#1D1D1E"))
			boxShadow("0 0 20px rgba(0, 212, 255, 0.4)")
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
		boxShadow("0px 0px 20px 4px rgba(0, 212, 255, 0.4)")
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
			dropShadow(offsetX = 2.px, offsetY = 0.px, blurRadius = 8.px, color = Color("#FF0080"))
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
		textDecorationLine(TextDecorationLine.None)
		border(1.px, LineStyle.Solid, Color.transparent)
		backgroundImage("""
			linear-gradient(#252525, #252525) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")

		transitions {
			defaultDuration(0.3.s)
			properties("all")
		}

		hover(self) style {
			backgroundColor(Color("#1D1D1E"))
			boxShadow("0 0 25px rgba(255, 0, 128, 0.5)")
			translateY((-5).px)
		}
	}

	val articlesSection by style {
		backgroundColor(Color("#ffffff10"))
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val articlePreview by style {
		backgroundColor(Color("#252525"))
		borderRadius(0.8.cssRem)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		height(280.px)
		margin(0.5.cssRem)
		minWidth(280.px)
		padding(1.5.cssRem)
		width(33.percent - 1.cssRem)

		animation(AnimationsStyle.appear) {
			duration(0.5.s)
			fillMode(AnimationFillMode.Forwards)
			timingFunction(AnimationTimingFunction.EaseInOut)
		}
		opacity(0)

		transitions {
			defaultDuration(0.3.s)
			properties("all")
		}

		hover(self) style {
			backgroundColor(Color("#2A2A2A"))
			boxShadow("0 0 30px rgba(0, 212, 255, 0.4)")
			transform {
				translateY((-5).px)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				width(50.percent - 1.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				width(100.percent)
			}
		}
	}

	val articleLink by style {
		color(Color.white)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		height(100.percent)
		textDecorationLine(TextDecorationLine.None)

		"h3" {
			fontSize(1.2.cssRem)
			marginTop(0.px)
			marginBottom(0.8.cssRem)
		}
	}

	val articleDesc by style {
		fontSize(0.9.cssRem)
		lineHeight(1.3.em)
		opacity(0.8)
		marginBottom(1.cssRem)
		flex(1)
		overflow(Overflow.Hidden)
	}

	val articleTags by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
		marginTop(autoLength)
	}

	val articleTag by style {
		backgroundColor(Color("#FFFFFF15"))
		borderRadius(1.cssRem)
		padding(0.3.cssRem, 0.8.cssRem)
		fontSize(0.75.cssRem)
		color(Color("#FFFFFFDD"))
	}

	val articleTagMore by style {
		backgroundColor(Color("#FFFFFF10"))
		borderRadius(1.cssRem)
		padding(0.3.cssRem, 0.8.cssRem)
		fontSize(0.75.cssRem)
		color(Color("#FFFFFFAA"))
	}

	val backgroundDecorations by style {
		position(Position.Fixed)
		top(0.px)
		left(0.px)
		width(100.percent)
		height(100.percent)
		pointerEvents(PointerEvents.None)
		overflow(Overflow.Hidden)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val floatingElement by style {
		position(Position.Absolute)
		width(6.px)
		height(6.px)
		borderRadius(50.percent)
		backgroundColor(Color("#ffffff90"))
		boxShadow("0 0 12px rgba(255, 255, 255, 0.3)")
		opacity(0.04)

		animation(floatUpDown) {
			duration(25.s)
			iterationCount(Int.MAX_VALUE)
			direction(AnimationDirection.Alternate)
			timingFunction(AnimationTimingFunction.EaseInOut)
		}

		nthChild(2.n) style {
			backgroundColor(Color("#00D4FF"))
			boxShadow("0 0 8px rgba(0, 212, 255, 1)")
			opacity(0.05)
		}

		nthChild(3.n) style {
			width(4.px)
			height(4.px)
			backgroundColor(Color("#FF0080"))
			boxShadow("0 0 6px rgba(255, 0, 128, 1)")
			opacity(0.08)
			property("animation-delay", "8s")
		}

		nthChild(4.n) style {
			width(8.px)
			height(8.px)
			backgroundColor(Color("#8A2BE2"))
			boxShadow("0 0 7px rgba(138, 43, 226, 1)")
			opacity(0.02)
			property("animation-delay", "15s")
		}

		nthChild(5.n) style {
			width(3.px)
			height(3.px)
			backgroundColor(Color("#ffffff"))
			boxShadow("0 0 5px rgba(255, 255, 255, 1)")
			opacity(0.1)
			property("animation-delay", "20s")
		}
	}

	val heroBackground by style {
		position(Position.Absolute)
		top(0.px)
		left(0.px)
		width(100.percent)
		height(100.percent)
		pointerEvents(PointerEvents.None)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val glowOrb by style {
		position(Position.Absolute)
		borderRadius(50.percent)
		filter {
			blur(60.px)
		}
		opacity(0.5)
		animation(orbFloat) {
			duration(30.s)
			iterationCount(Int.MAX_VALUE)
			direction(AnimationDirection.Alternate)
			timingFunction(AnimationTimingFunction.EaseInOut)
		}
	}

	val glowOrb1 by style {
		width(200.px)
		height(200.px)
		backgroundImage(radialGradient {
			add(Color("#00D4FF30"))
			add(Color.transparent, 50.percent)
		})
		left((-100).px)
		top((-100).px)
		property("animation-delay", "0s")
	}

	val glowOrb2 by style {
		width(250.px)
		height(250.px)
		backgroundImage(radialGradient {
			add(Color("#FF008030"))
			add(Color.transparent, 50.percent)
		})
		right((-125).px)
		top(30.px)
		property("animation-delay", "15s")
	}

	val glowOrb3 by style {
		width(180.px)
		height(180.px)
		backgroundImage(radialGradient {
			add(Color("#8A2BE230"))
			add(Color.transparent, 50.percent)
		})
		left(50.percent)
		bottom((-90).px)
		property("animation-delay", "8s")
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val avatarContainer by style {
		position(Position.Relative)
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		marginBottom(0.5.cssRem)

		animation(AnimationsStyle.appear) {
			duration(1.5.s)
			fillMode(AnimationFillMode.Forwards)
			timingFunction(AnimationTimingFunction.EaseOut)
		}
		opacity(0)
		translateY(30.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val avatarGlow by style {
		position(Position.Absolute)
		top((-20).px)
		left((-20).px)
		width(340.px)
		height(340.px)
		borderRadius(50.percent)
		backgroundImage(radialGradient {
			add(Color("#00D4FF30"))
			add(Color("#FF008020"), 50.percent)
			add(Color.transparent, 70.percent)
		})
		filter {
			blur(30.px)
		}
		zIndex(-1)
		animation(avatarGlowAnimation) {
			duration(4.s)
			iterationCount(Int.MAX_VALUE)
			direction(AnimationDirection.Alternate)
			timingFunction(AnimationTimingFunction.EaseInOut)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val mainTitle by style {
		fontSize(3.cssRem)
		fontWeight(700)
		marginBottom(0.cssRem)
		textAlign(TextAlign.Center)
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Baseline)
		justifyContent(JustifyContent.Center)
		gap(0.4.cssRem)
		flexWrap(FlexWrap.Wrap)

		backgroundClip(BackgroundClip.Text)
		backgroundImage(linearGradient(45.deg) {
			add(Color("#00D4FF"))
			add(Color("#FF0080"))
			add(Color("#8A2BE2"))
		})
		property("-webkit-background-clip", "text")
		property("-webkit-text-fill-color", "transparent")
		property("-moz-text-fill-color", "transparent")
		property("-moz-background-clip", "text")
		property("text-shadow", """
			0 0 10px rgba(0, 212, 255, 0.5),
			0 0 20px rgba(255, 0, 128, 0.3),
			0 0 30px rgba(138, 43, 226, 0.2)
		""")

		// Style for "alias"
		child(self, type("span") + firstOfType) style {
			fontSize(1.5.cssRem)
			fontWeight(300)
			background(Background.None)
			color(Color("#FFFFFFAA"))
			property("-webkit-text-fill-color", "#FFFFFFAA")
			property("-moz-text-fill-color", "#FFFFFFAA")
			property("text-shadow", "0 0 8px rgba(255, 255, 255, 0.3)")
			opacity(0.7)
		}

		// Style for "Ayfri"
		child(self, type("span") + lastOfType) style {
			fontSize(2.8.cssRem)
			fontWeight(700)
			backgroundClip(BackgroundClip.Text)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#FF0080"))
				add(Color("#8A2BE2"))
			})
			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
			property("-moz-text-fill-color", "transparent")
			property("-moz-background-clip", "text")
			property("text-shadow", """
				0 0 15px rgba(255, 0, 128, 0.6),
				0 0 25px rgba(138, 43, 226, 0.4)
			""")
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				fontSize(2.2.cssRem)
				flexDirection(FlexDirection.Column)
				gap(0.2.cssRem)
			}

			child(self, type("span") + firstOfType) style {
				fontSize(1.2.cssRem)
			}

			child(self, type("span") + lastOfType) style {
				fontSize(2.cssRem)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val subtitle by style {
		fontSize(1.8.cssRem)
		fontWeight(300)
		marginBottom(0.1.cssRem)
		color(Color("#FFFFFFCC"))
		textAlign(TextAlign.Center)
		animation(fadeInUp) {
			duration(1.s)
			fillMode(AnimationFillMode.Forwards)
			timingFunction(AnimationTimingFunction.EaseOut)
		}
		opacity(0)
		translateY(20.px)
		property("animation-delay", "0.5s")
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val ageInfo by style {
		fontWeight(300)
		marginBottom(1.cssRem)
		fontSize(1.2.cssRem)
		textAlign(TextAlign.Center)
		color(Color("#FFFFFF99"))
		animation(fadeInUp) {
			duration(1.s)
			fillMode(AnimationFillMode.Forwards)
			timingFunction(AnimationTimingFunction.EaseOut)
		}
		opacity(0)
		translateY(20.px)
		property("animation-delay", "0.8s")
	}

	val ageNumber by style {
		fontWeight(700)
		color(Color("#00D4FF"))
		property("text-shadow", "0 0 10px rgba(0, 212, 255, 0.8)")
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val introText by style {
		lineHeight(1.5.cssRem)
		fontSize(1.1.cssRem)
		maxWidth(800.px)
		marginLeft(autoLength)
		marginRight(autoLength)
		marginTop(0.px)
		marginBottom(2.cssRem)
		textAlign(TextAlign.Center)
		color(Color("#FFFFFFEE"))
		padding(1.2.cssRem)
		borderRadius(0.8.cssRem)
		backgroundColor(Color("#ffffff08"))
		border(1.px, LineStyle.Solid, Color("#ffffff20"))
		backdropFilter(BackdropFilter.list(BackdropFilter.of(blur(10.px))))
		animation(fadeInUp) {
			duration(1.2.s)
			fillMode(AnimationFillMode.Forwards)
			timingFunction(AnimationTimingFunction.EaseOut)
		}
		opacity(0)
		translateY(30.px)
		property("animation-delay", "1.2s")

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				fontSize(1.cssRem)
				padding(1.cssRem)
				margin(0.8.cssRem, auto)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val enhancedAvatar by style {
		borderRadius(1.cssRem)
		border(3.px, LineStyle.Solid, Color.transparent)
		backgroundImage( """
			linear-gradient(white, white) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080, #8A2BE2) border-box
		""")
		boxShadow( """
			0 0 30px rgba(0, 212, 255, 0.6),
			0 0 60px rgba(255, 0, 128, 0.4),
			inset 0 1px 1px rgba(255, 255, 255, 0.2)
		""")
		transitions {
			defaultDuration(0.3.s)
			properties("all")
		}

		hover(self) style {
			boxShadow( """
				0 0 40px rgba(0, 212, 255, 0.8),
				0 0 80px rgba(255, 0, 128, 0.6),
				inset 0 1px 1px rgba(255, 255, 255, 0.3)
			""")
		}
	}
}
