
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.dom.Section


@Composable
fun Header() {
	Section(
		{
			style {
				display(DisplayStyle.Flex)
			}
		}
	) {
		tabs.forEach {
			Tab(it)
		}
	}
}