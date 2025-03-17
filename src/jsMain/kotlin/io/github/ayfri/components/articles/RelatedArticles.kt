package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.borderTop
import com.varabyte.kobweb.compose.css.boxShadow
import io.github.ayfri.*
import io.github.ayfri.data.ArticleEntry
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
	const val BORDER_COLOR = "#FFFFFF20"
	const val TITLE_COLOR = "#FFFFFFEE"
	const val ACCENT_COLOR = "#6EBAE7"
	const val CARD_BG_COLOR = "#FFFFFF09"
	const val CARD_HOVER_BG_COLOR = "#FFFFFF15"
	const val CARD_SHADOW_COLOR = "#00000040"
	const val CARD_DESC_COLOR = "#FFFFFFCC"
	const val CARD_META_COLOR = "#FFFFFF99"

	val container by style {
		marginTop(4.cssRem)
		paddingTop(2.cssRem)
		borderTop(1.px, LineStyle.Solid, Color(BORDER_COLOR))
	}

	val title by style {
		fontSize(1.5.cssRem)
		margin(0.px, 0.px, 1.5.cssRem)
		color(Color(TITLE_COLOR))

		"i" {
			marginRight(0.5.cssRem)
		}
	}

	val grid by style {
		display(DisplayStyle.Companion.Grid)
		gridTemplateColumns("repeat(auto-fit, minmax(250px, 1fr))")
		gap(1.5.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val card by style {
		backgroundColor(Color(CARD_BG_COLOR))
		borderRadius(0.8.cssRem)
		border(1.px, LineStyle.Companion.Solid, Color(BORDER_COLOR))
		padding(1.5.cssRem)
		display(DisplayStyle.Companion.Flex)
		flexDirection(FlexDirection.Companion.Column)
		height(100.percent)
		textDecoration("none")
		color(Color.white)

		transitions {
			"background-color" {
				duration(0.3.s)
			}
			"transform" {
				duration(0.3.s)
			}
			"box-shadow" {
				duration(0.3.s)
			}
		}

		self + hover style {
			backgroundColor(Color(CARD_HOVER_BG_COLOR))
			transform {
				translateY((-5).px)
			}
			boxShadow(0.px, 4.px, 15.px, 0.px, Color(CARD_SHADOW_COLOR))
		}
	}

	val cardTitle by style {
		fontSize(1.2.cssRem)
		margin(0.px, 0.px, 0.8.cssRem)
		color(Color.white)
	}

	val cardDescription by style {
		fontSize(0.9.cssRem)
		lineHeight(1.5.em)
		color(Color(CARD_DESC_COLOR))
		margin(0.px, 0.px, 1.cssRem)
		flexGrow(1)
	}

	val cardMeta by style {
		display(DisplayStyle.Companion.Flex)
		justifyContent(JustifyContent.Companion.SpaceBetween)
		fontSize(0.8.cssRem)
		color(Color(CARD_META_COLOR))
		marginBottom(1.cssRem)

		"i" {
			marginRight(0.3.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val readMore by style {
		display(DisplayStyle.Companion.Flex)
		alignItems(AlignItems.Companion.Center)
		justifyContent(JustifyContent.Companion.FlexEnd)
		gap(0.5.cssRem)
		fontSize(0.9.cssRem)
		fontWeight(600)
		color(Color(ACCENT_COLOR))

		transitions {
			"transform" {
				duration(0.2.s)
			}
		}
	}

	// Responsive styles
	init {
		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			grid style {
				gridTemplateColumns("1fr")
			}
		}
	}
}
