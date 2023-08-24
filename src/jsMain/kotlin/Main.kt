@file:Suppress("JS_NAME_CLASH", "JS_FAKE_NAME_CLASH")

import app.softwork.routingcompose.BrowserRouter
import externals.TextRenderer
import externals.use
import header.Header
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.renderComposableInBody
import pages.*

const val MAIL_TO = "pierre.ayfri@gmail.com"

fun main() {
	renderComposable(root = document.querySelector("head")!!) {
		Head()
	}

	val renderer = object : TextRenderer() {
		override fun link(href: String?, title: String?, text: String) = """
			<a href="$href" ${title?.let { "title=$it" } ?: ""} class="link">$text</a>
		""".trimIndent()
	}

	use(jso { this.renderer = renderer })

	renderComposableInBody {
		BrowserRouter("/") {
			val pathname = document.location?.pathname ?: "404"
			val currentPageName = pathname.removePrefix("/").replace("/", " ").titlecase()
			setTitle("${currentPageName.ifEmpty { "Home" }} - Pierre Roy")

			Header()

			Main({
				id("main")
			}) {
				route("/") {
					Home()
				}

				route("/about") {
					AboutMe()
				}

				route("/skills") {
					Skills()
				}

				route("/projects") {
					Projects()
				}

				route("/experiences") {
					Experiences()
				}

				route("/portfolio") {
					Portfolio()
				}
			}

			Footer()

			noMatch {
				redirect("/", true)
			}

			window.scroll(0.0, 0.0)
		}
	}
}