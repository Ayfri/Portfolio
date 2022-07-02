
import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLMetaElement

@Composable
fun Head() {
	val description = "The portfolio of myself, Pierre Roy."
	
	Meta(charset = "utf-8")
	Meta(name = "viewport", content = "width=device-width, initial-scale=1")
	Meta(name = "description", content = description)
	Meta(name = "author", content = "Ayfri")
	Meta(name = "twitter:card", content = "summary")
	Meta(name = "twitter:site", content = "@ayfri_")
	Meta(name = "twitter:creator", content = "@ayfri_")
	Meta(name = "twitter:title", content = "Compose")
	Meta(name = "twitter:description", content = description)
}

@Composable
fun Meta(name: String, content: String) {
	TagElement<HTMLMetaElement>("meta", {
		attr("name", name)
		attr("content", content)
	}, {})
}

@Composable
fun Meta(charset: String) {
	TagElement<HTMLMetaElement>("meta", {
		attr("charset", charset)
	}, {})
}

fun setTitle(title: String) {
	document.querySelector("title")?.textContent = title
}