package io.github.ayfri

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import io.github.ayfri.externals.MarkedOptions
import io.github.ayfri.externals.TextRenderer
import io.github.ayfri.externals.use
import io.github.ayfri.pages.NotFoundPage

const val MAIL_TO = "pierre.ayfri@gmail.com"

private val markedRenderer = object : TextRenderer() {
	override fun link(href: String?, title: String?, text: String) =
		"""<a href="$href" ${title?.let { "title=$it" } ?: ""} class="link">$text</a>"""

	override fun code(code: String, infoString: String, escaped: Boolean): String {
		val language = if (infoString.isEmpty()) "nohighlight" else "language-$infoString"
		return """<pre><code class="$language line-numbers">$code</code></pre>"""
	}
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) = KobwebApp { content() }

@InitKobweb
fun initKobweb(context: InitKobwebContext) {
	// `marked` is a JS singleton: configuring it once at startup instead of on every recomposition of the app root.
	use(MarkedOptions(renderer = markedRenderer))

	context.router.setErrorPage {
		NotFoundPage()
	}
}
