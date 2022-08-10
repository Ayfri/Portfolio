@file:Suppress("JS_NAME_CLASH", "JS_FAKE_NAME_CLASH")

import app.softwork.routingcompose.BrowserRouter
import header.Header
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.renderComposableInBody
import org.w3c.dom.HTMLBodyElement
import pages.Home

const val MAIL_TO = "pierre.ayfri@gmail.com"

fun main() {
	renderComposable(root = document.querySelector("head")!!) {
		Head()
	}
	
	val renderer = object : marked.TextRenderer() {
		override fun link(href: String?, title: String?, text: String) = """
			<a href="$href" ${title?.let { "title=$it" } ?: ""} class="link">$text</a>
		"""
	}
	
	marked.use(jso { this.renderer = renderer })
	
	val body = document.createElement("body") as HTMLBodyElement
	document.body = body
	
	renderComposableInBody {
		BrowserRouter("/") {
			val pathname = document.location?.pathname ?: "404"
			setTitle("${pathname.removePrefix("/").replace("/", " ").titlecase()} - Pierre Roy")
			
			Header()
			
			Div({
				id("main")
			}) {
				route("/") {
					Home()
				}
			}
			
			Footer()
			
			noMatch {
				redirect("/", true)
			}
		}
	}
}
