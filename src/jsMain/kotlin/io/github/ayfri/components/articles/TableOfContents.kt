package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

// Extract headings from markdown content
fun extractHeadings(title: String, content: String): List<Pair<Int, String>> {
	val headingRegex = Regex("^(#{1,6})\\s+(.+)$", RegexOption.MULTILINE)
	// Ignore code blocks because lines with comments could be included
	val contentWithoutCodeBlocks = content.replace(Regex("```[\\s\\S]*?```"), "")

	val list = mutableListOf((1 to title))
	return list + headingRegex.findAll(contentWithoutCodeBlocks).map { matchResult ->
		val level = matchResult.groupValues[1].length
		val text = matchResult.groupValues[2].trim()
		level to text
	}
}

// Generate an ID from heading text for anchor links
fun headingToId(text: String): String {
	return text.lowercase()
		.replace(Regex("[^a-z0-9\\s-]"), "")
		.replace(Regex("\\s+"), "-")
}

@Composable
fun TableOfContents(headings: List<Pair<Int, String>>) {
	Style(TableOfContentsStyle)

	Div({
		classes(TableOfContentsStyle.container)
	}) {
		H3({
			classes(TableOfContentsStyle.title)
		}) {
			I({
				classes("fas", "fa-list")
			})
			Text(" Table of Contents")
		}

		Ul({
			classes(TableOfContentsStyle.list)
		}) {
			headings.forEach { (level, text) ->
				Li({
					classes(TableOfContentsStyle.item)
					style {
						paddingLeft((level - 1).cssRem)
					}
				}) {
					A(href = "#${headingToId(text)}") {
						Text(text)
					}
				}
			}
		}
	}
}

object TableOfContentsStyle : StyleSheet() {
	const val CONTAINER_BACKGROUND_COLOR = "#1A1225"
	const val ITEM_TEXT_COLOR = "#FFFFFFCC"
	const val ITEM_HOVER_BACKGROUND_COLOR = "#FFFFFF15"
	const val ITEM_HOVER_TEXT_COLOR = "#6EBAE7"

	val container by style {
		backgroundImage("""
			linear-gradient(${CONTAINER_BACKGROUND_COLOR}, ${CONTAINER_BACKGROUND_COLOR}) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		backgroundColor(Color(CONTAINER_BACKGROUND_COLOR))
		border(2.px, LineStyle.Solid, Color.transparent)
		borderRadius(1.cssRem)
		boxShadow("0 0 30px rgba(0, 212, 255, 0.15)")
		padding(2.cssRem)
		marginBottom(3.cssRem)
	}

	val title by style {
		backgroundClip(BackgroundClip.Text)
		backgroundImage(linearGradient(45.deg) {
			add(Color("#00D4FF"))
			add(Color("#FF0080"))
		})
		fontSize(1.5.cssRem)
		margin(0.px, 0.px, 1.cssRem)
		property("-webkit-background-clip", "text")
		property("-webkit-text-fill-color", "transparent")

		"i" {
			marginRight(0.5.cssRem)
		}
	}

	val list by style {
		listStyle(ListStyle.None)
		margin(0.px)
		padding(0.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val item by style {
		margin(0.2.cssRem, 0.px)

		"a" {
			color(Color(ITEM_TEXT_COLOR))
			textDecorationLine(TextDecorationLine.None)
			fontSize(0.95.cssRem)
			display(DisplayStyle.Block)
			padding(0.35.cssRem, 0.8.cssRem)
			borderRadius(0.5.cssRem)

			transitions {
				defaultDuration(0.2.s)
				properties("background-color", "color", "transform")
			}

			self + hover style {
				backgroundColor(Color(ITEM_HOVER_BACKGROUND_COLOR))
				color(Color(ITEM_HOVER_TEXT_COLOR))
				transform {
					translateX(5.px)
				}
			}
		}
	}
}
