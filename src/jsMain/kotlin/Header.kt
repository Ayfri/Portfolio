import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Section


@Composable
fun Header() {
	Section {
		tabs.forEach {
			Tab(it)
		}
	}
}