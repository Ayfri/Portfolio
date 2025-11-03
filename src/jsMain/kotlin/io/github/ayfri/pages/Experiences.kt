package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BackgroundClip
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.css.backgroundClip
import com.varabyte.kobweb.compose.css.backgroundImage
import com.varabyte.kobweb.compose.css.boxShadow
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AnimationsStyle
import io.github.ayfri.AppStyle
import io.github.ayfri.components.P
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
				classes(AppStyle.title)
				style {
					animation(AnimationsStyle.appearFromBelow) {
						duration(0.6.s)
						timingFunction(AnimationTimingFunction.EaseInOut)
						fillMode(AnimationFillMode.Forwards)
					}
					opacity(0)
				}
			}) {
				Span {
					Text("Experiences:")
				}
			}

            P("My professional journey includes various roles in web development and AI research. Below is a timeline of my work experiences, showcasing my growth and the diverse projects I've contributed to.", ExperiencesStyle.experiencesDescription)

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
					## [Eliophot](https://www.eliophot.com/en/) – Full-Stack Developer (2023 - 2025)

					I've been working at [Eliophot](https://www.eliophot.com/en/), a 360° marketing and communications agency, as a Full-Stack Developer from September 2023 to September 2025.
					During my 2-year internship, I had the chance to fully integrate into the team and contribute to long-term strategic projects.

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

				Experience(
					"""
					## Internship – Full-Stack & AI Developer (Since September 2025)

					Since September 2025, I've been working on a cutting-edge product still in development. This role combines multiple areas of expertise, keeping me engaged and continuously learning.

					My primary focus is [Python](https://www.python.org/) development, particularly in [AI](https://en.wikipedia.org/wiki/Artificial_intelligence) and machine learning applications. Beyond the backend, I also work on web development using modern frameworks and tools.
					Additionally, I handle [DevOps](https://en.wikipedia.org/wiki/DevOps) tasks and infrastructure management, which complements my full-stack responsibilities.

					The diverse nature of this role—spanning AI development, web technologies, and DevOps—provides me with valuable exposure to different aspects of building a complete product.
					Working on something unreleased is exciting, as it comes with unique challenges and the opportunity to shape the product from its early stages.

					I'm continuously learning and adapting to the various technologies and methodologies required, making this an enriching experience for my professional development.
				""".trimIndent(),
					0.7.s
				)
			}
		}
	}
}

object ExperiencesStyle : StyleSheet() {
	const val EXPERIENCES_BACKGROUND_COLOR = "#1A1225"  // Violet sombre cohérent
	const val EXPERIENCE_BACKGROUND_COLOR = "#1E1E1E"

	val experiences by style {
		backgroundColor(Color(EXPERIENCES_BACKGROUND_COLOR))

		// Fond principal avec gradient violet
		backgroundImage(com.varabyte.kobweb.compose.css.functions.linearGradient(180.deg) {
			add(Color("#0A0A0F"), (-3).percent)
			add(Color("#1A1225"), 14.percent)
			add(Color("#2A1B3D"), 65.percent)
			add(Color("#1E1535"), 90.percent)
		})
	}

    val experiencesDescription by style {
        fontSize(1.2.cssRem)
        lineHeight(1.5.number)
        color(Color("#FFFFFF"))
        padding(0.5.cssRem, 0.cssRem)
    }

	@OptIn(ExperimentalComposeWebApi::class)
	val experiencesList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(3.cssRem)

		type("section") style {
			backgroundColor(Color(EXPERIENCE_BACKGROUND_COLOR))
			borderRadius(1.cssRem)
			padding(1.cssRem, 2.cssRem)
			border(2.px, LineStyle.Solid, Color.transparent)
			backgroundImage("""
				linear-gradient(${EXPERIENCE_BACKGROUND_COLOR}, ${EXPERIENCE_BACKGROUND_COLOR}) padding-box,
				linear-gradient(45deg, #00D4FF, #FF0080) border-box
			""")
			boxShadow("0 0 20px rgba(0, 212, 255, 0.1)")

			transitions {
				properties("transform", "box-shadow") {
					duration(0.3.s)
					timingFunction(TransitionTimingFunction.EaseInOut)
				}
			}

			self + hover style {
				transform {
					translateY((-5).px)
				}
				boxShadow("0 0 30px rgba(255, 0, 128, 0.3)")
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

		type("h2") style {
			marginTop(0.px)
			fontSize(1.5.cssRem)

			backgroundClip(BackgroundClip.Text)
			backgroundImage(com.varabyte.kobweb.compose.css.functions.linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
			property("-moz-text-fill-color", "transparent")
			property("-moz-background-clip", "text")

			media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
				self {
					fontSize(1.3.cssRem)
				}
			}
		}

		type("a") style {
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
