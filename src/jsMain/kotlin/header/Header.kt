package header

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Header
import style.AppStyle


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