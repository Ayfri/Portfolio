package pages

import androidx.compose.runtime.Composable
import markdownParagraph
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import style.AppStyle

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
			Section {
				P({
					markdownParagraph(
						"""
							## [BlueFrog](https://www.bluefrog.fr/) Internship in 2022
							
							[BlueFrog](https://www.bluefrog.fr/) is a company that develops websites mainly in [PHP](https://www.php.net) for individuals, it is only composed of one employee. From June 2022 to August 2022, I had an internship in the company.
							Starting by learning PHP from scratch, while also learning [WordPress](https://wordpress.org) and then creating multiple websites & plugins. It was pretty interesting to learn a lot of new technologies and team working.
							Also taught [Git](https://git-scm.com/) & [GitHub](https://github.com) and a few other technologies to my boss, so we could work together more efficiently.
							
							My boss was very nice and was creating the mock-ups for the websites, I was only integrating the code and the design. We were also often talking about the animations which lead me to improve my CSS skills a lot.
							The internship was 2 months long with good environment, it was pretty far from my home, but I adapt easily thus it wasn't a big deal.
							
							Maybe it was a bit repetitive because it was only WordPress websites and no big plugins that change the experience.
							
							My days were from 10am to 6pm with the possibility to ask for home working if we needed to.
						""".trimIndent()
					)
				})
			}
		}
	}
}

object ExperiencesStyle : StyleSheet() {
	const val experiencesBackgroundColor = "#363636"
	const val experienceBackgroundColor = "#1E1E1E"
	
	val experiences by style {
		backgroundColor(Color(experiencesBackgroundColor))
	}
	
	val experiencesList by style {
		display(DisplayStyle.Flex)
		gap(2.cssRem)
		
		"section" {
			backgroundColor(Color(experienceBackgroundColor))
			borderRadius(.75.cssRem)
			padding(.5.cssRem, 1.5.cssRem)
			
			child(self, type("p")) + firstOfType style {
				margin(0.cssRem)
			}
			
			"p" {
				lineHeight(1.6.cssRem)
			}
			
			media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
				self {
					padding(.5.cssRem, .8.cssRem)
				}
				
				"p" {
					fontSize(.9.cssRem)
					lineHeight(1.4.cssRem)
				}
			}
		}
	}
}