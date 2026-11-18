package io.github.ayfri.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.AppStyle
import io.github.ayfri.components.*
import io.github.ayfri.jsonld.JsonLD
import io.github.ayfri.jsonld.PageType
import io.github.ayfri.jsonld.pageGraph
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Main

/**
 * `context.route.toString()` still carries the `?_kobwebIsExporting=...` query during a static export, which
 * would ship a canonical pointing at a URL that does not exist. Only the path belongs in a canonical.
 */
val PageContext.canonicalUrl get() = AppGlobals["url"] + route.path.removeSuffix("/") + "/"

@Composable
fun PageLayout(
	title: String,
	description: String = AppGlobals["description"]!!,
	schemaType: PageType = PageType.WebPage,
	jsonLD: Array<JsonLD> = emptyArray(),
	content: @Composable () -> Unit,
) {
	Style(AppStyle)

	val context = rememberPageContext()
	val currentStub = context.route
	val canonical = context.canonicalUrl

	setPageHead(
		"$title - ${AppGlobals["author"]}'s Portfolio",
		description,
		canonical,
		jsonLD = pageGraph(canonical, schemaType, title, description, *jsonLD),
	)

	Header()

	Main({
		id("main")
	}) {
		content()
	}

	Footer()

	/** Keyed on the route: an unkeyed call would yank the page back to the top on every recomposition. */
	LaunchedEffect(currentStub.path) {
		window.scroll(0.0, 0.0)
	}
}
