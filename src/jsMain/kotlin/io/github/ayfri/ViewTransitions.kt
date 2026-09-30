package io.github.ayfri

import com.varabyte.kobweb.navigation.Router
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.events.MouseEvent
import kotlin.js.Promise

/** Leaves Compose a few shimmed frames to render the new page before the browser snapshots it. */
private const val RECOMPOSE_DELAY_MS = 60

/**
 * Routes same-origin link clicks through `document.startViewTransition`, so pages cross-fade and elements sharing a
 * `view-transition-name` (header, page title, active tab) morph into each other.
 *
 * Kobweb has no hook around its DOM swap and Compose renders on its own animation frame, so a route interceptor would
 * snapshot the page after it already changed. The click is caught in the capture phase instead, before Kobweb's anchor
 * handler. While the update callback runs the browser suppresses rendering, which also pauses `requestAnimationFrame`,
 * the clock Compose recomposes on, so it is swapped for a timeout shim until the new page is in the DOM.
 */
fun enableViewTransitions(router: Router) {
	if (document.asDynamic().startViewTransition == undefined) return

	document.addEventListener("click", { event ->
		val click = event.unsafeCast<MouseEvent>()
		if (click.defaultPrevented || click.button != 0.toShort() || click.metaKey || click.ctrlKey || click.shiftKey || click.altKey) return@addEventListener

		val anchor = click.target.unsafeCast<Element?>()?.closest("a[href]")?.unsafeCast<HTMLAnchorElement>() ?: return@addEventListener
		val samePage = anchor.pathname == window.location.pathname
		if (anchor.target.isNotEmpty() || anchor.hasAttribute("download") || anchor.origin != window.location.origin || samePage) return@addEventListener

		click.preventDefault()
		click.stopImmediatePropagation()
		document.asDynamic().startViewTransition {
			val requestAnimationFrame = window.asDynamic().requestAnimationFrame
			window.asDynamic().requestAnimationFrame = { callback: (Double) -> Unit ->
				window.setTimeout({ callback(window.performance.now()) }, 0)
			}
			router.navigateTo(anchor.pathname + anchor.search + anchor.hash)

			Promise { resolve, _ ->
				window.setTimeout({
					window.asDynamic().requestAnimationFrame = requestAnimationFrame
					resolve(Unit)
				}, RECOMPOSE_DELAY_MS)
			}
		}
	}, js("({ capture: true })"))
}
