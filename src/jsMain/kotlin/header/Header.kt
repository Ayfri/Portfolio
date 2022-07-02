package header
import AppStyle
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Header


@Composable
fun Header() {
	Header({
		classes(AppStyle.navbar)
	}) {
		tabs.forEach {
			Tab(it)
		}
	}
}