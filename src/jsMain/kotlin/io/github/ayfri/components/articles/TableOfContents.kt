package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.ListStyle
import com.varabyte.kobweb.compose.css.listStyle
import com.varabyte.kobweb.silk.components.icons.lucide.LucideList
import io.github.ayfri.AppStyle
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

private val nonSlugRegex = Regex("[^a-z0-9\\s-]")
private val whitespaceRegex = Regex("\\s+")

fun headingToId(text: String) = text.lowercase()
	.replace(nonSlugRegex, "")
	.replace(whitespaceRegex, "-")

@Composable
fun TableOfContents(headings: List<Pair<Int, String>>) {
	Style(TableOfContentsStyle)

	Nav({
		classes(AppStyle.card, TableOfContentsStyle.container)
		attr("aria-label", "Table of contents")
	}) {
		P({
			classes(TableOfContentsStyle.title)
		}) {
			LucideList()
			Text("Contents")
		}

		Ul({
			classes(TableOfContentsStyle.list)
		}) {
			headings.forEach { (level, text) ->
				Li({
					style { paddingLeft((level - 1).cssRem) }
				}) {
					A("#${headingToId(text)}") { Text(text) }
				}
			}
		}
	}
}

object TableOfContentsStyle : StyleSheet() {
	val container by style {
		marginBottom(2.5.cssRem)
		padding(1.4.cssRem, 1.6.cssRem)
	}

	val title by style {
		alignItems(AlignItems.Center)
		color(Color.white)
		display(DisplayStyle.Flex)
		fontFamily(AppStyle.MONO_FONT_FAMILY, "monospace")
		fontSize(.9.cssRem)
		fontWeight(700)
		gap(.5.cssRem)
		margin(0.px, 0.px, .6.cssRem)

		"svg" {
			color(Color(AppStyle.VIOLET_COLOR))
		}
	}

	val list by style {
		listStyle(ListStyle.None)
		margin(0.px)
		padding(0.px)

		"a" {
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			display(DisplayStyle.Block)
			fontSize(.95.cssRem)
			padding(.2.cssRem, 0.px)
		}

		"a:hover" {
			color(Color(AppStyle.LINK_COLOR))
		}
	}
}
