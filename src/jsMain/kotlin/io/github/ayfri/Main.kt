@file:Suppress("JS_NAME_CLASH", "JS_FAKE_NAME_CLASH")

package io.github.ayfri

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import com.varabyte.kobweb.navigation.OpenLinkStrategy
import com.varabyte.kobweb.navigation.UpdateHistoryMode
import io.github.ayfri.externals.TextRenderer
import io.github.ayfri.externals.use

const val MAIL_TO = "pierre.ayfri@gmail.com"

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
	val renderer = object : TextRenderer() {
		override fun link(href: String?, title: String?, text: String) = """
			<a href="$href" ${title?.let { "title=$it" } ?: ""} class="link">$text</a>
		""".trimIndent()
	}

	use(jso { this.renderer = renderer })

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
