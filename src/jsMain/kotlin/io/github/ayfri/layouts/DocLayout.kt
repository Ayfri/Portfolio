package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.Footer
import io.github.ayfri.Head
import io.github.ayfri.header.Header
import io.github.ayfri.setTitle
import io.github.ayfri.utils.margin
import io.github.ayfri.utils.webkitScrollbar
import io.github.ayfri.utils.webkitScrollbarThumb
import io.github.ayfri.utils.webkitScrollbarTrack
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.renderComposable


@Composable
fun DocLayout(content: @Composable () -> Unit) {
	Style(MarkdownStyle)

	val context = rememberPageContext()
	val markdownData = context.markdown!!.frontMatter
	val title = markdownData["nav-title"]?.get(0) ?: "Title not found"

	LaunchedEffect(title) {
		setTitle("$title - Pierre Roy")
	}

	renderComposable(root = document.querySelector("head")!!) {
		Head()
	}

	Header()

	Main({
		id("main")
	}) {
		Article({
			classes(MarkdownStyle.article)
		}) {
			content()
		}
	}

	Footer()

	window.scroll(0.0, 0.0)
}

object MarkdownStyle : StyleSheet() {
	val article by style {
		margin(0.px, auto)
		maxWidth(800.px)
		padding(3.cssRem, 1.cssRem)

		"p" + lastOfType {
			marginBottom(0.px)
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
