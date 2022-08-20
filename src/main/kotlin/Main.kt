@file:Suppress("JS_NAME_CLASH", "JS_FAKE_NAME_CLASH")

import app.softwork.routingcompose.BrowserRouter
import header.Header
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.renderComposableInBody
import pages.AboutMe
import pages.Experiences
import pages.Home
import pages.Portfolio
import pages.Projects
import pages.Skills

const val MAIL_TO = "pierre.ayfri@gmail.com"

fun main() {
	renderComposable(root = document.querySelector("head")!!) {
		Head()
	}
	
	val renderer = object : marked.TextRenderer() {
		override fun link(href: String?, title: String?, text: String) = """
			<a href="$href" ${title?.let { "title=$it" } ?: ""} class="link">$text</a>
		""".trimIndent()
	}
	
	marked.use(jso { this.renderer = renderer })
	
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
		}
	}
}
