package io.github.ayfri.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.AppStyle
import io.github.ayfri.MAIL_TO
import io.github.ayfri.data.*
import io.github.ayfri.ifNotBlank
import io.github.ayfri.pages.IconHeading
import io.github.ayfri.utils.n
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import kotlin.js.Date

data class FooterSocial(val name: String, val icon: Icon, val url: String)

val footerSocials = listOf(
	FooterSocial("Discord", { DiscordIcon() }, DISCORD_LINK),
	FooterSocial("GitHub", { GitHubIcon() }, REPO_LINK),
	FooterSocial("LinkedIn", { LinkedInIcon() }, LINKEDIN_LINK),
	FooterSocial("Twitch", { TwitchIcon() }, TWITCH_LINK),
	FooterSocial("X", { XIcon() }, TWITTER_LINK),
)

data class ProjectLink(val name: String, val url: String)

val projectLinks = listOf(
	ProjectLink("Kore", "https://kore.ayfri.com"),
	ProjectLink("Atom Clicker", "https://atom-clicker.ayfri.com"),
	ProjectLink("PokeCards-Collector", "https://pokecards-collector.ayfri.com"),
	ProjectLink("Cursors Draw", "https://cursors-draw.ayfri.com"),
	ProjectLink("Realtime TodoList", "https://realtime-todolist.pages.dev"),
	ProjectLink("GPT Images", "https://gpt-images.ayfri.com"),
	ProjectLink("A* Follow", "https://afollow.ayfri.com"),
	ProjectLink("Shayders", "https://shayders.ayfri.com"),
)

data class PageLink(val name: String, val url: String, val icon: Icon, val download: String? = null)

val footerPages = listOf(
	PageLink("Home", "/", { LucideHouse() }),
	PageLink("About me", "/about-me/", { LucideUser() }),
	PageLink("Skills", "/skills/", { LucideCode() }),
	PageLink("Experiences", "/experiences/", { LucideBriefcase() }),
	PageLink("Articles", "/articles/", { LucideNewspaper() }),
	PageLink("This portfolio", "/portfolio/", { LucideFolderOpen() }),
	PageLink("CV (English, PDF)", "/cv-en.pdf", { LucideFileDown() }, "CV Pierre Roy EN.pdf"),
	PageLink("CV (French, PDF)", "/cv.pdf", { LucideFileDown() }, "CV Pierre Roy FR.pdf"),
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

		val attrs: AttrsScope<*>.() -> Unit = {
			classes(AppStyle.input)
			id(id)
			if (required) attr("required", "")
			range?.let {
				attr("minlength", it.first.toString())
				attr("maxlength", it.last.toString())
			}
			autocomplete?.let { attr("autocomplete", it) }
		}

		if (textArea) TextArea(attrs = attrs) else Input(type, attrs)
	}
}

@Composable
fun FooterColumn(heading: String, icon: Icon, content: @Composable () -> Unit) {
	Div({
		classes(FooterStyle.footerColumn)
	}) {
		H3({
			classes(FooterStyle.footerHeading)
		}) {
			icon()
			Text(heading)
		}

		content()
	}
}

@Composable
fun Footer() {
	Style(FooterStyle)

	Footer({
		classes(FooterStyle.footer)
	}) {
		Div({
			classes(FooterStyle.footerInner)
		}) {
			Div({
				classes(FooterStyle.contact)
			}) {
				IconHeading({ LucideMailOpen() }, "Contact me", FooterStyle.contactHeading)

				P({
					classes(FooterStyle.contactHint)
				}) {
					Text("The form opens your mail app with everything already filled in.")
				}

				Div({
					classes(FooterStyle.contactInputs)
				}) {
					FooterContactField(label = "First name", id = "first-name", range = 1..20, autocomplete = "given-name")
					FooterContactField(label = "Last name", id = "last-name", range = 1..32, autocomplete = "family-name")
					FooterContactField(label = "Subject", id = "subject", required = true, range = 5..96, autocomplete = "off")
					FooterContactField(label = "Message", id = "message", textArea = true, required = true, range = 16..512, autocomplete = "off")
				}

				Button({
					classes(AppStyle.button, AppStyle.primaryButton, FooterStyle.sendButton)

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
					LucideSend()
					Text("Send message")
				}
			}

			Div({
				classes(FooterStyle.columns)
			}) {
				FooterColumn("Projects", { LucideRocket() }) {
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

						Li {
							A("/projects/", {
								classes(FooterStyle.moreLink)
							}) {
								Text("All projects")
								LucideArrowRight()
							}
						}
					}
				}

				FooterColumn("Pages", { LucideCompass() }) {
					Ul({
						classes(FooterStyle.footerList)
					}) {
						footerPages.forEach { page ->
							Li {
								A(page.url, {
									page.download?.let {
										target(ATarget.Blank)
										attr("download", it)
									}
								}) {
									page.icon()
									Text(page.name)
								}
							}
						}
					}
				}

				FooterColumn("Elsewhere", { LucideSatelliteDish() }) {
					Div({
						classes(FooterStyle.socialLinks)
					}) {
						footerSocials.forEach { footerSocial ->
							A(footerSocial.url, {
								classes(FooterStyle.socialIcon)
								target(ATarget.Blank)
								title(footerSocial.name)
								attr("aria-label", footerSocial.name)
							}) {
								footerSocial.icon()
							}
						}
					}

					A("https://buymeacoffee.com/ayfri", {
						classes(AppStyle.button, FooterStyle.coffeeLink)
						target(ATarget.Blank)
						title("Support me on Buy Me A Coffee")
					}) {
						LucideCoffee()
						Text("Buy me a coffee")
					}

					A(GOOGLE_SOURCE_LINK, {
						classes(FooterStyle.sourceLink)
						target(ATarget.Blank)
						attr("rel", "noopener noreferrer")
					}) {
						LucideStar()
						Text("Add as a preferred source on Google")
					}
				}
			}
		}

		Div({
			classes(FooterStyle.copyright)
		}) {
			P("© ${Date().getFullYear()} Pierre Roy")
			P("Built with Kotlin and Kobweb")
		}
	}
}

object FooterStyle : StyleSheet() {
	val footer by style {
		backgroundColor(Color("#08060F"))
		borderTop(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		marginTop(4.cssRem)
		width(100.percent)
	}

	val footerInner by style {
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Grid)
		gap(4.cssRem)
		gridTemplateColumns("minmax(0, 1fr) minmax(0, 1.25fr)")
		margin(0.px, autoLength)
		maxWidth(1200.px)
		padding(4.cssRem, 2.cssRem, 3.cssRem)
		width(100.percent)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				gap(3.cssRem)
				gridTemplateColumns("minmax(0, 1fr)")
				padding(3.cssRem, 1.25.cssRem, 2.cssRem)
			}
		}
	}

	val contact by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}

	val contactHeading by style {
		color(Color.white)
		fontSize(1.6.cssRem)
		margin(0.px)
	}

	val contactHint by style {
		fontSize(.95.cssRem)
		margin(.4.cssRem, 0.px, 1.5.cssRem)
	}

	val contactInputs by style {
		display(DisplayStyle.Grid)
		gap(.9.cssRem)
		gridTemplateColumns("1fr 1fr")

		child(self, nthChild(3.n)) style {
			gridColumn("1 / -1")
		}

		child(self, nthChild(4.n)) style {
			gridColumn("1 / -1")
		}

		child(self, universal) style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(.35.cssRem)
			minWidth(0.px)

			type("label") style {
				color(Color(AppStyle.TEXT_COLOR))
				fontSize(.9.cssRem)
				fontWeight(600)
			}

			/** Grows with its content from 130px up, instead of a fixed box with its own scrollbar. */
			type("textarea") style {
				property("field-sizing", "content")
				maxHeight(24.cssRem)
				minHeight(130.px)
				resize(Resize.Vertical)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				gridTemplateColumns("1fr")
			}
		}
	}

	val sendButton by style {
		alignSelf("flex-start")
		marginTop(1.2.cssRem)
	}

	val columns by style {
		display(DisplayStyle.Grid)
		gap(2.cssRem)
		gridTemplateColumns("repeat(3, minmax(0, 1fr))")

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				gridTemplateColumns("1fr 1fr")
			}
		}
	}

	val footerColumn by style {
		minWidth(0.px)
	}

	val footerHeading by style {
		alignItems(AlignItems.Center)
		color(Color.white)
		display(DisplayStyle.Flex)
		fontSize(.95.cssRem)
		fontWeight(700)
		gap(.5.cssRem)
		margin(0.px, 0.px, 1.cssRem)

		"svg" {
			color(Color(AppStyle.VIOLET_COLOR))
			fontSize(.8.em)
		}
	}

	val footerList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(.55.cssRem)
		listStyle(ListStyle.None)
		margin(0.px)
		padding(0.px)

		"a" {
			alignItems(AlignItems.Center)
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			display(DisplayStyle.LegacyInlineFlex)
			fontSize(.95.cssRem)
			gap(.55.cssRem)

			hover(self) style {
				color(Color.white)
			}
		}

		"a svg" {
			fontSize(.8.em)
			opacity(.7)
		}
	}

	val moreLink by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.LegacyInlineFlex)
		gap(.4.cssRem)

		"svg" {
			fontSize(.75.cssRem)
		}
	}

	val socialLinks by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.7.cssRem)
	}

	val socialIcon by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontSize(1.4.cssRem)

		hover(self) style {
			color(Color.white)
		}
	}

	val coffeeLink by style {
		fontSize(.9.cssRem)
		marginTop(1.4.cssRem)

		"svg" {
			color(Color("#FFDD11"))
		}
	}

	val sourceLink by style {
		alignItems(AlignItems.Center)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		fontSize(.95.cssRem)
		gap(.55.cssRem)
		marginTop(1.2.cssRem)

		hover(self) style {
			color(Color.white)
		}

		"svg" {
			flexShrink(0)
			fontSize(.8.em)
			opacity(.7)
		}
	}

	val copyright by style {
		borderTop(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(.85.cssRem)
		gap(1.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		margin(0.px, autoLength)
		maxWidth(1200.px)
		padding(1.2.cssRem, 2.cssRem)
		width(100.percent)

		"p" {
			margin(0.px)
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				padding(1.2.cssRem, 1.25.cssRem)
			}
		}
	}
}
