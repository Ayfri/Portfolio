package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AppStyle
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.I
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.localImage
import io.github.ayfri.tint
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

/** One role; [logo] falls back to a briefcase icon when the company has no image. */
data class Job(
	val company: String,
	val url: String,
	val logo: String?,
	val role: String,
	val period: String,
	val kind: String,
	val location: String,
	val summary: String,
	val highlights: List<String>,
	val stack: List<String>,
)

/** Newest first, like the home page timeline. */
val jobs = listOf(
	Job(
		company = "Link2Brain",
		url = "https://www.link2brain.com/",
		logo = localImage("logos/link2brain.webp"),
		role = "Lead Full-Stack & AI Developer",
		period = "Sep 2025 - Sep 2026",
		kind = "Work-study",
		location = "Marseille",
		summary = "A startup building an AI platform that turns a company's own website into ready-to-publish social media content. It grew out of ScriptGraf, the research project I worked on at Ynov in 2023, and I led its development.",
		highlights = listOf(
			"Built Python AI agents with RAG to generate text, images and short videos",
			"Worked on the API and database layer, plus social network integrations to schedule and publish posts",
			"Owned the DevOps side: Docker, Dokploy deployments and the CI pipeline",
			"Led technical decisions, code review and task splitting, organized with GitHub Projects",
		),
		stack = listOf("Python", "RAG", "FastAPI", "MariaDB", "Vue", "Nuxt", "Docker", "Dokploy", "GitHub Actions"),
	),
	Job(
		company = "Eliophot",
		url = "https://www.eliophot.com/en/",
		logo = null,
		role = "Full-Stack Developer",
		period = "Sep 2023 - Sep 2025",
		kind = "Work-study",
		location = "Aix-en-Provence",
		summary = "Two years in a 360° marketing and communications agency of about 25 people, building client websites and internal tools on long-running projects.",
		highlights = listOf(
			"Built and maintained web apps with Svelte, Astro and Tailwind CSS, including e-commerce and portfolio sites",
			"Created custom WordPress plugins with Advanced Custom Fields for client CMS needs",
			"Set up deployment pipelines with Dokploy and RunDeck",
			"Evaluated new tools for the agency and planned parallel client projects in Asana",
		),
		stack = listOf("Svelte", "Astro", "Tailwind CSS", "WordPress", "PHP", "Dokploy"),
	),
	Job(
		company = "Ynov",
		url = "https://www.ynov.com/",
		logo = localImage("ynov-icon.avif"),
		role = "AI Research Intern, ScriptGraf",
		period = "Jun 2023 - Aug 2023",
		kind = "Internship",
		location = "Aix-en-Provence",
		summary = "A school-run AI internship on ScriptGraf, a prototype that writes social media posts for companies. It later became Link2Brain, where I joined two years later.",
		highlights = listOf(
			"Researched ChatGPT, autonomous agents and ways to extend GPT's context",
			"Built the first prototype with friends and first-year students",
			"Followed the Data Scientist track at Ynov, graduating with a Master's degree in 2026",
		),
		stack = listOf("Python", "OpenAI API", "LLM agents"),
	),
	Job(
		company = "BlueFrog",
		url = "https://www.bluefrog.fr/",
		logo = "https://www.bluefrog.fr/images/logo.png",
		role = "Web Developer",
		period = "Jun 2022 - Aug 2022",
		kind = "Internship",
		location = "France",
		summary = "A one-person web agency building PHP and WordPress sites for individuals. I integrated the designs my tutor mocked up.",
		highlights = listOf(
			"Learned PHP and WordPress from scratch, then shipped several sites and plugins",
			"Worked on CSS animations with my tutor, which sharpened my CSS a lot",
			"Introduced Git and GitHub so we could work on the same code",
		),
		stack = listOf("PHP", "WordPress", "CSS", "Git"),
	),
)

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
			classes(AppStyle.sections, ExperiencesStyle.page)
		}) {
			H1({ classes(AppStyle.title) }) { Text("Experience") }

			P({ classes(ExperiencesStyle.intro) }) {
				Text("From WordPress sites at a one-person web agency to leading development at an AI startup, newest first.")
			}

			Ol({
				classes(ExperiencesStyle.timeline)
			}) {
				jobs.forEach { job ->
					Li({
						classes(ExperiencesStyle.entry)
					}) {
						Article({
							classes(AppStyle.card, ExperiencesStyle.job)
						}) {
							Header({
								classes(ExperiencesStyle.header)
							}) {
								Div({ classes(ExperiencesStyle.logo) }) {
									if (job.logo != null) Img(job.logo, "${job.company} logo")
									else I(FontAwesomeType.SOLID, "briefcase")
								}

								Div({ classes(ExperiencesStyle.titles) }) {
									H2 { Text(job.role) }
									A(job.url, {
										target(ATarget.Blank)
									}) {
										Text(job.company)
									}
								}

								Div({
									classes(AppStyle.monoFont, ExperiencesStyle.meta)
								}) {
									Span {
										I(FontAwesomeType.REGULAR, "calendar")
										Text(job.period)
									}
									Span {
										I(FontAwesomeType.SOLID, "location-dot")
										Text("${job.kind} · ${job.location}")
									}
								}
							}

							P({ classes(ExperiencesStyle.summary) }) { Text(job.summary) }

							Ul({
								classes(ExperiencesStyle.highlights)
							}) {
								job.highlights.forEach { Li { Text(it) } }
							}

							Div({
								classes(ExperiencesStyle.stack)
							}) {
								job.stack.forEach { Span({ classes(AppStyle.tag) }) { Text(it) } }
							}
						}
					}
				}
			}
		}
	}
}

object ExperiencesStyle : StyleSheet() {
	private val railWidth = 2.5.cssRem

	val page by style {
		maxWidth(62.cssRem)
	}

	val intro by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontSize(1.15.cssRem)
		margin(0.px, 0.px, 2.5.cssRem)
	}

	/** The rail is the list's own left border, each entry draws its node on it. */
	val timeline by style {
		borderLeft(1.px, LineStyle.Solid, Color(AppStyle.BORDER_STRONG_COLOR))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.75.cssRem)
		listStyle(ListStyle.None)
		margin(0.px, 0.px, 0.px, .5.cssRem)
		padding(0.px, 0.px, 0.px, railWidth)

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				borderLeft(0.px, LineStyle.None, Color.transparent)
				margin(0.px)
				padding(0.px)
			}
		}
	}

	val entry by style {
		position(Position.Relative)

		self + before style {
			backgroundColor(Color(AppStyle.CONTENT_BACKGROUND_COLOR))
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_STRONG_COLOR))
			borderRadius(50.percent)
			boxSizing(BoxSizing.BorderBox)
			content("")
			position(Position.Absolute)
			property("left", "calc(-$railWidth - .4rem - .5px)")
			size(.8.cssRem)
			top(1.9.cssRem)
		}

		self + firstChild + before style {
			backgroundColor(Color(AppStyle.ACCENT_COLOR))
			borderColor(Color(AppStyle.ACCENT_COLOR))
			boxShadow("0 0 12px ${tint(AppStyle.ACCENT_COLOR, 60)}")
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self + before style {
				display(DisplayStyle.None)
			}
		}
	}

	val job by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		padding(1.6.cssRem, 1.8.cssRem)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				padding(1.2.cssRem)
			}
		}
	}

	val header by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.6.cssRem, 1.cssRem)
	}

	val logo by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color("#FFFFFF08"))
		border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		borderRadius(.7.cssRem)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		flexShrink(0)
		fontSize(1.2.cssRem)
		justifyContent(JustifyContent.Center)
		overflow(Overflow.Hidden)
		size(3.cssRem)

		"img" {
			objectFit(ObjectFit.Contain)
			size(2.2.cssRem)
		}
	}

	val titles by style {
		display(DisplayStyle.Flex)
		flex(1)
		flexDirection(FlexDirection.Column)
		gap(.15.cssRem)
		minWidth(12.cssRem)

		"h2" {
			color(Color.white)
			fontSize(1.2.cssRem)
			margin(0.px)
		}

		"a" {
			color(Color(AppStyle.LINK_COLOR))
			fontWeight(600)
			width(Width.FitContent)
		}

		"a:hover" {
			color(Color(AppStyle.LINK_HOVER_COLOR))
		}
	}

	val meta by style {
		alignItems(AlignItems.End)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		fontSize(.8.cssRem)
		gap(.25.cssRem)

		"span" {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			gap(.45.cssRem)
			whiteSpace(WhiteSpace.NoWrap)
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				alignItems(AlignItems.Start)
				width(100.percent)
			}
		}
	}

	val summary by style {
		color(Color(AppStyle.TEXT_COLOR))
		lineHeight(1.65.number)
		margin(0.px)
	}

	/** Small violet diamonds as markers, echoing the logo tube instead of default bullets. */
	val highlights by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(.45.cssRem)
		listStyle(ListStyle.None)
		margin(0.px)
		padding(0.px)

		"li" {
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			paddingLeft(1.3.cssRem)
			position(Position.Relative)
		}

		"li::before" {
			backgroundColor(Color(AppStyle.VIOLET_COLOR))
			content("")
			left(.2.cssRem)
			position(Position.Absolute)
			size(.4.cssRem)
			top(.62.em)
			transform { rotate(45.deg) }
		}
	}

	val stack by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.4.cssRem)
		paddingTop(.2.cssRem)
	}
}
