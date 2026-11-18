package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCalendar
import com.varabyte.kobweb.silk.components.icons.lucide.LucideClock
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRefreshCw
import io.github.ayfri.*
import io.github.ayfri.components.IconLabel
import io.github.ayfri.components.TagList
import io.github.ayfri.data.ArticleEntry
import io.github.ayfri.utils.renderWhenVisible
import io.github.ayfri.utils.revealOnScroll
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

/** Date, reading time and last update line, shared by the article list and the article page header. */
@Composable
fun ArticleMeta(date: String, dateModified: String, readingTime: Int) {
	P({
		classes(AppStyle.monoFont, ArticleListStyle.meta)
	}) {
		IconLabel(formatLongDate(date)) { LucideCalendar() }
		IconLabel("$readingTime min read") { LucideClock() }
		if (date != dateModified) IconLabel("updated ${formatLongDate(dateModified)}", ArticleListStyle.updated) { LucideRefreshCw() }
	}
}

@Composable
fun ArticleList(entries: List<ArticleEntry>) {
	Ul({
		classes(ArticleListStyle.articleList)
	}) {
		entries.forEach { entry ->
			Li {
				Anchor(entry.path, {
					classes(ArticleListStyle.article)
				}) {
					ArticleMeta(entry.date, entry.dateModified, entry.readingTime)

					H2 { Text(entry.title) }

					P({ classes(ArticleListStyle.description) }) { Text(entry.desc.withoutMarkdownLinks()) }

					TagList(entry.keywords, ArticleListStyle.keywords, max = 5)
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
		marginTop(.3.cssRem)
	}
}
