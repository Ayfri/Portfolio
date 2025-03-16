package io.github.ayfri.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.navigation.Anchor
import io.github.ayfri.ensureSuffix
import io.github.ayfri.linkStyle
import io.github.ayfri.utils.margin
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*


data class ArticleEntry(
	val path: String,
	val date: String,
	val title: String,
	val desc: String,
	val navTitle: String,
	val keywords: List<String>,
	val dateModified: String,
	val content: String
)

@Composable
fun ArticleList(entries: List<ArticleEntry>) {
	H1 {
		Text("Blog Posts")
	}

	Ul({
		classes(ArticleListStyle.articleList)
	}) {
		entries.forEach { entry ->
			ArticleEntry(entry)
		}
	}
}

@Composable
fun ArticleEntry(entry: ArticleEntry) {
	Li({
		classes(ArticleListStyle.articleEntry)
	}) {
		Anchor(href = entry.path.ensureSuffix("/")) {
			Div {
				H2 {
					Text(entry.title)
				}

				P(entry.desc)
			}
		}
	}
}

object ArticleListStyle : StyleSheet() {
	init {
		"main" style {
			margin(0.px, auto)
			maxWidth(800.px)
			padding(1.cssRem, 1.cssRem, 3.cssRem)
		}
	}

	val title by style {
		fontSize(3.cssRem)
	}

	val articleList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		padding(0.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val articleEntry by style {
		backgroundColor(Color("#FFFFFF09"))
		borderRadius(0.5.cssRem)
		border(3.px, LineStyle.Solid, Color("#FFFFFF81"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		transitions {
			"background-color" {
				duration(0.2.s)
			}
		}

		self + hover style {
			backgroundColor(Color("#FFFFFF10"))
		}

		"p" {
			color(Color.white)
		}

		"a" {
			padding(1.cssRem)
		}

		linkStyle(self)
	}
}
