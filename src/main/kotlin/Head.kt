import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLScriptElement
import style.AppStyle

@Composable
fun Head() {
	Style(AppStyle)
	Link(href = "https://fonts.googleapis.com/css2?family=Open+Sans:wght@100;300;400;500;700&display=swap", rel = "stylesheet")
	Link(href = "https://dev-cats.github.io/code-snippets/JetBrainsMono.css", rel = "stylesheet")
	Script(src = "https://kit.fontawesome.com/74fed0e2b5.js", crossOrigin = CrossOrigin.ANONYMOUS)
}

@Composable
fun Link(href: String, rel: String = "", type: String = "") {
	TagElement<HTMLLinkElement>("link", {
		attr("href", href)
		if (rel.isNotEmpty()) attr("rel", rel)
		if (type.isNotEmpty()) attr("type", type)
	}) {}
}

enum class ScriptMode(val value: String) {
	DEFAULT(""),
	ASYNC("async"),
	DEFER("defer")
}

enum class CrossOrigin(val value: String) {
	NONE(""),
	ANONYMOUS("anonymous"),
	USE_CREDENTIALS("use-credentials")
}

@Composable
fun Script(src: String, crossOrigin: CrossOrigin = CrossOrigin.NONE, mode: ScriptMode = ScriptMode.DEFAULT) {
	TagElement<HTMLScriptElement>("script", {
		attr("src", src)
		if (crossOrigin.value.isNotEmpty()) attr("crossorigin", crossOrigin.value)
		if (mode.value.isNotEmpty())
			when (mode) {
				ScriptMode.ASYNC -> attr("async", "")
				ScriptMode.DEFER -> attr("defer", "")
				else -> Unit
			}
	}) {}
}

fun setTitle(title: String) {
	document.querySelector("title")?.textContent = title
}