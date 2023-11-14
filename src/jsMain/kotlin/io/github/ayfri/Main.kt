@file:Suppress("JS_NAME_CLASH", "JS_FAKE_NAME_CLASH")

package io.github.ayfri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import com.varabyte.kobweb.navigation.OpenLinkStrategy
import com.varabyte.kobweb.navigation.UpdateHistoryMode
import io.github.ayfri.externals.TextRenderer
import io.github.ayfri.externals.use
import kotlinx.browser.document
import kotlinx.browser.window
import web.http.fetch

const val MAIL_TO = "pierre.ayfri@gmail.com"

external fun require(path: String): dynamic

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
	val renderer = object : TextRenderer() {
		override fun link(href: String?, title: String?, text: String) = """
			<a href="$href" ${title?.let { "title=$it" } ?: ""} class="link">$text</a>
		""".trimIndent()
	}

	use(jso { this.renderer = renderer })

	LaunchedEffect(Unit) {
		val currentSlug = window.location.pathname.replace(Regex("/$"), "").substringAfterLast("/")
		val currentArticle = currentSlug.split("-")
			.joinToString("") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
		fetch("/json-ld/$currentArticle.json").json().then {
			val script = document.createElement("script")
			script.setAttribute("type", "application/ld+json")
			script.innerHTML = JSON.stringify(it.asDynamic())
			document.head?.appendChild(script)

		}
	}

	KobwebApp {
		content()
	}
}

@InitKobweb
fun initKobweb(context: InitKobwebContext) {
	context.router.setErrorHandler {
		if (it != 404) return@setErrorHandler
		context.router.navigateTo(
			"/",
			openExternalLinksStrategy = OpenLinkStrategy.IN_PLACE,
			openInternalLinksStrategy = OpenLinkStrategy.IN_PLACE,
			updateHistoryMode = UpdateHistoryMode.REPLACE
		)
	}
}
