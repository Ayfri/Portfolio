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
import io.github.ayfri.utils.gradientBorderBackground
import io.github.ayfri.utils.pageBackground
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
	PageLayout(
		"Experiences",
		description = "Professional experience of Pierre Roy (Ayfri): AI and DevOps at Link2Brain, full-stack development at Eliophot, the ScriptGraf research project at Ynov and WordPress work at BlueFrog.",
		keywords = "software engineer experience, full-stack developer, AI developer, Link2Brain, Eliophot, Ynov, BlueFrog, Svelte, Python, DevOps",
	) {
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
					## [BlueFrog](https://www.bluefrog.fr/) Internship during summer 2022

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

					[Ynov](https://www.ynov.com/) is a school teaching computer science. I followed the [Data Scientist](https://www.ynov.com/metiers/data-scientist) course there and graduated with my Master's degree in summer 2026.<br>
					From June 2023 to August 2023, I had an internship organized by the school about AI.<br>
					We've done a lot of research about [ChatGPT](https://chat.openai.com/), autonomous agents, and expanding the context of GPT.

					The project was named ScriptGraf, its purpose is to automatically create posts for a company's social media.<br>
					The internship was 2 months long with a good environment, as it was in the school, it was pretty close to my home and in a place I already knew.<br>
					I was working with some friends and other students in their first year.

					That prototype is now a company: ScriptGraf grew into [Link2Brain](https://www.link2brain.com/), a Marseille startup selling the finished product, and I joined its team two years later.
				""".trimIndent(),
					0.3.s
				)

				Experience(
					"""
					## [Eliophot](https://www.eliophot.com/en/) – Full-Stack Developer (2023 - 2025)

					I worked at [Eliophot](https://www.eliophot.com/en/), a 360° marketing and communications agency, as a Full-Stack Developer from September 2023 to September 2025.
					During my 2-year internship, I had the chance to fully integrate into the team and contribute to long-term strategic projects.

					My responsibilities were quite diverse. Day-to-day, I built and maintained web applications using modern front-end tools like [Svelte](https://svelte.dev/), [Astro](https://astro.build/), and [Tailwind CSS](https://tailwindcss.com/).
					I created several websites from scratch, including [e-commerce](https://en.wikipedia.org/wiki/E-commerce) platforms and interactive portfolio sites, which required finding creative UI solutions.

					A particularly valuable part of this experience was working on long-term, large-scale projects, something new to me at the time.
					These projects spanned several months and involved collaboration with multiple departments, which significantly improved my skills in planning, architecture and teamwork.

					Besides regular web development, I also built customized [WordPress](https://wordpress.org/) plugins, integrating tools like [Advanced Custom Fields](https://www.advancedcustomfields.com/) to enhance client content management systems.
					I even stepped into [DevOps](https://en.wikipedia.org/wiki/DevOps), setting up deployment pipelines with tools such as [Dokploy](https://docs.dokploy.com/) and [RunDeck](https://www.rundeck.com/),
					which gave me valuable insights into managing the entire software lifecycle.

					Another interesting part of the job was technology research and evaluation: regularly exploring and testing new tools, so the agency could stay up-to-date and make smart tech decisions.
					Day-to-day coordination went through [Asana](https://asana.com/), which is where I learned to plan my own workload across several parallel client projects.

					Working in a larger team (around 25 people) greatly improved my communication and teamwork skills.
					I learned how to independently manage my projects, prioritize tasks more effectively, and troubleshoot complex legacy code, all of which are useful in any development role.

					The atmosphere was great, with supportive colleagues and a culture that encouraged innovation.
					I built meaningful relationships across multiple teams and gained a deeper understanding of how development work aligns with broader business goals.
				""".trimIndent(),
					0.5.s
				)

				Experience(
					"""
					## [Link2Brain](https://www.link2brain.com/) – Lead Full-Stack & AI Developer (Since September 2025)

					[Link2Brain](https://www.link2brain.com/) is a Marseille startup building an AI platform that turns a company's own website into ready-to-publish social media content, with no prompt to write.
					It is what ScriptGraf became: the research project I worked on at [Ynov](https://www.ynov.com/) in 2023 shipped as a real product. The service is live with paying clients, and I joined in September 2025 as lead developer of the team building it.

					Most of my work is [Python](https://www.python.org/) on the AI side, where I build agent systems using [RAG](https://en.wikipedia.org/wiki/Retrieval-augmented_generation) and state-of-the-art models to generate text, images and short videos.
					I also work on the API and the database layer, and I build the integrations with the social network APIs so posts can be scheduled and published from a single editorial calendar.

					On the front-end I work with [Vue](https://vuejs.org/) and [Nuxt](https://nuxt.com/), and the [DevOps](https://en.wikipedia.org/wiki/DevOps) side is mine too: [Docker](https://www.docker.com/), [Dokploy](https://docs.dokploy.com/) deployments and the CI pipeline that tests a change before it ships.
					I set up the team's [GitHub Projects](https://github.com/features/issues) boards as well, so issues, branches and releases stay traceable.

					We built the whole platform together, three developers alongside the founder handling product and marketing. I lead the development side, which means technical decisions, code review and splitting the work,
					while still owning a wide slice of the codebase rather than a narrow specialty.

					Taking a prototype I had seen at its very first step and watching it reach real marketing teams is the part I value most, and working across AI, web and infrastructure means I see the whole product rather than a single layer of it.
				""".trimIndent(),
					0.7.s
				)
			}
		}
	}
}

object ExperiencesStyle : StyleSheet() {
	const val EXPERIENCE_BACKGROUND_COLOR = "#1E1E1E"

	val experiences by style {
		pageBackground()
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
			borderRadius(1.cssRem)
			padding(1.cssRem, 2.cssRem)
			border(2.px, LineStyle.Solid, Color.transparent)
			gradientBorderBackground(Color(EXPERIENCE_BACKGROUND_COLOR))
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
		lineHeight(1.5.cssRem)

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
				lineHeight(1.35.cssRem)
			}
		}
	}
}
