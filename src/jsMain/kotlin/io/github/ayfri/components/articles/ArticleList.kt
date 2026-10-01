package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCalendar
import com.varabyte.kobweb.silk.components.icons.lucide.LucideClock
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRefreshCw
import io.github.ayfri.*
import io.github.ayfri.components.Icon
import io.github.ayfri.data.ArticleEntry
import io.github.ayfri.pages.IconStyle
import io.github.ayfri.utils.renderWhenVisible
import io.github.ayfri.utils.revealOnScroll
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

private const val VISIBLE_KEYWORDS = 5

@Composable
private fun MetaItem(text: String, vararg classes: String, icon: Icon) {
	Span({ classes(IconStyle.label, *classes) }) {
		icon()
		Text(text)
	}
}

/** Date, reading time and last update line, shared by the article list and the article page header. */
@Composable
fun ArticleMeta(date: String, dateModified: String, content: String) {
	Style(ArticleListStyle)
	Style(IconStyle)

	P({
		classes(AppStyle.monoFont, ArticleListStyle.meta)
	}) {
		MetaItem(formatLongDate(date)) { LucideCalendar() }
		MetaItem("${calculateReadingTime(content)} min read") { LucideClock() }
		if (date != dateModified) MetaItem("updated ${formatLongDate(dateModified)}", ArticleListStyle.updated) { LucideRefreshCw() }
	}
}

@Composable
fun ArticleList(entries: List<ArticleEntry>) {
	Ul({
		classes(ArticleListStyle.articleList)
	}) {
		entries.forEach { entry ->
			Li {
				Anchor(entry.path.ensureSuffix("/"), {
					classes(ArticleListStyle.article)
				}) {
					ArticleMeta(entry.date, entry.dateModified, entry.content)

					H2 { Text(entry.title) }

					P({ classes(ArticleListStyle.description) }) { Text(entry.desc.withoutMarkdownLinks()) }

					if (entry.keywords.isNotEmpty()) Div({
						classes(ArticleListStyle.keywords)
					}) {
						entry.keywords.take(VISIBLE_KEYWORDS).forEach { Span({ classes(AppStyle.tag) }) { Text(it) } }
						if (entry.keywords.size > VISIBLE_KEYWORDS) {
							Span({ classes(AppStyle.tag) }) { Text("+${entry.keywords.size - VISIBLE_KEYWORDS}") }
						}
					}
				}
			}
		}
	}
}

object ArticleListStyle : StyleSheet() {
	val articleList by style {
		borderTop(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		listStyle(ListStyle.None)
		margin(0.px)
		padding(0.px)
	}

	val article by style {
		borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(.6.cssRem)
		padding(1.6.cssRem, 0.px)
		renderWhenVisible(11.cssRem)
		revealOnScroll()

		"h2" {
			color(Color.white)
			fontSize(1.45.cssRem)
			margin(0.px)
			transitions {
				properties("color") { duration(.2.s) }
			}
		}

		desc(hover(self), "h2") style {
			color(Color(AppStyle.ACCENT_COLOR))
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			"h2" {
				fontSize(1.25.cssRem)
			}
		}
	}

	val meta by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(.8.cssRem)
		gap(.3.cssRem, 1.2.cssRem)
		margin(0.px)
	}

	val updated by style {
		color(Color(AppStyle.LINK_COLOR))
	}

	val description by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		margin(0.px)
		maxWidth(48.cssRem)
	}

	val keywords by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.4.cssRem)
		marginTop(.3.cssRem)
	}
}
