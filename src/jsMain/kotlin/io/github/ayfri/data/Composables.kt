package io.github.ayfri.data

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.clamp
import io.github.ayfri.AnimationsStyle
import io.github.ayfri.AppStyle
import io.github.ayfri.components.A
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.P
import io.github.ayfri.pages.TextIcon
import io.github.ayfri.pages.skills
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.alt
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
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
fun AvatarImage(owner: PartialUser) = Img(owner.avatarUrl, "${owner.login} avatar")

@Composable
fun ProjectCard(repository: GitHubRepository) {
	A("/projects/${repository.owner.login}/${repository.name}", {
		classes(DataStyle.projectCard)
		id(repository.name)
	}) {
		Div({
			classes("top", AppStyle.monoFont)
		}) {
			Div({
				classes("card-header")
			}) {
				AvatarImage(repository.owner)

				Div({
					classes("repo-info")
				}) {
					H3 { Text(repository.name) }

					repository.language?.let {
						Span({
							classes("language")
						}) {
							Div({
								classes("language-dot")
								style {
									backgroundColor(getLanguageColor(it))
								}
							})
							Text(it)
						}
					}
				}

				Div({
					classes("stats")
				}) {
					TextIcon(repository.stargazersCount.toString(), FontAwesomeType.SOLID, "star")
					TextIcon(repository.forksCount.toString(), FontAwesomeType.SOLID, "code-branch")
				}
			}
		}

		Div({
			classes("bottom")
		}) {
			P({
				classes("description")
			}) {
				Text(repository.description ?: "No description provided.")
			}

			Div({
				classes("topics")
			}) {
				repository.topics.take(3).forEach { topic ->
					Span({
						classes("topic-tag")
					}) {
						Text(topic)
					}
				}

				if (repository.topics.size > 3) {
					Span({
						classes("topic-more")
					}) {
						Text("+${repository.topics.size - 3}")
					}
				}
			}

			Div({
				classes("footer")
			}) {
				Span({
					classes("updated")
				}) {
					I({
						classes("fas", "fa-history")
					})
					Text(" Updated ${formatRelativeTime(repository.updatedAt)}")
				}
			}
		}
	}
}

private fun formatRelativeTime(dateString: String): String {
	val date = Date(dateString)
	val now = Date()
	val diffMs = now.getTime() - date.getTime()
	val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()

	return when {
		diffDays == 0 -> "today"
		diffDays == 1 -> "yesterday"
		diffDays < 30 -> "$diffDays days ago"
		diffDays < 365 -> "${(diffDays / 30)} months ago"
		else -> "${(diffDays / 365)} years ago"
	}
}

private fun getLanguageColor(language: String) =  Color(
	when (language.lowercase()) {
		"astro" -> "#e142c9"
		"c#" -> "#178600"
		"c++" -> "#f34b7d"
		"css" -> "#563d7c"
		"dart" -> "#00B4AB"
		"gdscript" -> "#518bc1"
		"go", "golang" -> "#00ADD8"
		"html" -> "#e34c26"
		"java" -> "#b07219"
		"javascript" -> "#f1e05a"
		"kotlin" -> "#A97BFF"
		"php" -> "#4F5D95"
		"python" -> "#3572A5"
		"ruby" -> "#701516"
		"rust" -> "#dea584"
		"shell" -> "#89e051"
		"svelte" -> "#f44108"
		"swift" -> "#ffac45"
		"typescript" -> "#3178c6"
		"vue" -> "#54ba82"
		else -> "#8257e6" // Default purple color
	}
)


object DataStyle : StyleSheet() {
	// Color constants
	const val HOME_CARD_BACKGROUND = "#181818"
	const val HOME_CARD_TITLE_BACKGROUND = "#0e0e0e"
	const val HOME_CARD_COLOR = "#cacaca"
	const val PROJECT_CARD_CLOSED_BACKGROUND = "#2a2b36"
	const val PROJECT_CARD_HOVER_BACKGROUND = "#353648"
	const val TEXT_SECONDARY = "#ffffffaa"

	val imageSize by variable<CSSSizeValue<*>>()

	@OptIn(ExperimentalComposeWebApi::class)
	val homeCard by style {
		animation(AnimationsStyle.appearFromBelow) {
			fillMode(AnimationFillMode.Forwards)
			duration(1.s)
			timingFunction(AnimationTimingFunction.cubicBezier(0.4, 0.0, 0.2, 1.0))
		}
		opacity(0)

		borderRadius(.8.cssRem)
		overflow(Overflow.Hidden)
		width(clamp(17.5.cssRem, 25.vw, 25.cssRem))

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		justifyContent(JustifyContent.SpaceBetween)

		border {
			width(1.px)
			style(LineStyle.Solid)
			color(Color.transparent)
		}
		background("""
			linear-gradient(${HOME_CARD_BACKGROUND}, ${HOME_CARD_BACKGROUND}) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		property("box-shadow", "0 0 15px rgba(0, 212, 255, 0.08)")

		className("title") style {
			color(Color.white)

			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			justifyContent(JustifyContent.Center)

			backgroundColor(Color(HOME_CARD_TITLE_BACKGROUND))
			border(1.px, LineStyle.Solid, Color.transparent)
			padding(1.cssRem)
			background("""
				linear-gradient(${HOME_CARD_TITLE_BACKGROUND}, ${HOME_CARD_TITLE_BACKGROUND}) padding-box,
				linear-gradient(45deg, #00D4FF, #FF0080) border-box
			""")

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

		child(self, type("div")) style {
			backgroundColor(Color(HOME_CARD_BACKGROUND))
			textAlign(TextAlign.Start)

			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			justifyContent(JustifyContent.SpaceBetween)
			gap(3.cssRem)

			border(1.px, LineStyle.Solid, Color.transparent)
			height(100.percent)
			padding(1.cssRem)
			background("""
				linear-gradient(${HOME_CARD_BACKGROUND}, ${HOME_CARD_BACKGROUND}) padding-box,
				linear-gradient(45deg, #00D4FF, #FF0080) border-box
			""")
		}

		combine(self, className("active")) style {
			transform {
				scale(1.05)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val projectCard by style {
		imageSize(2.5.cssRem)

		alignItems(AlignItems.Center)
		borderRadius(0.75.cssRem)
		color(Color.white)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
		padding(1.5.cssRem)

		border(1.px, LineStyle.Solid, Color.transparent)
		background("""
			linear-gradient(${PROJECT_CARD_CLOSED_BACKGROUND}, ${PROJECT_CARD_CLOSED_BACKGROUND}) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		property("box-shadow", "0 0 15px rgba(0, 212, 255, 0.08)")

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			background("""
				linear-gradient(${PROJECT_CARD_HOVER_BACKGROUND}, ${PROJECT_CARD_HOVER_BACKGROUND}) padding-box,
				linear-gradient(45deg, #00D4FF, #FF0080) border-box
			""")
			transform { translateY((-5).px) }
			property("box-shadow", "0 0 20px rgba(255, 0, 128, 0.2)")
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				padding(1.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			self {
				imageSize(2.cssRem)
				height(auto)
				maxHeight(MaxHeight.None)
			}
		}

		className("top") style {
			width(100.percent)

			"img" {
				size(imageSize.value())
				borderRadius(50.percent)
				objectFit(ObjectFit.Cover)
			}
		}

		className("card-header") style {
			display(DisplayStyle.Flex)
			alignItems(AlignItems.Center)
			width(100.percent)
			gap(1.cssRem)

			className("repo-info") style {
				flex(1)
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
				gap(0.25.cssRem)

				"h3" {
					margin(0.px)
					fontSize(1.2.cssRem)
					fontWeight(600)
				}

				className("language") style {
					display(DisplayStyle.Flex)
					alignItems(AlignItems.Center)
					gap(0.5.cssRem)
					fontSize(0.9.cssRem)
					color(Color(TEXT_SECONDARY))

					className("language-dot") style {
						size(0.8.cssRem)
						borderRadius(50.percent)
					}
				}
			}

			className("stats") style {
				display(DisplayStyle.Flex)
				gap(1.cssRem)
				color(Color(TEXT_SECONDARY))
				fontSize(1.cssRem)
			}
		}

		className("bottom") style {
			width(100.percent)
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(0.65.cssRem)

			className("description") style {
				margin(0.px)
				fontSize(0.95.cssRem)
				lineHeight(1.5.number)
				color(Color.white)
				property("display", "-webkit-box")
				property("-webkit-line-clamp", "2")
				property("-webkit-box-orient", "vertical")
				overflow(Overflow.Hidden)
			}

			className("topics") style {
				display(DisplayStyle.Flex)
				flexWrap(FlexWrap.Wrap)
				gap(0.5.cssRem)

				className("topic-tag") style {
					padding(0.3.cssRem, 0.6.cssRem)
					color(Color.white)
					border {
						width(2.px)
						style(LineStyle.Solid)
						color(Color.transparent)
					}
					borderRadius(1.cssRem)
					fontSize(0.8.cssRem)
					cursor(Cursor.Pointer)
					background("""
						linear-gradient(#1A1225, #1A1225) padding-box,
						linear-gradient(45deg, #00D4FF, #FF0080) border-box
					""")
				}

				className("topic-more") style {
					border(1.px, LineStyle.Solid, Color.transparent)
					borderRadius(1.cssRem)
					color(Color.white)
					fontSize(0.8.cssRem)
					padding(0.3.cssRem, 0.6.cssRem)
					background("""
						linear-gradient(#ffffff15, #ffffff15) padding-box,
						linear-gradient(45deg, #00D4FF, #FF0080) border-box
					""")
				}
			}

			className("footer") style {
				marginTop(0.5.cssRem)
				fontSize(0.85.cssRem)
				color(Color(TEXT_SECONDARY))
			}
		}
	}
}
