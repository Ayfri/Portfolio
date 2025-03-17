package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.margin
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.*
import io.github.ayfri.components.*
import io.github.ayfri.components.articles.*
import io.github.ayfri.externals.Prism
import io.github.ayfri.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import kotlin.js.Date


@Composable
fun ArticleLayout(content: @Composable () -> Unit) {
	Style(AnimationsStyle)
	Style(AppStyle)
	Style(MarkdownStyle)
	Style(CodeTheme)
	Style(ArticleHeaderStyle)

	val context = rememberPageContext()
	val markdownData = context.markdown!!.frontMatter
	val title = markdownData["title"]?.get(0) ?: "Title not found"
	val description = markdownData["description"]?.get(0) ?: "Description not found"
	val keywords = markdownData["keywords"]?.get(0) ?: ""
	val date = markdownData["date-created"]?.get(0) ?: ""
	val dateModified = markdownData["date-modified"]?.get(0) ?: date
	val articleContent = articlesEntries.find { it.title == title }?.content ?: ""

	// Extract headings from content for table of contents
	val headings = extractHeadings(title, articleContent)

	// Find related articles based on keywords
	val currentPath = context.route.toString()
	val keywordsList = keywords.split(",").map { it.trim() }
	val relatedArticles = findRelatedArticles(currentPath, keywordsList)

	setTitle("$title - ${AppGlobals["author"]}'s Blog")
	setDescription(description)

	val currentStub = context.route
	val canonicalUrl = AppGlobals["url"] + currentStub.toString().ensureSuffix("/")
	setCanonical(canonicalUrl)

	setJsonLD()

	LaunchedEffect(currentStub) {
		Prism.highlightAll()
	}

	// Reading progress bar
	ReadingProgressBar()

	// Back to top button
	BackToTopButton()

	Header()

	Main({
		id("main")
	}) {
		Article({
			classes(MarkdownStyle.article)
		}) {
			ArticleHeader(
				title = markdownData["title"]?.get(0) ?: title,
				date = date,
				dateModified = dateModified,
				keywords = keywordsList,
				content = articleContent
			)

			if (headings.isNotEmpty()) {
				TableOfContents(headings)
			}

			content()

			if (relatedArticles.isNotEmpty()) {
				RelatedArticles(relatedArticles)
			}

			ShareSection(title, canonicalUrl)
		}
	}

	Footer()
}

@Composable
fun ArticleHeader(
	title: String,
	date: String,
	dateModified: String,
	keywords: List<String>,
	content: String
) {
	Div({
		classes(ArticleHeaderStyle.container)
	}) {
		H1({
			classes(ArticleHeaderStyle.title)
		}) {
			Text(title)
		}

		Div({
			classes(ArticleHeaderStyle.meta)
		}) {
			// Format and display date
			val formattedDate = try {
				val jsDate = Date(date)
				jsDate.toLocaleDateString("en-US", jso {
					year = "numeric"
					month = "long"
					day = "numeric"
				})
			} catch (e: Exception) {
				date.split("T")[0]
			}

			Span({
				classes(ArticleHeaderStyle.metaItem)
			}) {
				I({
					classes("fas", "fa-calendar-alt")
				})
				Text(" $formattedDate")
			}

			// Reading time estimate
			val readingTime = calculateReadingTime(content)
			Span({
				classes(ArticleHeaderStyle.metaItem)
			}) {
				I({
					classes("fas", "fa-clock")
				})
				Text(" $readingTime min read")
			}

			// Check if article was updated
			if (date != dateModified) {
				Span({
					classes(ArticleHeaderStyle.metaItem, ArticleHeaderStyle.updated)
				}) {
					I({
						classes("fas", "fa-sync-alt")
					})
					Text(" Updated")
				}
			}
		}

		if (keywords.isNotEmpty()) {
			Div({
				classes(ArticleHeaderStyle.keywordsContainer)
			}) {
				keywords.forEach { keyword ->
					Span({
						classes(ArticleHeaderStyle.keyword)
					}) {
						Text(keyword)
					}
				}
			}
		}

		Hr {
			classes(ArticleHeaderStyle.divider)
		}
	}
}

object ArticleHeaderStyle : StyleSheet() {
	val container by style {
		marginBottom(2.cssRem)
	}

	val title by style {
		fontSize(2.8.cssRem)
		lineHeight(1.3.em)
		margin(top = 0.cssRem, bottom = 1.cssRem)
		background(linearGradient(45.deg) {
			stop(Color("#9C7CF4"))
			stop(Color("#6EBAE7"))
		})
		property("-webkit-background-clip", "text")
		property("background-clip", "text")
		property("-webkit-text-fill-color", "transparent")
	}

	val meta by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
		color(Color("#FFFFFF99"))
		fontSize(0.9.cssRem)
		marginBottom(1.cssRem)
	}

	val metaItem by style {
		display(DisplayStyle.LegacyInlineFlex)
		alignItems(AlignItems.Center)

		"i" {
			marginRight(0.3.cssRem)
			fontSize(0.9.em)
		}
	}

	val updated by style {
		color(Color("#6EBAE7"))
	}

	val keywordsContainer by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
		marginBottom(1.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val keyword by style {
		backgroundColor(Color("#FFFFFF15"))
		borderRadius(1.cssRem)
		padding(0.3.cssRem, 0.8.cssRem)
		fontSize(0.8.cssRem)
		color(Color("#FFFFFFDD"))

		transitions {
			"background-color" {
				duration(0.2.s)
			}
		}

		self + hover style {
			backgroundColor(Color("#FFFFFF25"))
		}
	}

	val divider by style {
		border(0.px)
		height(1.px)
		background(linearGradient(90.deg) {
			stop(Color("#FFFFFF00"))
			stop(Color("#FFFFFF40"))
			stop(Color("#FFFFFF00"))
		})
		margin(0.5.cssRem, 0.px, 1.cssRem)
	}

	// Responsive styles
	init {
		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			title style {
				fontSize(2.4.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			title style {
				fontSize(2.cssRem)
			}
		}
	}
}

object MarkdownStyle : StyleSheet() {
	val article by style {
		margin(0.px, auto)
		maxWidth(1000.px)
		padding(3.cssRem, 1.cssRem)
		fontSize(1.05.cssRem)
		lineHeight(1.5.em)
		property("tab-size", 4)

		"h1" {
			fontSize(2.5.cssRem)
			lineHeight(1.3.em)
			margin(top = 0.cssRem, bottom = 1.cssRem)
		}

		"h2" {
			fontSize(1.75.cssRem)
		}

		"h3" {
			fontSize(1.35.cssRem)
		}

		"h4" {
			fontSize(1.25.cssRem)
		}

		child(not(type("pre")), type("code")) style {
			backgroundColor(Color("#1d1d20"))
			borderRadius(.5.cssRem)
			padding(.3.cssRem, .5.cssRem)
		}

		linkStyle(desc(self, "li"))

		"p" {
			self + lastOfType style {
				marginBottom(0.px)
			}
		}

		"img" {
			maxWidth(100.percent)
			borderRadius(.5.cssRem)
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
	}
}
