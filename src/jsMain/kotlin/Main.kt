
import app.softwork.routingcompose.HashRouter
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
	renderComposable(rootElementId = "root") {
		HashRouter("/") {
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
