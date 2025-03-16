package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AnimationsStyle
import io.github.ayfri.AppStyle
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.markdownParagraph
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun Experience(
	text: String,
	animationDelay: CSSSizeValue<out CSSUnitTime> = 0.s
) {
	Section({
		classes(ExperiencesStyle.experience)
		style {
			animation(AnimationsStyle.appearFromBelow) {
				duration(0.8.s)
				timingFunction(AnimationTimingFunction.EaseInOut)
				fillMode(AnimationFillMode.Forwards)
				delay(animationDelay)
			}
			opacity(0)
		}
	}) {
		P({
			markdownParagraph(text)
		})
	}
}

@Page("/experiences/index")
@Composable
fun Experiences() {
	PageLayout("Experiences") {

		Style(ExperiencesStyle)

		Div({
			classes(AppStyle.monoFont, AppStyle.sections, ExperiencesStyle.experiences)
		}) {
			H1({
				classes(AppStyle.title, ExperiencesStyle.experiencesTitle)
				style {
					animation(AnimationsStyle.appearFromBelow) {
						duration(0.6.s)
						timingFunction(AnimationTimingFunction.EaseInOut)
						fillMode(AnimationFillMode.Forwards)
					}
					opacity(0)
				}
			}) {
				Text("Experiences:")
			}

			Div({
				classes(ExperiencesStyle.experiencesList)
			}) {
				Experience(
					"""
					## [BlueFrog](https://www.bluefrog.fr/) Internship durin summer 2022
					
					[BlueFrog](https://www.bluefrog.fr/) is a company that develops websites mainly in [PHP](https://www.php.net) for individuals, it is only composed of one employee. From June 2022 to August 2022, I had an internship in the company.
					Starting by learning PHP from scratch, while also learning [WordPress](https://wordpress.org) and then creating multiple websites & plugins. It was pretty interesting to learn a lot of new technologies and team working.
					Also taught [Git](https://git-scm.com/) & [GitHub](https://github.com) and a few other technologies to my boss, so we could work together more efficiently.
					
					My boss was very nice and was creating the mock-ups for the websites, I was only integrating the code and the design. We were also often talking about the animations, which lead me to improve my CSS skills a lot.
					The internship was 2 months long with a good environment, it was pretty far from my home, but I adapted easily, thus it was not a big deal.
					
					Maybe it was a bit repetitive because it was only WordPress websites and no big plugins that change the experience.
					
					My days were from 10AM to 6PM with the option to ask for home working if we needed to.
				""".trimIndent(),
					0.1.s
				)

				Experience(
					"""
					## [Ynov](https://www.ynov.com/) Internship during summer 2023
					
					[Ynov](https://www.ynov.com/) is a school teaching computer science, I'm currently learning in the [Data Scientist](https://www.ynov.com/metiers/data-scientist) course and will finish my Master in summer 2026.<br>
					From June 2023 to August 2023, I had an internship organized by the school about AI.<br>
					We've done a lot of searches about [ChatGPT](https://chat.openai.com/), autonomous agents, and expanding context of GPT.
					
					The project is named ScriptGraf, its purpose is to automatically create posts for your company's social media.<br>
					The internship was 2 months long with a good environment, as it was in the school, it was pretty close to my home and in a place I already knew.<br>
					I was working with some friends and other students in their first year.
					
					This was a really great experience, I've learned a lot about AI and how to use it.
				""".trimIndent(),
					0.3.s
				)

				Experience(
					"""
					## [Eliophot](https://www.eliophot.com/en/) – Full-Stack Developer (2023 - Present)
					
					I've been working at [Eliophot](https://www.eliophot.com/en/), a 360° marketing and communications agency, as a Full-Stack Developer since September 2023.
					My role is set to continue until at least September 2026, giving me the chance to fully integrate into the team and contribute to long-term strategic projects.
					
					My responsibilities are quite diverse. Day-to-day, I build and maintain web applications using modern front-end tools like [Svelte](https://svelte.dev/), [Astro](https://astro.build/), and [Tailwind CSS](https://tailwindcss.com/).
					I've created several websites from scratch, including [e-commerce](https://en.wikipedia.org/wiki/E-commerce) platforms and interactive portfolio sites, which required finding creative UI solutions.
					
					A particularly valuable part of this experience has been working on long-term, large-scale projects—something new to me.
					These projects typically span several months and involve collaboration with multiple departments, helping me significantly improve my skills in planning, architecture and teamwork.
					
					Besides regular web development, I've also built customized [WordPress](https://wordpress.org/) plugins, integrating tools like [Advanced Custom Fields](https://www.advancedcustomfields.com/) to enhance client content management systems.
					I've even stepped into [DevOps](https://en.wikipedia.org/wiki/DevOps), setting up deployment pipelines with tools such as [Dokploy](https://docs.dokploy.com/) and [RunDeck](https://www.rundeck.com/).
					This has given me valuable insights into managing the entire software lifecycle.
					
					Another interesting part of my job is technology research and evaluation. I regularly explore and test new technologies and tools, helping Eliophot stay up-to-date and make smart tech decisions.
					
					Working in a larger team (around 25 people) has greatly improved my communication and teamwork skills.
					I've learned how to independently manage my projects, prioritize tasks more effectively, and troubleshoot complex legacy code—skills extremely useful in any development role.
					
					The atmosphere at Eliophot is fantastic, with supportive colleagues and a culture that encourages innovation.
					I've formed meaningful relationships across multiple teams, gaining a deeper understanding of how my work aligns with broader business goals.
					Facing new challenges daily keeps me motivated, continuously learning, and makes Eliophot a great place for my professional growth.
				""".trimIndent(),
					0.5.s
				)
			}
		}
	}
}

object ExperiencesStyle : StyleSheet() {
	const val EXPERIENCES_BACKGROUND_COLOR = "#363636"
	const val EXPERIENCE_BACKGROUND_COLOR = "#1E1E1E"
	const val EXPERIENCE_BORDER_COLOR = "#444444"
	const val EXPERIENCE_SHADOW_COLOR = "rgba(0, 0, 0, 0.3)"

	val experiences by style {
		backgroundColor(Color(EXPERIENCES_BACKGROUND_COLOR))
	}

	val experiencesTitle by style {
		marginBottom(3.5.cssRem)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				marginBottom(2.5.cssRem)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val experiencesList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(3.cssRem)

		"section" {
			backgroundColor(Color(EXPERIENCE_BACKGROUND_COLOR))
			borderRadius(1.cssRem)
			padding(1.cssRem, 2.cssRem)
			border {
				width(1.px)
				style(LineStyle.Solid)
				color(Color(EXPERIENCE_BORDER_COLOR))
			}

			transitions {
				properties("transform") {
					duration(0.3.s)
					timingFunction(TransitionTimingFunction.EaseInOut)
				}
			}

			self + hover style {
				transform {
					translateY((-5).px)
				}
			}

			media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
				self {
					padding(.8.cssRem, 1.2.cssRem)
				}
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val experience by style {
		lineHeight(1.7.cssRem)

		"h2" {
			marginTop(0.px)
			fontSize(1.5.cssRem)

			media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
				self {
					fontSize(1.3.cssRem)
				}
			}
		}

		"a" {
			color(Color(AppStyle.LINK_COLOR))
			transitions {
				properties("color") {
					duration(0.2.s)
					timingFunction(TransitionTimingFunction.EaseInOut)
				}
			}

			hover {
				color(Color(AppStyle.LINK_HOVER_COLOR))
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				fontSize(.9.cssRem)
				lineHeight(1.5.cssRem)
			}
		}
	}
}
