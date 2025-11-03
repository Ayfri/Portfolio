package io.github.ayfri.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import io.github.ayfri.AppStyle
import io.github.ayfri.MAIL_TO
import io.github.ayfri.data.*
import io.github.ayfri.ifNotBlank
import io.github.ayfri.utils.list
import io.github.ayfri.utils.margin
import io.github.ayfri.utils.n
import io.github.ayfri.utils.size
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import kotlin.js.Date

data class FooterSocial(val iconName: String, val url: String)

val footerSocials = listOf(
	FooterSocial("discord", DISCORD_LINK),
	FooterSocial("github", REPO_LINK),
	FooterSocial("linkedin", LINKEDIN_LINK),
	FooterSocial("twitch", TWITCH_LINK),
	FooterSocial("x-twitter", TWITTER_LINK),
)

data class ProjectLink(val name: String, val url: String)

val projectLinks = listOf(
	ProjectLink("Kore", "https://kore.ayfri.com"),
	ProjectLink("Atom Clicker", "https://atom-clicker.ayfri.com"),
	ProjectLink("PokeCards-Collector", "https://pokecards-collector.ayfri.com"),
	ProjectLink("Cursors Draw", "https://cursors.draw.ayfri.com"),
	ProjectLink("Realtime TodoList", "https://realtime-todolist.pages.dev"),
	ProjectLink("GPT Images", "https://gpt-images.ayfri.com"),
	ProjectLink("A* Follow", "https://afollow.ayfri.com"),
)

@Composable
fun FooterContactField(
	label: String,
	id: String,
	type: InputType<String> = InputType.Text,
	required: Boolean = false,
	range: IntRange? = null,
	textArea: Boolean = false,
	autocomplete: String? = null,
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
				if (autocomplete != null) attr("autocomplete", autocomplete)
			}
		} else {
			Input(type) {
				id(id)
				if (required) attr("required", "")
				placeholder(label)
				minLength(range?.first ?: 0)
				maxLength(range?.last ?: Int.MAX_VALUE)
				if (autocomplete != null) attr("autocomplete", autocomplete)
			}
		}
	}
}

@Composable
fun Footer() {
	Style(FooterStyle)

	Footer({
		classes(FooterStyle.footerWrapper)
	}) {
		// Top decorative bar
		Div({
			classes(FooterStyle.topBar)
		})

		// Contact Section (Above Footer)
		Div({
			classes(FooterStyle.contactSection)
		}) {
			Div({
				classes(FooterStyle.contactContainer)
			}) {
				H3({
					classes(AppStyle.monoFont, FooterStyle.contactHeading)
				}) {
					Text("Contact Me")
				}

				Div({
					classes(FooterStyle.contactForm)
				}) {
					Div({
						classes(FooterStyle.footerContactInputs)
					}) {
						FooterContactField(label = "First Name", id = "first-name", range = 1..20, autocomplete = "given-name")
						FooterContactField(label = "Last Name", id = "last-name", range = 1..32, autocomplete = "family-name")
						FooterContactField(label = "Subject", id = "subject", required = true, range = 5..96, autocomplete = "off")
						FooterContactField(label = "Message", id = "message", textArea = true, required = true, range = 16..512, autocomplete = "off")
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
						I(FontAwesomeType.SOLID, "arrow-up-right-from-square") {
							marginLeft(0.5.cssRem)
						}
					}
				}
			}
		}

		// Main Footer
		Div({
			classes(FooterStyle.footer)
		}) {
			Div({
				classes(FooterStyle.footerContent)
			}) {
				// Projects Column
				Div({
					classes(FooterStyle.footerColumn)
				}) {
					H3({
						classes(AppStyle.monoFont, FooterStyle.footerHeading)
					}) {
						Text("My Projects")
					}

					Ul({
						classes(FooterStyle.footerList)
					}) {
						projectLinks.forEach { project ->
							Li {
								A(project.url, {
									target(ATarget.Blank)
									title("Visit ${project.name}")
								}) {
									Text(project.name)
								}
							}
						}
					}

					A(href = "/projects/", {
						classes(FooterStyle.footerLink)
					}) {
						Text("View all projects")
						I(FontAwesomeType.SOLID, "arrow-right") {
							marginLeft(0.5.cssRem)
						}
					}
				}

				// Links Column
				Div({
					classes(FooterStyle.footerColumn)
				}) {
					H3({
						classes(AppStyle.monoFont, FooterStyle.footerHeading)
					}) {
						Text("Quick Links")
					}

					Ul({
						classes(FooterStyle.footerList)
					}) {
						Li {
							A("/", {}) {
								Text("Home")
							}
						}
						Li {
							A("/about-me/", {}) {
								Text("About Me")
							}
						}
						Li {
							A("/skills/", {}) {
								Text("Skills")
							}
						}
						Li {
							A("/experiences/", {}) {
								Text("Experiences")
							}
						}
						Li {
							A("/portfolio/", {}) {
								Text("Portfolio")
							}
						}
						Li {
							A(href = "/cv.pdf", {
								target(ATarget.Blank)
								attr("download", "CV Pierre Roy.pdf")
							}) {
								Text("Download CV")
							}
						}
					}
				}

				// Social Column
				Div({
					classes(FooterStyle.footerColumn)
				}) {
					H3({
						classes(AppStyle.monoFont, FooterStyle.footerHeading)
					}) {
						Text("Follow Me")
					}

					Div({
						classes(FooterStyle.socialLinks)
					}) {
						footerSocials.forEach { footerSocial ->
							A(footerSocial.url, {
								classes(FooterStyle.socialIcon)
								attr("target", "_blank")
								title(footerSocial.iconName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
							}) {
								I(FontAwesomeType.BRAND, footerSocial.iconName)
							}
						}
					}

					// Buy Me A Coffee link
					A("https://buymeacoffee.com/ayfri", {
						classes(FooterStyle.buyMeCoffeeLink)
						attr("target", "_blank")
						title("Support me on Buy Me A Coffee")
					}) {
						I(FontAwesomeType.SOLID, "mug-hot") {
							marginRight(0.5.cssRem)
						}
						Text("Buy Me A Coffee")
					}
				}
			}

			// Copyright Bar
			Div({
				classes(FooterStyle.footerCopyright)
			}) {
				P("© ${Date().getFullYear()} Pierre Roy - All rights reserved.")
			}
		}
	}
}

object FooterStyle : StyleSheet() {
	const val FOOTER_COLOR = "#1A1225"
	const val FOOTER_DARK_COLOR = "#0F0A1A"
	const val FOOTER_ACCENT_START = "#00D4FF"
	const val FOOTER_ACCENT_END = "#FF0080"
	const val FOOTER_LINK_HOVER = "#00D4FF"
	const val FOOTER_HEADING_COLOR = "#ffffff"
	const val FOOTER_TEXT_COLOR = "#cccccc"
	const val FOOTER_LINK_COLOR = "#aaaaaa"

	val footerWrapper by style {
		backgroundImage(linearGradient(180.deg) {
			add(Color("#1E1535"))
			add(Color(FOOTER_COLOR), 30.percent)
			add(Color("#0F0A1A"), 100.percent)
		})

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		width(100.percent)
	}

	val topBar by style {
		height(2.px)
		width(100.percent)
		backgroundImage(linearGradient(90.deg) {
			add(Color.transparent)
			add(Color(FOOTER_ACCENT_START), 20.percent)
			add(Color(FOOTER_ACCENT_END), 80.percent)
			add(Color.transparent)
		})
	}

	val contactSection by style {
		padding(2.cssRem, 0.px)
		color(Color.white)
		backgroundImage(linearGradient(135.deg) {
			add(Color("#1A1225"))
			add(Color("#2A1B3D"), 50.percent)
			add(Color("#1A1225"), 100.percent)
		})
		borderBottom(2.px, LineStyle.Solid, Color.transparent)
		property("border-image", "linear-gradient(90deg, $FOOTER_ACCENT_START, $FOOTER_ACCENT_END) 1")
	}

	val contactContainer by style {
		maxWidth(600.px)
		margin(0.px, auto)
		padding(0.px, 2.cssRem)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				padding(0.px, 1.cssRem)
			}
		}
	}

	val contactHeading by style {
		color(Color.white)
		fontSize(1.5.cssRem)
		fontWeight(700)
		textAlign(TextAlign.Center)
		marginTop(0.px)
		marginBottom(1.5.cssRem)
		backgroundImage(linearGradient(45.deg) {
			add(Color(FOOTER_ACCENT_START))
			add(Color(FOOTER_ACCENT_END))
		})
		property("-webkit-background-clip", "text")
		property("-webkit-text-fill-color", "transparent")
		property("-moz-text-fill-color", "transparent")
		property("-moz-background-clip", "text")
	}

	val footer by style {
		color(Color(FOOTER_TEXT_COLOR))
	}

	val footerContent by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		flexWrap(FlexWrap.Wrap)
		justifyContent(JustifyContent.SpaceAround)
		maxWidth(1200.px)
		margin(0.px, auto)
		padding(3.cssRem, 2.cssRem, 2.cssRem)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				flexDirection(FlexDirection.Column)
				gap(2.cssRem)
				padding(2.cssRem, 1.cssRem)
			}
		}
	}

	val footerColumn by style {
		flex(1, 1, 250.px)
		margin(0.px, 1.cssRem, 2.cssRem)
		maxWidth(350.px)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				maxWidth(100.percent)
				margin(0.px)
			}
		}
	}

	val footerHeading by style {
		borderBottom(2.px, LineStyle.Solid, Color.transparent)
		borderImageSlice(BorderImageSlice.of(1))
		color(Color(FOOTER_HEADING_COLOR))
		fontSize(1.2.cssRem)
		fontWeight(700)
		marginTop(0.px)
		marginBottom(1.2.cssRem)
		paddingBottom(0.5.cssRem)
		property("border-image", "linear-gradient(90deg, $FOOTER_ACCENT_START, $FOOTER_ACCENT_END) 1")
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val footerList by style {
		listStyle(ListStyle.None)
		padding(0.px)
		margin(0.px)

		"li" {
			marginBottom(0.8.cssRem)
		}

		"a" {
			color(Color(FOOTER_LINK_COLOR))
			textDecorationLine(TextDecorationLine.None)
			transitions {
				defaultDuration(0.3.s)
				properties("color", "text-shadow")
			}

			hover(self) style {
				color(Color(FOOTER_LINK_HOVER))
				textDecorationLine(TextDecorationLine.Underline)
				property("text-shadow", "0 0 8px rgba(0, 212, 255, 0.6)")
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val footerLink by style {
		color(Color(FOOTER_LINK_COLOR))
		textDecorationLine(TextDecorationLine.None)
		display(DisplayStyle.InlineBlock)
		marginTop(0.8.cssRem)
		transitions {
			defaultDuration(0.3.s)
			properties("color", "text-shadow")
		}

		hover(self) style {
			color(Color(FOOTER_LINK_HOVER))
			textDecorationLine(TextDecorationLine.Underline)
			property("text-shadow", "0 0 8px rgba(0, 212, 255, 0.6)")
		}
	}

	val socialLinks by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		flexWrap(FlexWrap.Wrap)
		gap(2.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val socialIcon by style {
		display(DisplayStyle.InlineBlock)
		fontSize(2.25.cssRem)
		color(Color(FOOTER_LINK_COLOR))
		transitions {
			defaultDuration(0.3.s)
			properties("color", "transform", "text-shadow")
		}

		hover(self) style {
			backgroundImage(linearGradient(45.deg) {
				add(Color(FOOTER_ACCENT_START))
				add(Color(FOOTER_ACCENT_END))
			})
			transform {
				translateY((-3).px)
				scale(1.1)
			}
			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
			property("text-shadow", "0 0 15px rgba(255, 0, 128, 0.8)")
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val buyMeCoffeeLink by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		backgroundColor(Color("#ffdd11"))
		color(Color("#000000"))
		padding(0.6.cssRem, 1.cssRem)
		borderRadius(0.5.cssRem)
		marginTop(1.5.cssRem)
		fontWeight(700)
		textDecorationLine(TextDecorationLine.None)
		width(Width.FitContent)

		transitions {
			defaultDuration(0.3.s)
			properties("background-color", "transform", "box-shadow")
		}

		hover(self) style {
			backgroundColor(Color("#FFEA7F"))
			transform { translateY((-3).px) }
			property("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.2)")
		}
	}

	val contactForm by style {
		"button" {
			backgroundImage(linearGradient(45.deg) {
				add(Color(FOOTER_ACCENT_START))
				add(Color(FOOTER_ACCENT_END))
			})
			color(Color.white)
			marginTop(1.cssRem)
			width(100.percent)
			fontWeight(700)
			property("box-shadow", "0 0 15px rgba(0, 212, 255, 0.4)")

			hover {
				property("box-shadow", "0 0 20px rgba(255, 0, 128, 0.6)")
				scale(1.02)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val footerContactInputs by style {
		display(DisplayStyle.Grid)
		gap(1.cssRem)
		gridTemplateColumns(GridEntry.repeat(2, GridEntry.TrackSize(1.fr)))
		gridTemplateRows(GridEntry.repeat(4, GridEntry.TrackSize.Auto))

		child(self, nthChild(1.n)) style {
			gridArea("1", "1", "2", "2")
		}

		child(self, nthChild(2.n)) style {
			gridArea("1", "2", "2", "3")
		}

		child(self, nthChild(3.n)) style {
			gridArea("2", "1", "3", "3")
		}

		child(self, nthChild(4.n)) style {
			gridArea("3", "1", "5", "3")
		}

		child(self, universal) style {
			size(100.percent)
			minWidth(3.cssRem)

			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(.4.cssRem)

			type("label") style {
				color(Color.white)
				fontSize(0.9.cssRem)
			}

			list("input", "textarea") style {
				backgroundColor(Color("#252525"))
				borderRadius(.4.cssRem)
				color(Color("#FFFFFF"))
				border(1.px, LineStyle.Solid, Color("#444444"))
				fontFamily("Open Sans", "sans-serif")
				fontOpticalSizing(FontOpticalSizing.Auto)
				padding(.5.cssRem)
				transitions {
					defaultDuration(0.3.s)
					properties("border-color")
				}

				self + focus style {
					borderColor(Color.white)
					outlineStyle(LineStyle.None)
				}
			}

			list("input::placeholder", "textarea::placeholder") style {
				color(Color("#FFFFFF7F"))
			}

			type("textarea") style {
				height(120.px)
				resize(Resize.None)
			}
		}

		media(mediaMaxWidth(768.px)) {
			self {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
			}
		}
	}

	val footerCopyright by style {
		backgroundColor(Color(FOOTER_DARK_COLOR))
		padding(1.cssRem)
		textAlign(TextAlign.Center)
		fontSize(0.9.cssRem)
		color(Color("#888888"))
		borderTop(1.px, LineStyle.Solid, Color.transparent)
		property("border-image", "linear-gradient(90deg, transparent, $FOOTER_ACCENT_START, $FOOTER_ACCENT_END, transparent) 1")

		"p" {
			margin(0.px)
		}
	}
}
