package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AppStyle
import io.github.ayfri.components.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

/** The export writes this page as `404.html`, which Cloudflare Pages serves with a real 404 status. */
@Page("/404")
@Composable
fun NotFound404Page() = NotFoundPage()

@Composable
fun NotFoundPage() {
	Style(AppStyle)

	setPageHead("404 - Page Not Found", "This page doesn't exist, or it moved.", canonical = null, robots = "noindex, nofollow")

	Header()

	Main({
		id("main")
	}) {
		Div({
			style {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
				alignItems(AlignItems.Center)
				justifyContent(JustifyContent.Center)
				gap(1.cssRem)
				minHeight(60.vh)
				padding(2.cssRem)
			}
		}) {
			H1({
				classes(AppStyle.title)
				style { margin(0.px) }
			}) {
				Text("404")
			}
			P("This page doesn't exist, or it moved.")
			A("/", "Back to the home page", AppStyle.button, AppStyle.primaryButton)
		}
	}

	Footer()
}
