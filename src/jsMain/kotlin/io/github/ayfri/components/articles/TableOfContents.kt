package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
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
	const val CONTAINER_BACKGROUND_COLOR = "#FFFFFF08"
	const val CONTAINER_BORDER_COLOR = "#FFFFFF20"
	const val TITLE_TEXT_COLOR = "#FFFFFFEE"
	const val ITEM_TEXT_COLOR = "#FFFFFFCC"
	const val ITEM_HOVER_BACKGROUND_COLOR = "#FFFFFF15"
	const val ITEM_HOVER_TEXT_COLOR = "#6EBAE7"

	val container by style {
		backgroundColor(Color(CONTAINER_BACKGROUND_COLOR))
		borderRadius(0.8.cssRem)
		border(1.px, LineStyle.Companion.Solid, Color(CONTAINER_BORDER_COLOR))
		padding(1.2.cssRem)
		marginBottom(2.cssRem)
	}

	val title by style {
		fontSize(1.3.cssRem)
		margin(0.px, 0.px, 1.cssRem)
		color(Color(TITLE_TEXT_COLOR))

		"i" {
			marginRight(0.5.cssRem)
		}
	}

	val list by style {
		listStyleType("none")
		margin(0.px)
		padding(0.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val item by style {
		margin(0.5.cssRem, 0.px)

		"a" {
			color(Color(ITEM_TEXT_COLOR))
			textDecoration("none")
			fontSize(0.95.cssRem)
			display(DisplayStyle.Companion.Block)
			padding(0.3.cssRem, 0.5.cssRem)
			borderRadius(0.3.cssRem)

			transitions {
				"background-color" {
					duration(0.2.s)
				}
				"color" {
					duration(0.2.s)
				}
			}

			self + hover style {
				backgroundColor(Color(ITEM_HOVER_BACKGROUND_COLOR))
				color(Color(ITEM_HOVER_TEXT_COLOR))
			}
		}
	}
}
