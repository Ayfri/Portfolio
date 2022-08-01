import androidx.compose.runtime.Composable
import header.GITHUB_LINK
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Ul
import style.AppStyle
import kotlin.js.Date

const val DISCORD_LINK = "https://discord.gg/BySjRNQ9Je"
const val LINKEDIN_LINK = "https://www.linkedin.com/in/pierre-roy-411a6821b/"
const val TWITCH_LINK = "https://www.twitch.tv/ayfri_"
const val TWITTER_LINK = "https://twitter.com/@ayfri_"

data class FooterSocial(val iconName: String, val url: String)

val footerSocials = listOf(
	FooterSocial("discord", DISCORD_LINK),
	FooterSocial("github", GITHUB_LINK),
	FooterSocial("linkedin", LINKEDIN_LINK),
	FooterSocial("twitch", TWITCH_LINK),
	FooterSocial("twitter", TWITTER_LINK),
)

@Composable
fun Footer() {
	Footer({
		classes(AppStyle.footerInfo)
	}) {
		Ul({
			classes("top")
		}) {
			footerSocials.forEach {
				Li {
					A(it.url, {
						attr("target", "_blank")
					}) {
						I(FontAwesomeType.BRAND, it.iconName)
					}
				}
			}
		}
		
		Div {
			P("© ${Date().getFullYear()} Pierre Roy - All rights reserved.")
		}
	}
}