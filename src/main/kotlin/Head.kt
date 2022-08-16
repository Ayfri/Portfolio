import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLMetaElement
import org.w3c.dom.HTMLScriptElement
import style.AppStyle

private const val description = "The portfolio of myself, Pierre Roy."

@Composable
fun Head() {
	Meta(charset = "utf-8")
	MetaName(name = "viewport", content = "width=device-width, initial-scale=1")
	MetaName(name = "description", content = description)
	MetaName(name = "author", content = "Ayfri")
	
	MetaName(name = "twitter:card", content = "summary")
	MetaName(name = "twitter:site", content = "@Ayfri_")
	MetaName(name = "twitter:creator", content = "@ayfri_")
	MetaName(name = "twitter:title", content = "Ayfri Portfolio")
	MetaName(name = "twitter:description", content = description)
	
	MetaProperty("og:title", content = "Ayfri Portfolio")
	MetaProperty("og:description", content = description)
	MetaProperty("og:type", content = "website")
	MetaProperty("og:image", content = localImage("avatar.jpg"))
	
	document.location?.href?.let {
		MetaProperty("og:url", content = it)
	}
	
	Style(AppStyle)
	Link(href = "https://fonts.googleapis.com/css2?family=Open+Sans:wght@100;300;400;500;700&display=swap", rel = "stylesheet")
	Link(href = "https://dev-cats.github.io/code-snippets/JetBrainsMono.css", rel = "stylesheet")
	Script(src = "https://kit.fontawesome.com/74fed0e2b5.js", crossOrigin = CrossOrigin.ANONYMOUS)
}

@Composable
fun MetaName(name: String, content: String) {
	TagElement<HTMLMetaElement>("meta", {
		attr("name", name)
		attr("content", content)
	}) {}
}

@Composable
fun MetaProperty(property: String, content: String) {
	TagElement<HTMLMetaElement>("meta", {
		attr("property", property)
		attr("content", content)
	}) {}
}

@Composable
fun Meta(charset: String) {
	TagElement<HTMLMetaElement>("meta", {
		attr("charset", charset)
	}, {})
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