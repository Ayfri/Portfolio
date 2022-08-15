
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLParagraphElement

fun localImage(path: String) = "images/$path"

inline fun <T> jso(block: T.() -> Unit = {}) = (js("{}") as T).apply(block)

fun AttrsScope<HTMLParagraphElement>.markdownParagraph(text: String, vararg classes: String) {
	ref {
		if (classes.isNotEmpty()) it.classList.add(*classes)
		it.innerHTML = marked.parseInline(text)
		
		onDispose {}
	}
}

inline fun <C : CharSequence, R : C> C.ifNotBlank(block: (C) -> R) = if (isNotBlank()) block(this) else ""