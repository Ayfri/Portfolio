package pages

import androidx.compose.runtime.Composable
import markdownParagraph
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import style.AppStyle

@Composable
fun Experience(
	text: String,
) {
	Section({
		classes(ExperiencesStyle.experience)
	}) {
		P({
			markdownParagraph(text)
		})
	}
}

@Composable
fun Experiences() {
	Style(ExperiencesStyle)

	Div({
		classes(AppStyle.monoFont, AppStyle.sections, ExperiencesStyle.experiences)
	}) {
		H1({
			classes(AppStyle.title)
		}) {
			Text("Experiences:")
		}

		Div({
			classes(ExperiencesStyle.experiencesList)
		}) {
			Experience(
				"""
					## [BlueFrog](https://www.bluefrog.fr/) Internship in 2022
					
					[BlueFrog](https://www.bluefrog.fr/) is a company that develops websites mainly in [PHP](https://www.php.net) for individuals, it is only composed of one employee. From June 2022 to August 2022, I had an internship in the company.
					Starting by learning PHP from scratch, while also learning [WordPress](https://wordpress.org) and then creating multiple websites & plugins. It was pretty interesting to learn a lot of new technologies and team working.
					Also taught [Git](https://git-scm.com/) & [GitHub](https://github.com) and a few other technologies to my boss, so we could work together more efficiently.
					
					My boss was very nice and was creating the mock-ups for the websites, I was only integrating the code and the design. We were also often talking about the animations, which lead me to improve my CSS skills a lot.
					The internship was 2 months long with a good environment, it was pretty far from my home, but I adapted easily, thus it was not a big deal.
					
					Maybe it was a bit repetitive because it was only WordPress websites and no big plugins that change the experience.
					
					My days were from 10AM to 6PM with the option to ask for home working if we needed to.
				""".trimIndent()
			)

			Experience(
				"""
					## [Ynov](https://www.ynov.com/) Internship in 2023
					
					[Ynov](https://www.ynov.com/) is a school teaching computer science, I'm currently as of August 2023 in the second year of the [Computer Science](https://www.ynov.com/formations/ecole-informatique/) course.<br>
					From June 2023 to August 2023, I had an internship organized by the school about AI.<br>
					We've done a lot of searches about [ChatGPT](https://chat.openai.com/), autonomous agents and expanding context of GPT.
					
					The project is named ScriptGraf, its purpose is to automatically create posts for your company's social media.<br>
					The internship was 2 months long with a good environment, as it was in the school, it was pretty close to my home and in a place I already knew.<br>
					I was working with some friends and other students in their first year.
					
					This was a really great experience, I've learned a lot about AI and how to use it.
				""".trimIndent()
			)
		}
	}
}

object ExperiencesStyle : StyleSheet() {
	const val EXPERIENCES_BACKGROUND_COLOR = "#363636"
	const val EXPERIENCE_BACKGROUND_COLOR = "#1E1E1E"

	val experiences by style {
		backgroundColor(Color(EXPERIENCES_BACKGROUND_COLOR))
	}

	val experiencesList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(2.cssRem)

		"section" {
			backgroundColor(Color(EXPERIENCE_BACKGROUND_COLOR))
			borderRadius(.75.cssRem)
			padding(.5.cssRem, 1.5.cssRem)

			media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
				self {
					padding(.5.cssRem, .8.cssRem)
				}
			}
		}
	}

	val experience by style {
		lineHeight(1.6.cssRem)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				fontSize(.9.cssRem)
				lineHeight(1.4.cssRem)
			}
		}
	}
}
