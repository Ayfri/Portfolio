
import app.softwork.routingcompose.BrowserRouter
import header.Header
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.renderComposableInBody
import org.w3c.dom.HTMLBodyElement

fun main() {
	renderComposable(root = document.querySelector("head")!!) {
		Head()
	}
	
	val body = document.createElement("body") as HTMLBodyElement
	document.body = body
	
	renderComposableInBody {
		BrowserRouter("/") {
			redirect("/about.html", target = "/about", hide = true)
			
			Header()
			
			setTitle("${document.location?.pathname?.titlecase() ?: "404"} - Pierre Roy")
			
			noMatch {
				Text("$remainingPath not found")
			}
		}
	}
}
