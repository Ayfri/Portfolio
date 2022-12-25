import androidx.compose.runtime.Composable
import header.GITHUB_LINK
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import style.AppStyle
import style.utils.*
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
				if (required) attr("required", "")
				placeholder(label)
				minLength(range?.first ?: 0)
				maxLength(range?.last ?: Int.MAX_VALUE)
			}
		}
	}
}

@Composable
fun Footer() {
	Style(FooterStyle)

	Footer({
		classes(FooterStyle.footer)
	}) {
		Div({
			classes(FooterStyle.footerContact)
		}) {
			H2({
				classes(AppStyle.monoFont)
			}) {
				Text("Contact Me :")
			}

			Div({
				classes(FooterStyle.footerContactInputs)
			}) {
				FooterContactField(label = "First Name", id = "first-name", range = 1..20)
				FooterContactField(label = "Last Name", id = "last-name", range = 1..32)
				FooterContactField(label = "Subject", id = "subject", required = true, range = 5..96)
				FooterContactField(label = "Message", id = "message", textArea = true, required = true, range = 16..512)
			}

			Button({
				classes(AppStyle.button)

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
			classes(FooterStyle.footerInfo)
		}) {
			val cvPath = "${window.location.origin}/cv.pdf"

			A(href = cvPath, {
				target(ATarget.Blank)
				attr("download", "CV Pierre Roy.pdf")
				classes(AppStyle.button, FooterStyle.footerCVButton)
			}) {
				Text("Download my CV")
			}

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

object FooterStyle : StyleSheet() {
	const val footerColor = "#1a1120"
	const val footerLinkHover = "#cccccc"

	val footerCVButtonHover by keyframes {
		from {
			top((-100).percent)
		}

		to {
			top(0.percent)
		}
	}

	val footer by style {
		background(linearGradient(180.deg) {
			stop(Color("#302F39"))
			stop(Color("#211C24"), 100.percent)
		})
		color(Color.white)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val footerContact by style {
		AppStyle.buttonColor(Color("#50435A"))
		margin(0.px, auto)
		padding(2.cssRem, 0.px)
		position(Position.Relative)
		width(clamp(20.cssRem, 40.vw, 60.cssRem))

		"button" {
			position(Position.Relative)
			left(50.percent)
			transform {
				translateX((-50).percent)
			}

			"i" {
				paddingLeft(.4.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				boxSizing("border-box")
				padding(1.5.cssRem)
				width(100.percent)
			}
		}
	}

	val footerContactInputs by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns(repeat(2, 1.fr))
		gridTemplateRows(repeat(4, 1.fr))
		gap(1.cssRem)
		margin(1.cssRem, 0.px)

		child(self, nthChild(Nth.Functional(1))) style {
			gridArea("1", "1", "2", "2")
		}

		child(self, nthChild(Nth.Functional(2))) style {
			gridArea("1", "2", "2", "3")
		}

		child(self, nthChild(Nth.Functional(3))) style {
			gridArea("2", "1", "3", "3")
		}

		child(self, nthChild(Nth.Functional(4))) style {
			gridArea("3", "1", "5", "3")
		}

		child(self, universal) style {
			size(100.percent)
			minWidth(3.cssRem)

			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(.4.cssRem)

			"label" style {
				color(Color("#CFCFD2"))
				fontSize(1.cssRem)
			}

			list("input", "textarea") style {
				backgroundColor(Color("#252525"))
				borderRadius(.4.cssRem)
				color(Color("#FFFFFF"))
				border {
					color(Color.white)
					style(LineStyle.Solid)
					width(2.px)
				}
				fontFamily("Open Sans", "sans-serif")
				padding(.5.cssRem)
			}

			list("input::placeholder", "textarea::placeholder") style {
				color(Color("#FFFFFF7F"))
			}

			"textarea" style {
				height(100.percent)
				resize(Resize.None)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)

				"textarea" style {
					height(4.cssRem)
				}
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val footerCVButton by style {
		val buttonBackgroundColor = Color("#252525")

		AppStyle.buttonColor(buttonBackgroundColor)
		position(Position.Relative)
		overflow(Overflow.Hidden)
		display(DisplayStyle.InlineBlock)

		self + before style {
			backgroundColor(buttonBackgroundColor)
			property("content", "'\\f019'")
			position(Position.Absolute)
			inset(0.px)

			fontFamily("Font Awesome 6 Free")
			fontSize(2.cssRem)
			lineHeight(3.cssRem)
			color(Color("#69CF75"))
			transitions {
				defaultDelay(.25.s)
				properties("color")
			}

			display(DisplayStyle.None)
		}

		hover(self) + before style {
			animation(footerCVButtonHover) {
				duration(.3.s)
				timingFunction(AnimationTimingFunction.EaseInOut)
			}
			display(DisplayStyle.Block)
		}

		group(self + active + before, self + focus + before) style {
			display(DisplayStyle.Block)
			color(Color("#32AC66"))
		}
	}

	val footerInfo by style {
		backgroundColor(Color(footerColor))
		color(Color.white)
		padding(2.cssRem, 0.px, .5.cssRem)
		textAlign("center")
		width(100.percent)

		className("top") style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			gap(2.cssRem)
			justifyContent(JustifyContent.Center)
			listStyle("none")
			padding(0.px)

			"a" style {
				color(Color.white)
				fontSize(2.5.cssRem)

				hover(self) style {
					color(Color(footerLinkHover))
				}
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			self {
				className("top") style {
					gap(1.cssRem)

					type("a") style {
						fontSize(1.8.cssRem)
					}
				}

				"p" {
					fontSize(.85.cssRem)
				}
			}
		}
	}
}
