package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.*
import io.github.ayfri.components.*
import io.github.ayfri.components.articles.*
import io.github.ayfri.externals.HighlightCode
import io.github.ayfri.utils.list
import io.github.ayfri.utils.webkitScrollbar
import io.github.ayfri.utils.webkitScrollbarThumb
import io.github.ayfri.utils.webkitScrollbarTrack
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun ArticleLayout(content: @Composable () -> Unit) {
	Style(AppStyle)
	Style(MarkdownStyle)
	Style(CodeTheme)

	val context = rememberPageContext()
	val frontMatter = context.markdown!!.frontMatter
	fun field(name: String) = frontMatter[name]?.firstOrNull()

	val title = field("title") ?: "Title not found"
	val description = field("description") ?: "Description not found"
	val keywords = field("keywords") ?: ""
	val date = field("date-created") ?: ""
	val dateModified = field("date-modified") ?: date
	val articleContent = remember(title) { articlesEntries.find { it.title == title }?.content ?: "" }

	// Both walk the whole article with regexes, so they are keyed on the article rather than run per recomposition.
	val headings = remember(title, articleContent) { extractHeadings(title, articleContent) }

	// The path only: with the export query attached this never matched an entry, so each article listed itself.
	val currentPath = context.route.path
	val keywordsList = remember(keywords) { keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() } }
	val relatedArticles = remember(currentPath, keywordsList) { findRelatedArticles(currentPath, keywordsList) }

	setRobots(null)
	setTitle("$title - ${AppGlobals["author"]}'s Blog")
	setDescription(description)
	if (keywords.isNotEmpty()) setKeywords(keywords)

	// `context.route.toString()` still carries the `?_kobwebIsExporting=...` query during a static export, which
	// would ship a canonical pointing at a URL that does not exist. Only the path belongs in a canonical.
	val canonicalUrl = AppGlobals["url"] + context.route.path.ensureSuffix("/")
	setCanonical(canonicalUrl)
	setMarkdownAlternate(canonicalUrl + "index.md")
	setJsonLD()

	HighlightCode()
	ReadingProgressBar()
	BackToTopButton()
	Header()

	Main({
		id("main")
	}) {
		Article({
			classes(MarkdownStyle.article)
		}) {
			Header({
				classes(MarkdownStyle.header)
			}) {
				ArticleMeta(date, dateModified, articleContent)

				H1 { Text(title) }

				if (keywordsList.isNotEmpty()) Div({
					classes(MarkdownStyle.keywords)
				}) {
					keywordsList.forEach { Span({ classes(AppStyle.tag) }) { Text(it) } }
				}
			}

			if (headings.isNotEmpty()) TableOfContents(headings)

			content()

			if (relatedArticles.isNotEmpty()) RelatedArticles(relatedArticles)

			ShareSection(title, canonicalUrl)
		}
	}

	Footer()
}

object MarkdownStyle : StyleSheet() {
	val article by style {
		boxSizing(BoxSizing.BorderBox)
		fontSize(1.1.cssRem)
		lineHeight(1.75.number)
		margin(0.px, autoLength)
		maxWidth(50.cssRem)
		padding(3.cssRem, 1.25.cssRem)
		property("tab-size", 4)

		"h1" {
			color(Color.white)
			fontSize(2.6.cssRem)
			margin(.6.cssRem, 0.px, 1.cssRem)
		}

		"h2" {
			fontSize(1.7.cssRem)
			margin(2.8.cssRem, 0.px, 1.cssRem)
		}

		"h3" {
			fontSize(1.3.cssRem)
			margin(2.cssRem, 0.px, .8.cssRem)
		}

		"h4" {
			fontSize(1.1.cssRem)
		}

		list("h2", "h3", "h4", "h5", "h6") style {
			color(Color.white)
		}

		list("p", "li", "td") style {
			color(Color("#D8D3E6"))
		}

		linkStyle(desc(self, "li"))

		"p" {
			self + lastOfType style {
				marginBottom(0.px)
			}
		}

		":not(pre) > code" {
			backgroundColor(Color(AppStyle.SURFACE_RAISED))
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			borderRadius(.35.cssRem)
			fontSize(.9.em)
			padding(.1.cssRem, .35.cssRem)
		}

		"img" {
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			borderRadius(.6.cssRem)
			maxWidth(100.percent)
		}

		"pre" {
			backgroundColor(Color(AppStyle.CARD_BACKGROUND))
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			borderRadius(.7.cssRem)
			fontSize(.9.cssRem)
			lineHeight(1.6.number)
			overflowX(Overflow.Auto)
			padding(1.25.cssRem)

			self + webkitScrollbar style {
				height(8.px)
			}

			self + webkitScrollbarThumb style {
				backgroundColor(Color("#FFFFFF33"))
				borderRadius(.5.cssRem)
			}

			self + webkitScrollbarTrack style {
				backgroundColor(Color.transparent)
			}
		}

		"table" {
			borderCollapse(BorderCollapse.Collapse)
			display(DisplayStyle.Block)
			fontSize(.9.cssRem)
			margin(1.5.cssRem, 0.px)
			overflowX(Overflow.Auto)
		}

		list("td", "th") style {
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			padding(.5.cssRem, .75.cssRem)
			textAlign("left")
			property("vertical-align", "top")
		}

		"th" {
			backgroundColor(Color(AppStyle.SURFACE_RAISED))
			color(Color.white)
		}

		"blockquote" {
			borderLeft(2.px, LineStyle.Solid, Color(AppStyle.ACCENT_COLOR))
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			margin(1.5.cssRem, 0.px)
			padding(.2.cssRem, 0.px, .2.cssRem, 1.25.cssRem)
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			"h1" {
				fontSize(2.cssRem)
			}

			"h2" {
				fontSize(1.45.cssRem)
			}
		}
	}

	val header by style {
		borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		marginBottom(2.5.cssRem)
		paddingBottom(1.8.cssRem)
	}

	val keywords by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.4.cssRem)
	}
}
