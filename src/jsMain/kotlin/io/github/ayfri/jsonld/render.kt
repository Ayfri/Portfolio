
import androidx.compose.runtime.Composable
import io.github.ayfri.components.Script
import io.github.ayfri.jsonEncoder
import io.github.ayfri.jsonld.JsonLD
import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable


@Composable
fun JsonLD(jsonLD: JsonLD) = renderComposable(document.head!!) {
    Script {
        attr("type", "application/ld+json")
        ref {
            it.innerHTML = jsonEncoder.encodeToString(jsonLD)

			onDispose {}
        }
    }
}
