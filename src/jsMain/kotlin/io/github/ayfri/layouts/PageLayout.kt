package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ayfri.AppStyle
import io.github.ayfri.Footer
import io.github.ayfri.header.Header
import io.github.ayfri.setTitle
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Main

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
	Style(AppStyle)
	LaunchedEffect(title) {
		setTitle("$title - Pierre Roy")
	}

	Header()

	Main({
		id("main")
	}) {
		content()
	}

	Footer()

	window.scroll(0.0, 0.0)
}
