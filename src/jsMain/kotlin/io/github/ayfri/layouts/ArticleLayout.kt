package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.*
import io.github.ayfri.components.*
import io.github.ayfri.components.articles.*
import io.github.ayfri.externals.Prism
import io.github.ayfri.utils.margin
import io.github.ayfri.utils.webkitScrollbar
import io.github.ayfri.utils.webkitScrollbarThumb
import io.github.ayfri.utils.webkitScrollbarTrack
import js.date.Date
import js.intl.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*


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
				jsDate.toLocaleDateString("en-US", DateTimeFormatOptions(
					year = YearFormat.numeric,
					month = MonthFormat.long,
					day = DayFormat.numeric
				))
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
		backgroundColor(Color("#1A1225"))
		borderRadius(1.cssRem)
		padding(2.cssRem)
		marginBottom(3.cssRem)
		border {
			width(2.px)
			style(LineStyle.Solid)
			color(Color.transparent)
		}
		property("background", """
			linear-gradient(#1A1225, #1A1225) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		property("box-shadow", "0 0 30px rgba(0, 212, 255, 0.15)")
	}

	val title by style {
		fontSize(2.8.cssRem)
		lineHeight(1.3.em)
		margin(top = 0.cssRem, bottom = 1.cssRem)
		backgroundClip(BackgroundClip.Text)
		backgroundImage(linearGradient(45.deg) {
			add(Color("#00D4FF"))
			add(Color("#FF0080"))
		})
		property("-webkit-text-fill-color", "transparent")
		property("text-shadow", "0 0 20px rgba(0, 212, 255, 0.5)")
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
		backgroundImage(linearGradient(90.deg) {
			add(Color("#FFFFFF00"))
			add(Color("#FFFFFF40"))
			add(Color("#FFFFFF00"))
		})
		margin(1.5.cssRem, 0.px, 1.cssRem)
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
	init {
		id("main") style {
			// Add consistent background styling
			backgroundImage(linearGradient(180.deg) {
				add(Color("#0A0A0F"), (-3).percent)
				add(Color("#1A1225"), 14.percent)
				add(Color("#2A1B3D"), 65.percent)
				add(Color("#1E1535"), 90.percent)
			})
			minHeight(100.vh)
		}
	}

	val article by style {
		margin(0.px, auto)
		maxWidth(1000.px)
		padding(3.cssRem, 1.cssRem)
		fontSize(1.05.cssRem)
		lineHeight(1.5.em)
		property("tab-size", 4)

		"h1" {
			backgroundClip(BackgroundClip.Text)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			fontSize(2.5.cssRem)
			lineHeight(1.3.em)
			margin(top = 0.cssRem, bottom = 1.cssRem)
			property("-webkit-text-fill-color", "transparent")
		}

		"h2" {
			backgroundClip(BackgroundClip.Text)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			fontSize(1.75.cssRem)
			marginTop(2.cssRem)
			marginBottom(1.cssRem)
			property("-webkit-text-fill-color", "transparent")
		}

		"h3" {
			backgroundClip(BackgroundClip.Text)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			fontSize(1.35.cssRem)
			property("-webkit-text-fill-color", "transparent")
		}

		"h4" {
			fontSize(1.25.cssRem)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			property("-webkit-background-clip", "text")
			property("background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
		}

		// Create content sections for better visual separation
		"h2, h3, h4, h5, h6" {
			color(Color.white)
		}


		linkStyle(desc(self, "li"))

		"p" {
			self + lastOfType style {
				marginBottom(0.px)
			}

			self + child(not(type("pre")), type("code")) style {
				backgroundColor(Color("#1d1d20"))
				borderRadius(.5.cssRem)
				padding(.3.cssRem, .5.cssRem)
			}
		}

		"img" {
			maxWidth(100.percent)
			borderRadius(.5.cssRem)
		}

		"pre" {
			backgroundColor(Color("#1A1225"))
			borderRadius(.8.cssRem)
			overflowX(Overflow.Auto)
			padding(1.5.cssRem)

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

		// Enhanced blockquote styling
		"blockquote" {
			borderLeft(4.px, LineStyle.Solid, Color("#00D4FF"))
			backgroundColor(Color("#1A1225"))
			borderRadius(0.5.cssRem)
			padding(1.cssRem)
			margin(1.cssRem, 0.px)
			fontStyle(FontStyle.Italic)
		}
	}
}
