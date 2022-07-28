import app.softwork.routingcompose.BrowserRouter
import header.Header
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.renderComposableInBody
import org.w3c.dom.HTMLBodyElement
import pages.Home

fun main() {
	renderComposable(root = document.querySelector("head")!!) {
		Head()
	}
	
	val body = document.createElement("body") as HTMLBodyElement
	document.body = body
	
	renderComposableInBody {
		BrowserRouter("/") {
			redirect("/about.html", target = "/about", hide = true)
			
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
		}
	}
}
