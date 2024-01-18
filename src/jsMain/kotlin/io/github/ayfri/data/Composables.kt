package io.github.ayfri.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.web.events.SyntheticMouseEvent
import io.github.ayfri.*
import io.github.ayfri.pages.TextIcon
import io.github.ayfri.pages.skills
import io.github.ayfri.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.alt
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import kotlin.js.Date

@Composable
fun HomeCard(repository: GitHubRepository) {
	A("/projects/#${repository.name}", {
		classes("title")
	}) {
		H3 {
			Img(skills.first { it.language.name == repository.language }.language.iconUrl) {
				alt("${repository.language} language")
			}
			Text(repository.name)
		}

		Span({
			style {
				display(DisplayStyle.Flex)
				alignItems(AlignItems.Center)
				gap(.5.cssRem)
				marginTop(.5.cssRem)
			}
		}) {
			TextIcon(repository.stargazersCount.toString(), FontAwesomeType.SOLID, "star", Color("#ffe218"))
		}
	}

	Div {
		P(repository.description ?: "No description provided.")

		P {
			Text("GitHub URL: ")

			A(repository.htmlUrl, repository.htmlUrl.substringAfter("github.com/"))
		}
	}
}

@Composable
fun ProjectCard(repository: GitHubRepository, onClick: AttrsScope<HTMLDivElement>.(SyntheticMouseEvent) -> Unit = {}) {
	var open by mutableStateOf(false)

	Div({
		classes(if (open) DataStyle.projectCardOpen else DataStyle.projectCardClosed, DataStyle.projectCard)
		id(repository.name)

		onClick {
			open = !open
			onClick(it)
		}
	}) {
		Div({
			classes("top", AppStyle.monoFont)
		}) {
			if (open) {
				Img(src = repository.owner.avatarUrl, alt = "${repository.owner.login} avatar")
				H2 { Text(repository.name) }

				Div({
					classes("texts")
				}) {
					P({
						val creationDate = Date(repository.createdAt)
						val day = creationDate.getDate().toString().padStart(2, '0')
						val month = (creationDate.getMonth() + 1).toString().padStart(2, '0')
						val year = creationDate.getFullYear()
						val formatted = "$day/$month/$year"

						markdownParagraph(
							"""
							Creation date: <span>$formatted</span>
							Stars: <span>${repository.stargazersCount}</span>
							Commits: <span>${repository.commitsCount}</span>
							Contributors: <span>${repository.contributorsCount}</span>
						""".trimIndent(), true
						)
					})
				}
			} else {
				Img(src = repository.owner.avatarUrl)
				Div({
					classes("stars")
				}) {
					TextIcon(repository.stargazersCount.toString(), FontAwesomeType.SOLID, "star")
				}
			}
		}

		Div({
			classes("bottom")
		}) {
			if (open) {
				val readmeContent = repository.readmeContent ?: repository.description ?: repository.name

				P({
					markdownParagraph(readmeContent)
				})
			} else {
				H3 { Text(repository.name) }
				P(repository.description ?: "No description provided.")
			}
		}

		if (open) {
			A(repository.htmlUrl, {
				classes(AppStyle.button)
				target(ATarget.Blank)
			}) {
				Text("View on GitHub")
			}
		}
	}
}

object DataStyle : StyleSheet() {
	const val HOME_CARD_BACKGROUND = "#181818"
	const val HOME_CARD_TITLE_BACKGROUND = "#0e0e0e"
	const val HOME_CARD_COLOR = "#cacaca"
	const val PROJECT_CARD_CLOSED_BACKGROUND = "#2a2b36"
	const val PROJECT_CARD_OPEN_BACKGROUND_START = "#66085190"
	const val PROJECT_CARD_OPEN_BACKGROUND_END = "#39379490"

	val gridColumnStartVar by variable<StylePropertyNumber>()
	val imageSize by variable<CSSSizeValue<*>>()

	@OptIn(ExperimentalComposeWebApi::class)
	val homeCard by style {
		borderRadius(.8.cssRem)
		overflow(Overflow.Hidden)
		width(clamp(17.5.cssRem, 25.vw, 25.cssRem))

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		justifyContent(JustifyContent.SpaceBetween)

		className("title") style {
			color(Color.white)

			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			justifyContent(JustifyContent.Center)

			backgroundColor(Color(HOME_CARD_TITLE_BACKGROUND))
			padding(1.cssRem)

			"h3" {
				display(DisplayStyle.Flex)
				alignItems(AlignItems.Center)
				gap(.5.cssRem)

				margin(0.px)
			}

			"img" {
				size(1.3.cssRem)
			}
		}

		"p" {
			color(Color(HOME_CARD_COLOR))
		}

		child(self, selector("div")) style {
			backgroundColor(Color(HOME_CARD_BACKGROUND))
			textAlign(TextAlign.Start)

			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			justifyContent(JustifyContent.SpaceBetween)
			gap(3.cssRem)

			height(100.percent)
			padding(1.cssRem)
		}

		combine(self, className("active")) style {
			transform {
				scale(1.05)
			}
		}
	}

	val projectCard by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.cssRem)
		padding(clamp(1.6.cssRem, 2.vw, 3.cssRem))
		borderRadius(.75.cssRem)

		cursor(Cursor.Pointer)

		className("top") style {
			"img" {
				size(imageSize.value())
				borderRadius(imageSize.value())
			}
		}
	}

	val projectCardOpen by style {
		flexDirection(FlexDirection.Column)
		gap(2.cssRem)
		imageSize(5.cssRem)
		gridColumn("${gridColumnStartVar.value()} span")

		backgroundImage(linearGradient(135.deg) {
			stop(Color(PROJECT_CARD_OPEN_BACKGROUND_START))
			stop(Color(PROJECT_CARD_OPEN_BACKGROUND_END))
		})

		maxHeight(500.cssRem)

		className("top") style {
			textAlign(TextAlign.Center)

			"h2" {
				fontSize(2.5.cssRem)
				margin(1.cssRem, 0.px)
			}

			"p" {
				textAlign(TextAlign.Start)
				margin(0.px, 1.5.cssRem)
			}

			child(self, type("div")) style {
				margin(auto)
				width(fitContent)
			}

			inline fun subSpanColor(color: CSSColorValue, index: Int) {
				child(type("p"), type("span")) + nthOfType(index.n) style {
					color(color)
				}
			}

			subSpanColor(Color("#B4BBFF"), 1)
			subSpanColor(Color("#FFE24B"), 2)
			subSpanColor(Color("#75C9CE"), 3)
			subSpanColor(Color("#64E881"), 4)
		}

		className("bottom") style {
			backgroundColor(Color("#ffffff20"))
			borderRadius(.75.cssRem)
			padding(clamp(2.cssRem, 2.vw, 3.5.cssRem))
			maxWidth(90.percent)

			"h1" {
				marginTop(0.px)
			}

			"pre" {
				backgroundColor(Color("#00000020"))
				borderRadius(.5.cssRem)
				overflowX("auto")
				padding(1.cssRem)

				self + webkitScrollbar style {
					height(8.px)
				}

				self + webkitScrollbarThumb style {
					backgroundColor(Color("#ffffff40"))
					borderRadius(.5.cssRem)
				}

				self + webkitScrollbarTrack style {
					backgroundColor(Color.transparent)
				}
			}

			"blockquote" {
				borderLeft {
					color(Color("#ffffff20"))
					style(LineStyle.Solid)
					width(4.px)
				}
				marginLeft(1.5.cssRem)
				paddingLeft(.8.cssRem)
			}

			"table" {
				borderRadius(.5.cssRem)
				property("border-collapse", "collapse")
				property("border-spacing", "0")
				margin(1.cssRem, 0.px)
				fontSize(.9.cssRem)
				overflow(Overflow.Hidden)
				width(100.percent)

				"th" {
					backgroundColor(Color("#ffffff20"))
					padding(.5.cssRem)
				}

				"td" {
					padding(.5.cssRem)
				}

				"tr" {
					borderBottom {
						color(Color("#ffffff20"))
						style(LineStyle.Solid)
						width(1.px)
					}
				}
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			self {
				padding(clamp(.8.cssRem, 1.vw, 1.6.cssRem))
			}

			className("bottom") style {
				maxWidth(95.percent)
				padding(clamp(.8.cssRem, 1.vw, 2.cssRem))
			}
		}
	}

	val projectCardClosed by style {
		flexDirection(FlexDirection.Row)
		imageSize(3.5.cssRem)

		backgroundColor(Color(PROJECT_CARD_CLOSED_BACKGROUND))

		className("top") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			textAlign(TextAlign.Center)
			gap(.5.cssRem)

			className("stars") style {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.RowReverse)
				alignItems(AlignItems.Center)
				justifyContent(JustifyContent.Center)
				gap(.5.cssRem)
			}
		}

		className("bottom") style {
			group(type("h3"), type("p")) style {
				property("text-ellipsis", "ellipsis")
				margin(0.px)
			}

			"h3" {
				marginBottom(.5.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				padding(1.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			self {
				imageSize(3.cssRem)
				height(auto)
				maxHeight("none")

				"h3" {
					fontSize(1.1.cssRem)
				}
			}
		}
	}
}
