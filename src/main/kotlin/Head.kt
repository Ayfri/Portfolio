import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLMetaElement
import org.w3c.dom.HTMLScriptElement
import style.AppStyle

@Composable
fun Head() {
	val description = "The portfolio of myself, Pierre Roy."
	
	Meta(charset = "utf-8")
	Meta(name = "viewport", content = "width=device-width, initial-scale=1")
	Meta(name = "description", content = description)
	Meta(name = "author", content = "Ayfri")
	
	Meta(name = "twitter:card", content = "summary")
	Meta(name = "twitter:site", content = "@Ayfri_")
	Meta(name = "twitter:creator", content = "@ayfri_")
	Meta(name = "twitter:title", content = "Ayfri Portfolio")
	Meta(name = "twitter:description", content = description)
	
	Meta(name = "og:title", content = "Ayfri Portfolio")
	Meta(name = "og:description", content = description)
	Meta(name = "og:type", content = "website")
	
	document.location?.href?.let {
		Meta(name = "og:url", content = it)
	}
	
	Style(AppStyle)
	Link(href = "https://fonts.googleapis.com/css2?family=Roboto:wght@100;300;400;500;700&display=swap", rel = "stylesheet")
	Script(src = "https://kit.fontawesome.com/74fed0e2b5.js", crossOrigin = CrossOrigin.ANONYMOUS)
}

@Composable
fun Meta(name: String, content: String) {
	TagElement<HTMLMetaElement>("meta", {
		attr("name", name)
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
		if (mode.value.isNotEmpty()) attr("mode", mode.value)
	}) {}
}

fun setTitle(title: String) {
	document.querySelector("title")?.textContent = title
}