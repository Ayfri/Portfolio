
import app.softwork.routingcompose.BrowserRouter
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
	renderComposable(rootElementId = "root") {
		BrowserRouter("/") {
			route("/test") {
				Text("Test")
			}
			
			noMatch {
				Header()
				Text(this.remainingPath)
			}
		}
	}
}
