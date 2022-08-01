import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.AttrsScope

fun localImage(path: String) = "images/$path"

inline fun <T> jso(block: T.() -> Unit = {}) = (js("{}") as T).apply(block)

fun AttrsScope<*>.markdownParagraph(text: String, vararg classes: String) {
	ref {
		val element = document.createElement("p")
		element.classList.add(*classes)
		element.innerHTML = marked.parseInline(text)
		
		it.insertAdjacentElement("beforeend", element)
		
		onDispose {}
	}
}