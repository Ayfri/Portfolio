package io.github.ayfri.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.css.ObjectFit
import com.varabyte.kobweb.compose.css.boxShadow
import com.varabyte.kobweb.compose.css.objectFit
import io.github.ayfri.AnimationsStyle
import io.github.ayfri.AppStyle
import io.github.ayfri.components.A
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.P
import io.github.ayfri.externals.Prism
import io.github.ayfri.markdownParagraph
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
fun AvatarImage(owner: PartialUser) = Img(owner.avatarUrl, "${owner.login} avatar")

@Composable
fun ProjectCard(repository: GitHubRepository, onClick: AttrsScope<HTMLDivElement>.(SyntheticMouseEvent) -> Unit = {}) {
	var open by mutableStateOf(false)

	Div({
		classes(DataStyle.projectCard)
		id(repository.name)

		onClick {
			onClick(it)
		}
	}) {
		Div({
			classes("top", AppStyle.monoFont)
		}) {
			if (open) {
				AvatarImage(repository.owner)
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
		}

		Div({
			classes("bottom")
		}) {
			if (open) {
				val readmeContent = repository.readmeContent ?: repository.description ?: repository.name

				P({
					markdownParagraph(readmeContent) { element ->
						Prism.highlightAllUnder(element)
					}
				})
			} else {
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

private fun getLanguageColor(language: String): CSSColorValue {
	return Color(
		when (language.lowercase()) {
			"javascript" -> "#f1e05a"
			"typescript" -> "#3178c6"
			"html" -> "#e34c26"
			"css" -> "#563d7c"
			"python" -> "#3572A5"
			"java" -> "#b07219"
			"kotlin" -> "#A97BFF"
			"go", "golang" -> "#00ADD8"
			"rust" -> "#dea584"
			"c#" -> "#178600"
			"c++" -> "#f34b7d"
			"php" -> "#4F5D95"
			"ruby" -> "#701516"
			"swift" -> "#ffac45"
			"dart" -> "#00B4AB"
			"shell" -> "#89e051"
			else -> "#8257e6" // Default purple color
		}
	)
}

object DataStyle : StyleSheet() {
	// Color constants
	const val HOME_CARD_BACKGROUND = "#181818"
	const val HOME_CARD_TITLE_BACKGROUND = "#0e0e0e"
	const val HOME_CARD_COLOR = "#cacaca"
	const val PROJECT_CARD_CLOSED_BACKGROUND = "#2a2b36"
	const val PROJECT_CARD_HOVER_BACKGROUND = "#353648"
	const val TOPIC_TAG_BACKGROUND = "#B4BBFF20"
	const val TOPIC_TAG_COLOR = AppStyle.SPECIAL_TEXT_COLOR
	const val TEXT_SECONDARY = "#ffffffaa"

	val gridColumnStartVar by variable<StylePropertyNumber>()
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

	@OptIn(ExperimentalComposeWebApi::class)
	val projectCard by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color(PROJECT_CARD_CLOSED_BACKGROUND))
		borderRadius(0.75.cssRem)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.65.cssRem)
		imageSize(2.5.cssRem)
		padding(1.65.cssRem)

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			boxShadow(0.px, 5.px, 15.px, 0.px, Color("#00000030"))
			backgroundColor(Color(PROJECT_CARD_HOVER_BACKGROUND))
			transform { translateY((-5).px) }
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
				maxHeight("none")
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
					backgroundColor(Color(TOPIC_TAG_BACKGROUND))
					color(Color(TOPIC_TAG_COLOR))
					padding(0.3.cssRem, 0.6.cssRem)
					borderRadius(1.cssRem)
					fontSize(0.8.cssRem)
				}

				className("topic-more") style {
					backgroundColor(Color("#ffffff15"))
					color(Color.white)
					padding(0.3.cssRem, 0.6.cssRem)
					borderRadius(1.cssRem)
					fontSize(0.8.cssRem)
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
