package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.varabyte.kobweb.core.Page
import io.github.ayfri.*
import io.github.ayfri.data.DataStyle
import io.github.ayfri.data.GitHubRepository
import io.github.ayfri.data.HomeCard
import io.github.ayfri.data.data
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.TextAlign
import io.github.ayfri.utils.linearGradient
import io.github.ayfri.utils.size
import io.github.ayfri.utils.textAlign
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType.Enum.Screen
import org.jetbrains.compose.web.css.CSSMediaQuery.Only
import org.jetbrains.compose.web.dom.*
import kotlin.js.Date

inline val years get() = (Date.now() - Date("2002-10-15").getTime()) / 1000 / 60 / 60 / 24 / 365

const val MAIN_PRESENTATION = """
Hi, I'm Pierre Roy, an IT student at [Ynov Aix school](https://www.ynov.com/campus/aix-en-provence/), and I'm passionate about computer science and especially programming.
I'm making all sorts of projects and programming by myself for years. This is my portfolio, welcome!
"""

@Page
@Composable
fun Home() {
	PageLayout("Home") {
		val homeRepositories = remember { mutableStateListOf<GitHubRepository>() }

		Style(HomeStyle)
		Style(DataStyle)

		Section({
			classes(HomeStyle.top)
		}) {
			Div({
				classes(HomeStyle.topInfo)
			}) {
				Img(localImage("avatar.webp"), "avatar") {
					classes(AppStyle.avatar)
					fetchPriority(Priority.HIGH)
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
			})

			if (homeRepositories.isEmpty()) {
				data.then { gitHubData ->
					homeRepositories += gitHubData.repos.sortedBy {
						it.stargazersCount
					}.reversed().take(3)
				}
			}

			Section({
				classes(HomeStyle.section)
			}) {
				Div({
					classes("list", "repos")
				}) {
					homeRepositories.forEach {
						Div({
							classes(DataStyle.homeCard, "repo")
						}) {
							HomeCard(it)
						}
					}
				}

				A("/projects", "See all projects", AppStyle.button)
			}

			Section({
				classes(HomeStyle.section)
			}) {
				Div({
					classes("list", "skills")
				}) {
					skills.sortedWith(
						compareByDescending<Skill> { it.language.level }.thenByDescending { it.language.since }
					).take(8).forEach {
						A("/skills#${it.language.name}", {
							classes("skill")
						}) {
							it.DisplaySimple()
						}
					}
				}

				A("/skills", "See all skills", AppStyle.button)
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

	val section by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.5.cssRem)
		marginTop(3.cssRem)
		padding(0.px, 1.cssRem)

		className("skills") style {
			flexWrap(FlexWrap.Wrap)
		}

		className("list") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Row)
			justifyContent(JustifyContent.Center)
			gap(1.cssRem)

			className("skill") style {
				backgroundColor(Color("#252525"))
				borderRadius(.4.cssRem)
				color(Color.white)
				padding(.3.cssRem, .5.cssRem)

				"img" {
					borderRadius(.4.cssRem)
					size(3.5.cssRem)
				}
			}
		}

		media(Only(MediaType(Screen), mediaMaxWidth(AppStyle.mobileFirstBreak))) {
			className("repos") style {
				flexDirection(FlexDirection.Column)
				alignItems(AlignItems.Center)

				className("repo") style {
					maxWidth(95.percent)
				}
			}
		}
	}
}
