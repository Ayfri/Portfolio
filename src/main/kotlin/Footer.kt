import androidx.compose.runtime.Composable
import header.GITHUB_LINK
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.maxLength
import org.jetbrains.compose.web.attributes.minLength
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
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
fun FooterContactField(
	label: String,
	id: String,
	type: InputType<String> = InputType.Text,
	required: Boolean = false,
	range: IntRange? = null,
	textArea: Boolean = false,
) {
	Div {
		Label(id) {
			Text(label)
		}
		
		if (textArea) {
			TextArea {
				id(id)
				if (required) attr("required", "")
				placeholder(label)
				minLength(range?.first ?: 0)
				maxLength(range?.last ?: Int.MAX_VALUE)
			}
		} else {
			Input(type) {
				id(id)
				if (required) required()
				placeholder(label)
				minLength(range?.first ?: 0)
				maxLength(range?.last ?: Int.MAX_VALUE)
			}
		}
	}
}

@Composable
fun Footer() {
	Footer({
		classes(AppStyle.footer)
	}) {
		Div({
			classes(AppStyle.footerContact)
		}) {
			H2({
				classes(AppStyle.monoFont)
			}) {
				Text("Contact Me :")
			}
			
			Div({
				classes(AppStyle.footerContactInputs)
			}) {
				FooterContactField(label = "First Name", id = "first-name", range = 1..20)
				FooterContactField(label = "Last Name", id = "last-name", range = 1..32)
				FooterContactField(label = "Subject", id = "subject", required = true, range = 5..96)
				FooterContactField(label = "Message", id = "message", textArea = true, required = true, range = 16..512)
			}
			
			Button({
				onClick {
					val firstName = (document.querySelector("#first-name") as HTMLInputElement).value
					val lastName = (document.querySelector("#last-name") as HTMLInputElement).value
					val subjectString = (document.querySelector("#subject") as HTMLInputElement).value
					val message = (document.querySelector("#message") as HTMLTextAreaElement).value
					
					val subject = subjectString.ifBlank { "No Subject" }
					val name = firstName.ifBlank { lastName }
					
					val body = """
						${name.ifNotBlank { "Name: $name" }}
						${subject.ifNotBlank { "Subject: $subject" }}
						
						${message.ifNotBlank { "Message: $message" }}
					""".trimIndent().replace("\n", "%0A")
					
					window.open("mailto:$MAIL_TO?subject=$subject&body=$body", "_blank")
				}
			}) {
				Text("Send message")
				
				I(FontAwesomeType.SOLID, "arrow-up-right-from-square")
			}
		}
		
		Div({
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
}