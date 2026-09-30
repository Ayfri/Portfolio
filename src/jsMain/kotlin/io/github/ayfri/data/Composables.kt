package io.github.ayfri.data

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.AppStyle
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.I
import io.github.ayfri.pages.TextIcon
import io.github.ayfri.utils.renderWhenVisible
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*
import kotlin.js.Date

@Composable
fun ProjectCard(repository: GitHubRepository) {
	A("/projects/${repository.owner.login}/${repository.name}", {
		classes(AppStyle.card, DataStyle.projectCard)
		id(repository.name)
	}) {
		Div({
			classes("card-header")
		}) {
			Img(repository.owner.avatarUrl, "${repository.owner.login} avatar")

			Div({
				classes("repo-info")
			}) {
				H3 { Text(repository.name) }

				repository.language?.let {
					Span({
						classes("language")
					}) {
						Span({
							classes("language-dot")
							style { backgroundColor(languageColor(it)) }
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

		P({
			classes("description")
		}) {
			Text(repository.description ?: "No description provided.")
		}

		if (repository.topics.isNotEmpty()) Div({
			classes("topics")
		}) {
			repository.topics.take(3).forEach { Span({ classes(AppStyle.tag) }) { Text(it) } }
			if (repository.topics.size > 3) Span({ classes(AppStyle.tag) }) { Text("+${repository.topics.size - 3}") }
		}

		Span({
			classes("updated")
		}) {
			I(FontAwesomeType.SOLID, "history")
			Text(" Updated ${formatRelativeTime(repository.updatedAt)}")
		}
	}
}

private fun formatRelativeTime(dateString: String): String {
	val diffDays = ((Date().getTime() - Date(dateString).getTime()) / (1000 * 60 * 60 * 24)).toInt()

	return when {
		diffDays == 0 -> "today"
		diffDays == 1 -> "yesterday"
		diffDays < 30 -> "$diffDays days ago"
		diffDays < 365 -> "${diffDays / 30} months ago"
		else -> "${diffDays / 365} years ago"
	}
}

/** GitHub's linguist colors for the languages found in the snapshot. */
private val languageColors = mapOf(
	"astro" to "#e142c9",
	"c#" to "#178600",
	"c++" to "#f34b7d",
	"css" to "#563d7c",
	"dart" to "#00B4AB",
	"gdscript" to "#518bc1",
	"go" to "#00ADD8",
	"golang" to "#00ADD8",
	"html" to "#e34c26",
	"java" to "#b07219",
	"javascript" to "#f1e05a",
	"kotlin" to "#A97BFF",
	"php" to "#4F5D95",
	"python" to "#3572A5",
	"ruby" to "#701516",
	"rust" to "#dea584",
	"shell" to "#89e051",
	"svelte" to "#f44108",
	"swift" to "#ffac45",
	"typescript" to "#3178c6",
	"vue" to "#54ba82",
)

private fun languageColor(language: String) = Color(languageColors[language.lowercase()] ?: AppStyle.TEXT_MUTED_COLOR)

object DataStyle : StyleSheet() {
	val projectCard by style {
		color(Color(AppStyle.TEXT_COLOR))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(.8.cssRem)
		padding(1.4.cssRem)
		renderWhenVisible(13.cssRem)

		transitions {
			properties("border-color") { duration(.2.s) }
		}

		hover(self) style {
			borderColor(Color(AppStyle.BORDER_STRONG_COLOR))
		}

		className("card-header") style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			gap(.9.cssRem)

			"img" {
				borderRadius(50.percent)
				objectFit(ObjectFit.Cover)
				size(2.4.cssRem)
			}
		}

		className("repo-info") style {
			display(DisplayStyle.Flex)
			flex(1)
			flexDirection(FlexDirection.Column)
			gap(.2.cssRem)
			minWidth(0.px)

			"h3" {
				color(Color.white)
				fontSize(1.1.cssRem)
				margin(0.px)
				overflow(Overflow.Hidden)
				textOverflow(TextOverflow.Ellipsis)
				whiteSpace(WhiteSpace.NoWrap)
			}
		}

		group(className("language"), className("stats"), className("updated")) style {
			alignItems(AlignItems.Center)
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			display(DisplayStyle.Flex)
			fontSize(.85.cssRem)
			gap(.45.cssRem)
		}

		className("stats") style {
			gap(.4.cssRem, .9.cssRem)
		}

		className("language-dot") style {
			borderRadius(50.percent)
			size(.7.cssRem)
		}

		className("description") style {
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			fontSize(.95.cssRem)
			margin(0.px)
			overflow(Overflow.Hidden)
			property("display", "-webkit-box")
			property("-webkit-line-clamp", "2")
			property("-webkit-box-orient", "vertical")
		}

		className("topics") style {
			display(DisplayStyle.Flex)
			flexWrap(FlexWrap.Wrap)
			gap(.4.cssRem)
		}

		className("updated") style {
			marginTop(autoLength)
		}
	}
}
