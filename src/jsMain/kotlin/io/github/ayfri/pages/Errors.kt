package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AnimationsStyle
import io.github.ayfri.AppStyle
import io.github.ayfri.components.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

// Statically exported so Cloudflare Pages can serve it as `404.html` with a real 404 status
// (see .github/workflows/CD.yml, which copies this page's export output into place).
@Page("/404")
@Composable
fun NotFound404Page() = NotFoundPage()

@Composable
fun NotFoundPage() {
	Style(AnimationsStyle)
	Style(AppStyle)
	setTitle("404 - Page Not Found")

	// Tell crawlers to drop this URL instead of endlessly re-crawling it.
	MetaName("robots", "noindex, nofollow")

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
			H1 {
				Text("404")
			}
			P {
				Text("This page doesn't exist.")
			}
			A("/", "Go back home")
		}
	}

	Footer()
}
