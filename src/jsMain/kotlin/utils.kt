import externals.parse
import kotlinx.serialization.InternalSerializationApi
import org.intellij.lang.annotations.Language
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLParagraphElement

fun localImage(path: String) = "images/$path"

inline fun <T> jso(block: T.() -> Unit = {}) = (js("{}") as T).apply(block)

@OptIn(InternalSerializationApi::class)
fun AttrsScope<HTMLParagraphElement>.markdownParagraph(
	@Language("markdown") text: String,
	breaks: Boolean = false,
	vararg classes: String,
) {
	ref {
		if (classes.isNotEmpty()) it.classList.add(*classes)

		val textToParse = if (breaks) text.replace("\n", "<br>") else text
		it.innerHTML = parse(textToParse)

		onDispose {}
	}
}

inline fun <C : CharSequence, R : C> C.ifNotBlank(block: (C) -> R) = if (isNotBlank()) block(this) else ""
