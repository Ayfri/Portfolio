package header

import FontAwesomeType
import I
import P
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Header
import style.AppStyle

const val PERSONAL_GITHUB = "https://github.com/Ayfri"

@Composable
fun Header() {
	Header({
		classes(AppStyle.navbar)
	}) {
		Div({
			classes(AppStyle.navbarPart, AppStyle.navbarLinks)
		}) {
			tabs.forEach {
				Tab(it)
			}
		}
		
		A(PERSONAL_GITHUB, {
			classes(AppStyle.navbarPart, AppStyle.navbarGithub)
		}) {
			P("Ayfri")
			
			I(FontAwesomeType.BRAND, "github")
		}
	}
}