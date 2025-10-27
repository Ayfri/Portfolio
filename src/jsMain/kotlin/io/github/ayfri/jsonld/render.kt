package io.github.ayfri.jsonld

import androidx.compose.runtime.Composable
import io.github.ayfri.components.Script
import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable


@Composable
fun JsonLD(jsonLD: JsonLD) = renderComposable(document.head!!) {
	Script {
		attr("type", "application/ld+json")
		ref {
			it.innerHTML = JSON.stringify(jsonLD)

			onDispose {}
		}
	}
}
