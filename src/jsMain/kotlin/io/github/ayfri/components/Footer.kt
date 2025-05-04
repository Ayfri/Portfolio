package io.github.ayfri.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.borderColor
import com.varabyte.kobweb.compose.css.gridTemplateRows
import com.varabyte.kobweb.compose.css.textAlign
import io.github.ayfri.AppStyle
import io.github.ayfri.MAIL_TO
import io.github.ayfri.data.*
import io.github.ayfri.ifNotBlank
import io.github.ayfri.utils.*
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
	ProjectLink("Pokestore", "https://pokestore.ayfri.com"),
	ProjectLink("Cursors Draw", "https://cursors.draw.ayfri.com"),
	ProjectLink("Realtime TodoList", "https://realtime-todolist.pages.dev"),
	ProjectLink("GPT Images", "https://gpt-images.ayfri.com"),
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
		classes(FooterStyle.footerWrapper)
	}) {
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
						footerSocials.forEach {
							A(it.url, {
								classes(FooterStyle.socialIcon)
								attr("target", "_blank")
								title(it.iconName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
							}) {
								I(FontAwesomeType.BRAND, it.iconName)
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
	const val FOOTER_COLOR = "#1a1120"
	const val FOOTER_DARK_COLOR = "#150d1a"
	const val FOOTER_LINK_HOVER = "#b3b3b3"
	const val FOOTER_HEADING_COLOR = "#ffffff"
	const val FOOTER_TEXT_COLOR = "#cccccc"
	const val FOOTER_LINK_COLOR = "#aaaaaa"

	val footerWrapper by style {
		background(linearGradient(180.deg) {
			stop(Color("#302F39"))
			stop(Color("#211C24"), 100.percent)
		})

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		width(100.percent)
	}

	val contactSection by style {
		padding(2.cssRem, 0.px)
		color(Color.white)
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
		flex("1 1 250px")
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
		color(Color(FOOTER_HEADING_COLOR))
		fontSize(1.2.cssRem)
		fontWeight(700)
		marginTop(0.px)
		marginBottom(1.2.cssRem)
		paddingBottom(0.5.cssRem)
		borderBottom {
			width(2.px)
			style(LineStyle.Solid)
			color(Color("#444444"))
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val footerList by style {
		listStyle("none")
		padding(0.px)
		margin(0.px)

		"li" {
			marginBottom(0.8.cssRem)
		}

		"a" {
			color(Color(FOOTER_LINK_COLOR))
			textDecoration("none")
			transitions {
				defaultDuration(0.3.s)
				properties("color")
			}

			hover(self) style {
				color(Color.white)
				textDecoration("underline")
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val footerLink by style {
		color(Color(FOOTER_LINK_COLOR))
		textDecoration("none")
		display(DisplayStyle.InlineBlock)
		marginTop(0.8.cssRem)
		transitions {
			defaultDuration(0.3.s)
			properties("color")
		}

		hover(self) style {
			color(Color.white)
			textDecoration("underline")
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
			properties("color", "transform")
		}

		hover(self) style {
			color(Color.white)
			property("transform", "translateY(-3px)")
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
		textDecoration("none")
		width(fitContent)

		transitions {
			defaultDuration(0.3.s)
			properties("background-color", "transform", "box-shadow")
		}

		hover(self) style {
			backgroundColor(Color("#FFEA7F"))
			property("transform", "translateY(-3px)")
			property("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.2)")
		}
	}

	val contactForm by style {
		"button" {
			backgroundColor(Color("#50435A"))
			marginTop(1.cssRem)
			width(100.percent)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val footerContactInputs by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns(repeat(2, 1.fr))
		gridTemplateRows {
			repeat(4) {
				size(auto)
			}
		}
		gap(1.cssRem)

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
				color(Color.white)
				fontSize(0.9.cssRem)
			}

			list("input", "textarea") style {
				backgroundColor(Color("#252525"))
				borderRadius(.4.cssRem)
				color(Color("#FFFFFF"))
				border {
					color(Color("#444444"))
					style(LineStyle.Solid)
					width(1.px)
				}
				fontFamily("Open Sans", "sans-serif")
				padding(.5.cssRem)
				transitions {
					defaultDuration(0.3.s)
					properties("border-color")
				}

				self + focus style {
					borderColor(Color.white)
					outline("none")
				}
			}

			list("input::placeholder", "textarea::placeholder") style {
				color(Color("#FFFFFF7F"))
			}

			"textarea" style {
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
		textAlign("center")
		fontSize(0.9.cssRem)
		color(Color("#888888"))

		"p" {
			margin(0.px)
		}
	}
}
