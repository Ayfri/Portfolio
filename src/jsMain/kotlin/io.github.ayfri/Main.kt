@file:Suppress("JS_NAME_CLASH", "JS_FAKE_NAME_CLASH")

package io.github.ayfri

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import io.github.ayfri.externals.TextRenderer
import io.github.ayfri.externals.use
import io.github.ayfri.header.Header
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.renderComposable

const val MAIL_TO = "pierre.ayfri@gmail.com"

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
	renderComposable(root = document.querySelector("head")!!) {
		Head()
	}

	val renderer = object : TextRenderer() {
		override fun link(href: String?, title: String?, text: String) = """
			<a href="$href" ${title?.let { "title=$it" } ?: ""} class="link">$text</a>
		""".trimIndent()
	}

	use(jso { this.renderer = renderer })

	KobwebApp {
		val pathname = document.location?.pathname ?: "404"
		val currentPageName = pathname.removePrefix("/").replace("/", " ").titlecase()
		setTitle("${currentPageName.ifEmpty { "Home" }} - Pierre Roy")

		Header()

		Main({
			id("main")
		}) {
			content()
		}

		Footer()

		window.scroll(0.0, 0.0)
	}
}
