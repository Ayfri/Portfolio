package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.margin
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.AppStyle
import io.github.ayfri.CodeTheme
import io.github.ayfri.Footer
import io.github.ayfri.header.Header
import io.github.ayfri.setTitle
import io.github.ayfri.utils.margin
import io.github.ayfri.utils.webkitScrollbar
import io.github.ayfri.utils.webkitScrollbarThumb
import io.github.ayfri.utils.webkitScrollbarTrack
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.Main


@Composable
fun DocLayout(content: @Composable () -> Unit) {
	Style(MarkdownStyle)
	Style(AppStyle)
	Style(CodeTheme)

	val context = rememberPageContext()
	val markdownData = context.markdown!!.frontMatter
	val title = markdownData["nav-title"]?.get(0) ?: "Title not found"

	LaunchedEffect(title) {
		setTitle("$title - Pierre Roy")
		js("window.Prism.highlightAll()").unsafeCast<Unit>()
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
