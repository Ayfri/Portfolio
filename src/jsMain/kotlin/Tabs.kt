import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text

data class Tab(val name: String, val link: String)

val tabs = listOf(
	Tab("Home", "/"),
	Tab("About", "/about"),
	Tab("Blog", "/blog"),
	Tab("Contact", "/contact")
)

@Composable
fun Tab(tab: Tab) {
	A(href = tab.link) {
		Text(tab.name)
	}
}