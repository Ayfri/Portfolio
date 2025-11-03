package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.GridEntry
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.gridTemplateColumns
import com.varabyte.kobweb.compose.css.textDecorationLine
import io.github.ayfri.*
import io.github.ayfri.data.ArticleEntry
import io.github.ayfri.utils.linearGradient
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import kotlin.js.Date

// Find related articles based on keywords
fun findRelatedArticles(currentPath: String, keywords: List<String>): List<ArticleEntry> {
	if (keywords.isEmpty()) return emptyList()

	// Get all articles except the current one
	val otherArticles = articlesEntries.filter { it.path != currentPath }

	// Calculate relevance score for each article based on keyword matches
	val scoredArticles = otherArticles.map { article ->
		val matchingKeywords = article.keywords.count { it in keywords }
		article to matchingKeywords
	}

	// Filter articles with at least one matching keyword and sort by relevance
	return scoredArticles
		.filter { (_, score) -> score > 0 }
		.sortedByDescending { (_, score) -> score }
		.map { (article, _) -> article }
		.take(3) // Limit to 3 related articles
}

@Composable
fun RelatedArticles(articles: List<ArticleEntry>) {
	Style(RelatedArticlesStyle)

	Div({
		classes(RelatedArticlesStyle.container)
	}) {
		H3({
			classes(RelatedArticlesStyle.title)
		}) {
			I({
				classes("fas", "fa-bookmark")
			})
			Text(" You might also like")
		}

		Div({
			classes(RelatedArticlesStyle.grid)
		}) {
			articles.forEach { article ->
				RelatedArticleCard(article)
			}
		}
	}
}

@Composable
fun RelatedArticleCard(article: ArticleEntry) {
	A(
		href = article.path.ensureSuffix("/"),
		attrs = {
			classes(RelatedArticlesStyle.card)
		}
	) {
		H4({
			classes(RelatedArticlesStyle.cardTitle)
		}) {
			Text(article.title)
		}

		P({
			classes(RelatedArticlesStyle.cardDescription)
		}) {
			Text(article.desc)
		}

		Div({
			classes(RelatedArticlesStyle.cardMeta)
		}) {
			// Format date
			val date = try {
				val jsDate = Date(article.date)
				jsDate.toLocaleDateString("en-US", jso {
					year = "numeric"
					month = "short"
					day = "numeric"
				})
			} catch (e: Exception) {
				article.date.split("T")[0]
			}

			Span {
				I({
					classes("fas", "fa-calendar-alt")
				})
				Text(" $date")
			}

			// Reading time
			val readingTime = calculateReadingTime(article.content)
			Span {
				I({
					classes("fas", "fa-clock")
				})
				Text(" $readingTime min read")
			}
		}

		Div({
			classes(RelatedArticlesStyle.readMore)
		}) {
			Text("Read article")
			I({
				classes("fas", "fa-arrow-right")
			})
		}
	}
}

object RelatedArticlesStyle : StyleSheet() {
	const val CONTAINER_BG_COLOR = "#1A1225"
	const val ACCENT_COLOR = "#6EBAE7"
	const val CARD_BG_COLOR = "#1E1535"
	const val CARD_HOVER_BG_COLOR = "#2A1B3D"
	const val CARD_DESC_COLOR = "#FFFFFFCC"
	const val CARD_META_COLOR = "#FFFFFF99"

	val container by style {
		marginTop(4.cssRem)
		paddingTop(0.px)
		backgroundColor(Color(CONTAINER_BG_COLOR))
		borderRadius(1.cssRem)
		padding(2.cssRem)
		border {
			width(2.px)
			style(LineStyle.Solid)
			color(Color.transparent)
		}
		property("background", """
			linear-gradient(${CONTAINER_BG_COLOR}, ${CONTAINER_BG_COLOR}) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		property("box-shadow", "0 0 30px rgba(0, 212, 255, 0.15)")
	}

	val title by style {
		fontSize(1.8.cssRem)
		margin(0.px, 0.px, 2.cssRem)
		background(linearGradient(45.deg) {
			stop(Color("#00D4FF"))
			stop(Color("#FF0080"))
		})
		property("-webkit-background-clip", "text")
		property("background-clip", "text")
		property("-webkit-text-fill-color", "transparent")

		"i" {
			marginRight(0.5.cssRem)
		}
	}

	val grid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(GridEntry.Repeat.Auto.Type.AutoFit) {
				minmax(280.px, 1.fr)
			}
		}
		gap(1.5.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val card by style {
		backgroundColor(Color(CARD_BG_COLOR))
		borderRadius(1.cssRem)
		padding(1.5.cssRem)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		height(100.percent)
		textDecorationLine(TextDecorationLine.None)
		color(Color.white)

		transitions {
			defaultDuration(0.3.s)
			defaultTimingFunction(AnimationTimingFunction.EaseInOut)
			properties("background-color", "transform", "box-shadow")
		}

		self + hover style {
			backgroundColor(Color(CARD_HOVER_BG_COLOR))
			transform {
				translateY((-5).px)
			}
			property("box-shadow", "0 0 20px rgba(255, 0, 128, 0.2)")
		}
	}

	val cardTitle by style {
		fontSize(1.2.cssRem)
		margin(0.px, 0.px, 1.cssRem)
		background(linearGradient(45.deg) {
			stop(Color("#00D4FF"))
			stop(Color("#FF0080"))
		})
		property("-webkit-background-clip", "text")
		property("background-clip", "text")
		property("-webkit-text-fill-color", "transparent")
	}

	val cardDescription by style {
		fontSize(0.9.cssRem)
		lineHeight(1.5.em)
		color(Color(CARD_DESC_COLOR))
		margin(0.px, 0.px, 1.cssRem)
		flexGrow(1)
	}

	val cardMeta by style {
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.FlexEnd)
		fontSize(0.8.cssRem)
		color(Color(CARD_META_COLOR))
		marginBottom(1.cssRem)

		"i" {
			marginRight(0.3.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val readMore by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.FlexEnd)
		gap(0.5.cssRem)
		fontSize(0.9.cssRem)
		fontWeight(600)
		color(Color(ACCENT_COLOR))

		transitions {
			"transform" {
				duration(0.2.s)
			}
		}

		self + hover style {
			transform {
				translateX(5.px)
			}
		}
	}

	// Responsive styles
	init {
		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			grid style {
				gridTemplateColumns(GridEntry.TrackSize(1.fr))
			}
		}
	}
}
