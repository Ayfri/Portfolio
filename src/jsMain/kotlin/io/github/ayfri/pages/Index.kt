package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.*
import io.github.ayfri.components.Avatar
import io.github.ayfri.components.GitHubIcon
import io.github.ayfri.components.Icon
import io.github.ayfri.components.LinkedInIcon
import io.github.ayfri.data.DataStyle
import io.github.ayfri.data.FeaturedProjectCard
import io.github.ayfri.data.FeaturedProjectStyle
import io.github.ayfri.data.LINKEDIN_LINK
import io.github.ayfri.data.REPO_LINK
import io.github.ayfri.data.featuredProjects
import io.github.ayfri.data.portfolioData
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.revealOnScroll
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import web.http.FetchPriority
import web.http.high
import kotlin.js.Date

/** Counted from the first Python scripts in 2014, the starting point of the About Me timeline. */
inline val codingYears get() = ((Date.now() - Date("2014-01-01").getTime()) / 1000 / 60 / 60 / 24 / 365).toInt()

const val MAIN_PRESENTATION = """
Hi, I'm Pierre Roy, a full-stack and AI developer based in France, holding a Data Scientist Master's degree from [Ynov Aix](https://www.ynov.com/campus/aix-en-provence).
I build web products with Kotlin, Svelte and TypeScript, ship AI features in Python, and maintain [Kore](https://kore.ayfri.com), the Kotlin library used to generate Minecraft datapacks.
"""

const val EXPERIENCE_SUMMARY = """
Two years as a full-stack developer at [Eliophot](https://www.eliophot.com/en/) building Svelte and Astro platforms, then Python, AI agents and DevOps work at [Link2Brain](https://www.link2brain.com/),
the Marseille startup grown out of the ScriptGraf research project I worked on at [Ynov](https://www.ynov.com/).
"""

/** Condensed version of the Experiences page, newest first. */
data class TimelineEntry(val period: String, val role: String, val company: String)

val timelineEntries = listOf(
	TimelineEntry("2025 - 2026", "Lead Full-Stack & AI Developer", "Link2Brain"),
	TimelineEntry("2023 - 2025", "Full-Stack Developer", "Eliophot"),
	TimelineEntry("Summer 2023", "AI Research, ScriptGraf", "Ynov"),
	TimelineEntry("Summer 2022", "Web Developer", "BlueFrog"),
)

/** `h2` led by a small icon in a violet-tinted square, the site's single icon + heading treatment. */
@Composable
fun IconHeading(icon: Icon, title: String, vararg classes: String) {
	Style(IconStyle)

	H2({
		classes(IconStyle.heading, *classes)
	}) {
		Span({ classes(IconStyle.badge) }) { icon() }
		Text(title)
	}
}

/** Shared icon treatments: [heading] + [badge] for titles, [label] for a muted icon inline before some text. */
object IconStyle : StyleSheet() {
	val heading by style {
		alignItems(AlignItems.Baseline)
		display(DisplayStyle.Flex)
		gap(.7.cssRem)
	}

	val label by style {
		alignItems(AlignItems.Baseline)
		display(DisplayStyle.LegacyInlineFlex)
		gap(.45.cssRem)

		"svg" {
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			fontSize(.85.em)
			opacity(.8)
		}
	}

	val badge by style {
		alignItems(AlignItems.Center)
		alignSelf(AlignSelf.Center)
		backgroundColor(Color(tint(AppStyle.VIOLET_COLOR, 14)))
		border(1.px, LineStyle.Solid, Color(tint(AppStyle.VIOLET_COLOR, 30)))
		borderRadius(.55.cssRem)
		color(Color(AppStyle.VIOLET_COLOR))
		display(DisplayStyle.LegacyInlineFlex)
		flexShrink(0)
		fontSize(.55.em)
		justifyContent(JustifyContent.Center)
		size(1.9.em)
	}
}

@Composable
fun HomeSectionHeader(icon: Icon, title: String, linkLabel: String, link: String) {
	Div({
		classes(HomeStyle.sectionHeader)
	}) {
		IconHeading(icon, title)

		A(link, {
			classes(HomeStyle.sectionLink)
		}) {
			Text(linkLabel)
			LucideArrowRight()
		}
	}
}

@Composable
fun HomeFact(icon: Icon, value: String, label: String) {
	Li {
		icon()
		B { Text(value) }
		Text(label)
	}
}

@Page("/index")
@Composable
fun Home() {
	PageLayout(
		"Home",
		description = "Pierre Roy (Ayfri), a Kotlin and Compose Multiplatform developer building web apps, Minecraft datapacks with Kore, and AI-powered side projects.",
		keywords = "Pierre Roy, Ayfri, Kotlin developer, Compose Multiplatform, Kotlin Multiplatform, Svelte developer, TypeScript developer, software engineer portfolio, Minecraft datapack developer",
	) {
		val portfolio = portfolioData()
		val starsByRepository = portfolio.repos.associate { it.fullName to it.stargazersCount }

		Style(HomeStyle)
		Style(IconStyle)
		Style(DataStyle)
		Style(FeaturedProjectStyle)

		Section({
			classes(HomeStyle.hero)
		}) {
			Div({
				classes(HomeStyle.heroText)
			}) {
				H1({
					classes(HomeStyle.name)
				}) {
					Text("Pierre Roy")
				}

				P({
					classes(HomeStyle.alias)
				}) {
					Text("aka ")
					Span { Text("Ayfri") }
					Text(" · Full-Stack & AI Developer")
				}

				P({
					markdownParagraph(MAIN_PRESENTATION.trimIndent(), false)
					classes(HomeStyle.intro)
				})

				Div({
					classes(HomeStyle.actions)
				}) {
					A("/projects/", {
						classes(AppStyle.button, AppStyle.primaryButton)
					}) {
						LucideRocket()
						Text("See my projects")
					}

					A("/cv-en.pdf", {
						classes(AppStyle.button)
						target(ATarget.Blank)
						attr("download", "CV Pierre Roy EN.pdf")
					}) {
						LucideFileDown()
						Text("Download CV")
					}

					listOf<Triple<String, Icon, String>>(
						Triple("GitHub", { GitHubIcon() }, REPO_LINK),
						Triple("LinkedIn", { LinkedInIcon() }, LINKEDIN_LINK),
						Triple("Email", { LucideMail() }, "mailto:pierre@ayfri.com"),
					).forEach { (label, icon, url) ->
						A(url, {
							classes(HomeStyle.iconLink)
							title(label)
							attr("aria-label", label)
						}) {
							icon()
						}
					}
				}

				Ul({
					classes(AppStyle.monoFont, HomeStyle.facts)
				}) {
					HomeFact({ LucideStar() }, portfolio.repos.sumOf { it.stargazersCount }.toString(), "GitHub stars")
					HomeFact({ LucideGitBranch() }, portfolio.repos.size.toString(), "public repositories")
					HomeFact({ LucideTerminal() }, "$codingYears+", "years of code")
					HomeFact({ LucidePenTool() }, articlesEntries.size.toString(), "articles")
				}
			}

			Div({
				classes(HomeStyle.orbit)
			}) {
				Avatar("Pierre Roy") {
					classes(HomeStyle.avatar)
					fetchPriority(FetchPriority.high)
				}
			}
		}

		Section({
			classes(HomeStyle.section)
		}) {
			HomeSectionHeader({ LucideRocket() }, "Featured projects", "All projects", "/projects/")

			Div({
				classes(FeaturedProjectStyle.grid)
			}) {
				featuredProjects.forEachIndexed { index, project ->
					FeaturedProjectCard(project, starsByRepository[project.repository], hero = index == 0)
				}
			}
		}

		Section({
			classes(HomeStyle.section)
		}) {
			HomeSectionHeader({ LucideWrench() }, "Tools I use most", "All skills", "/skills/")

			Div({
				classes(HomeStyle.skills)
			}) {
				skillsByLevel.take(8).forEach { skill ->
					A("/skills/#${skill.language.name}", {
						classes(HomeStyle.skill)
					}) {
						skill.DisplaySimple()
					}
				}
			}
		}

		Section({
			classes(HomeStyle.section, HomeStyle.split)
		}) {
			Div {
				HomeSectionHeader({ LucideBriefcase() }, "Experience", "Details", "/experiences/")

				P({
					markdownParagraph(EXPERIENCE_SUMMARY.trimIndent(), false)
					classes(HomeStyle.muted)
				})
			}

			Ol({
				classes(HomeStyle.timeline)
			}) {
				timelineEntries.forEach { entry ->
					Li {
						Span({
							classes(AppStyle.monoFont, HomeStyle.timelinePeriod)
						}) {
							Text(entry.period)
						}

						Span({
							classes(HomeStyle.timelineRole)
						}) {
							Text(entry.role)
						}

						Span({
							classes(HomeStyle.muted, IconStyle.label)
						}) {
							LucideBuilding2()
							Text(entry.company)
						}
					}
				}
			}
		}

		Section({
			classes(HomeStyle.section)
		}) {
			HomeSectionHeader({ LucideNewspaper() }, "Latest articles", "All articles", "/articles/")

			Ul({
				classes(HomeStyle.articles)
			}) {
				articlesEntries.sortedByDescending { it.date }.take(3).forEach { article ->
					Li {
						A(article.path, {
							classes(HomeStyle.article)
						}) {
							Span({
								classes(AppStyle.monoFont, HomeStyle.articleDate, IconStyle.label)
							}) {
								LucideCalendar()
								Text(formatLongDate(article.date))
							}

							Div {
								H3 { Text(article.navTitle) }
								P({
									classes(HomeStyle.muted)
								}) {
									Text(article.desc.withoutMarkdownLinks())
								}
							}
						}
					}
				}
			}
		}

		Section({
			classes(HomeStyle.section, HomeStyle.split)
		}) {
			Div({
				classes(HomeStyle.more)
			}) {
				IconHeading({ LucideBookOpen() }, "The long version")
				P({
					classes(HomeStyle.muted)
				}) {
					Text("From my first Python scripts in 2014 to Kotlin, Minecraft datapacks and AI work: the whole story, project by project.")
				}
				A("/about-me/", {
					classes(AppStyle.button)
				}) {
					Text("Read my story")
					LucideArrowRight()
				}
			}

			Div({
				classes(HomeStyle.more)
			}) {
				IconHeading({ LucideWandSparkles() }, "How this site is built")
				P({
					classes(HomeStyle.muted)
				}) {
					Text("Written entirely in Kotlin with Kobweb and Compose HTML, statically exported, with GitHub data fetched at build time.")
				}
				A("/portfolio/", {
					classes(AppStyle.button)
				}) {
					Text("About this portfolio")
					LucideArrowRight()
				}
			}
		}
	}
}

object HomeStyle : StyleSheet() {
	val fadeInUp by keyframes {
		from {
			opacity(0)
			property("translate", "0 .9rem")
			property("filter", "blur(6px)")
		}
	}

	val orbitIn by keyframes {
		from {
			opacity(0)
			property("scale", ".82")
			property("filter", "blur(10px)")
		}
	}

	val spin by keyframes {
		to {
			transform { rotate(360.deg) }
		}
	}

	val shimmer by keyframes {
		to {
			property("background-position", "200% 0")
		}
	}

	val lineGrow by keyframes {
		from {
			property("scale", "0 1")
		}
	}

	init {
		id("main") style {
			alignItems(AlignItems.Center)
			boxSizing(BoxSizing.BorderBox)
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			padding(0.px, 5.vw, 2.cssRem)
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			id("main") style {
				paddingLeft(4.vw)
				paddingRight(4.vw)
			}
		}
	}

	val hero by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Grid)
		gap(3.cssRem)
		gridTemplateColumns("minmax(0, 1.5fr) minmax(0, 1fr)")
		maxWidth(1200.px)
		padding(7.cssRem, 0.px, 2.cssRem)
		width(100.percent)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				gap(2.cssRem)
				gridTemplateColumns("minmax(0, 1fr)")
				padding(2.cssRem, 0.px, 0.px)
			}
		}
	}

	/** Each hero line blurs in after the previous one, 80ms apart. */
	val heroText by style {
		(1..6).forEach { index ->
			"> :nth-child($index)" {
				property("animation", "${fadeInUp.name} .7s cubic-bezier(.2, .8, .2, 1) ${index * 80}ms both")
			}
		}
	}

	val name by style {
		property("view-transition-name", "page-title")
		property("width", "fit-content")
		color(Color.white)
		fontSize(4.2.cssRem)
		letterSpacing((-0.04).em)
		margin(0.px)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				fontSize(2.8.cssRem)
			}
		}
	}

	val alias by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontFamily(AppStyle.MONO_FONT_FAMILY, "monospace")
		fontSize(1.05.cssRem)
		margin(.6.cssRem, 0.px, 1.8.cssRem)

		/** The alias carries the logo gradient, the only gradient text of the site, slowly flowing through it. */
		"span" {
			property(
				"background",
				"linear-gradient(90deg, ${AppStyle.ACCENT_COLOR}, ${AppStyle.VIOLET_COLOR}, ${AppStyle.ACCENT_SECONDARY_COLOR}, ${AppStyle.VIOLET_COLOR}, ${AppStyle.ACCENT_COLOR}) 0 0 / 200% 100%"
			)
			property("background-clip", "text")
			property("animation", "${shimmer.name} 8s linear infinite")
			color(Color.transparent)
			fontWeight(700)
		}
	}

	val intro by style {
		color(Color(AppStyle.TEXT_COLOR))
		fontSize(1.2.cssRem)
		lineHeight(1.65.number)
		margin(0.px)
		maxWidth(40.cssRem)
	}

	val actions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.75.cssRem)
		marginTop(2.cssRem)
	}

	val iconLink by style {
		alignItems(AlignItems.Center)
		borderRadius(.5.cssRem)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		fontSize(1.3.cssRem)
		justifyContent(JustifyContent.Center)
		size(2.6.cssRem)

		hover(self) style {
			backgroundColor(Color("#FFFFFF0F"))
			color(Color.white)
		}
	}

	val facts by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(.85.cssRem)
		gap(.4.cssRem, 1.6.cssRem)
		listStyle(ListStyle.None)
		margin(2.4.cssRem, 0.px, 0.px)
		padding(0.px)

		"li" {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			gap(.45.cssRem)
		}

		"svg" {
			color(Color(AppStyle.VIOLET_COLOR))
			fontSize(.8.cssRem)
		}

		"b" {
			color(Color.white)
		}
	}

	/**
	 * Avatar inside two orbit rings, each carrying a satellite in a logo color, turning in opposite directions.
	 * The whole system blurs in after the hero text, and the rotations stop for visitors asking for reduced motion.
	 */
	val orbit by style {
		alignItems(AlignItems.Center)
		property("animation", "${orbitIn.name} 1.1s cubic-bezier(.2, .8, .2, 1) .25s both")
		property("aspect-ratio", "1")
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
		property("justify-self", "center")
		position(Position.Relative)
		property("width", "min(100%, 22rem)")

		group(self + before, self + after) style {
			borderRadius(50.percent)
			content("")
			position(Position.Absolute)
		}

		self + before style {
			border(1.px, LineStyle.Dashed, Color("#FFFFFF1A"))
			property("inset", "9%")
			property(
				"background",
				"radial-gradient(circle at 50% 100%, ${AppStyle.ACCENT_SECONDARY_COLOR} 0 2px, ${tint(AppStyle.ACCENT_SECONDARY_COLOR, 40)} 3px, transparent 7px) no-repeat"
			)
			property("animation", "${spin.name} 26s linear infinite reverse")
		}

		self + after style {
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_STRONG_COLOR))
			property("inset", "0")
			property(
				"background",
				"radial-gradient(circle at 50% 0%, ${AppStyle.ACCENT_COLOR} 0 3px, ${tint(AppStyle.ACCENT_COLOR, 40)} 4px, transparent 9px) no-repeat"
			)
			property("animation", "${spin.name} 40s linear infinite")
		}

		media(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("reduce"))) {
			group(self + before, self + after) style {
				property("animation", "none")
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				property("grid-row", "1")
				property("justify-self", "start")
				property("width", "11rem")
			}
		}
	}

	val avatar by style {
		borderRadius(50.percent)
		boxShadow("0 0 0 1px ${AppStyle.BORDER_STRONG_COLOR}, 0 20px 70px -15px ${tint(AppStyle.VIOLET_COLOR, 55)}")
		height(68.percent)
		objectFit(ObjectFit.Cover)
		width(68.percent)
	}

	val section by style {
		maxWidth(1200.px)
		paddingTop(5.cssRem)
		width(100.percent)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				paddingTop(3.5.cssRem)
			}
		}
	}

	/** The hairline under each section title draws itself in the logo gradient as the section scrolls in. */
	val sectionHeader by style {
		alignItems(AlignItems.Baseline)
		borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		display(DisplayStyle.Flex)
		gap(1.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		marginBottom(1.8.cssRem)
		paddingBottom(.9.cssRem)
		position(Position.Relative)
		revealOnScroll()

		self + after style {
			content("")
			bottom((-1).px)
			height(1.px)
			left(0.px)
			position(Position.Absolute)
			property("background", "linear-gradient(90deg, ${AppStyle.ACCENT_COLOR}, ${AppStyle.VIOLET_COLOR} 35%, transparent 70%)")
			property("transform-origin", "left")
			property("animation", "${lineGrow.name} linear both")
			property("animation-timeline", "view()")
			property("animation-range", "entry 20% cover 35%")
			width(100.percent)
		}

		"h2" {
			color(Color.white)
			fontSize(1.6.cssRem)
			margin(0.px)
		}
	}

	val sectionLink by style {
		alignItems(AlignItems.Center)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		fontSize(.95.cssRem)
		gap(.45.cssRem)
		whiteSpace(WhiteSpace.NoWrap)

		"svg" {
			fontSize(.75.cssRem)
			transitions {
				properties("transform") { duration(.2.s) }
			}
		}

		hover(self) style {
			color(Color(AppStyle.ACCENT_COLOR))
		}

		desc(hover(self), "svg") style {
			transform { translateX(3.px) }
		}
	}

	val muted by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
	}

	val skills by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.75.cssRem)
	}

	val skill by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color(AppStyle.CARD_BACKGROUND))
		border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		borderRadius(.6.cssRem)
		color(Color(AppStyle.TEXT_COLOR))
		display(DisplayStyle.Flex)
		gap(.7.cssRem)
		padding(.55.cssRem, 1.cssRem, .55.cssRem, .6.cssRem)
		revealOnScroll()

		transitions {
			properties("border-color", "box-shadow", "translate") { duration(.2.s) }
		}

		hover(self) style {
			borderColor(Color(tint(AppStyle.VIOLET_COLOR, 70)))
			boxShadow("0 8px 24px -12px ${tint(AppStyle.VIOLET_COLOR, 70)}")
			property("translate", "0 -2px")
		}

		"img" {
			borderRadius(.35.cssRem)
			size(2.cssRem)
		}

		"p" {
			fontSize(.95.cssRem)
			margin(0.px)
		}
	}

	val split by style {
		alignItems(AlignItems.Start)
		display(DisplayStyle.Grid)
		gap(3.cssRem)
		gridTemplateColumns("minmax(0, 1fr) minmax(0, 1fr)")

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				gap(2.cssRem)
				gridTemplateColumns("minmax(0, 1fr)")
			}
		}
	}

	val timeline by style {
		listStyle(ListStyle.None)
		margin(0.px)
		padding(0.px)

		"li" {
			alignItems(AlignItems.Baseline)
			borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			display(DisplayStyle.Grid)
			gap(.2.cssRem, 1.2.cssRem)
			gridTemplateColumns("8rem minmax(0, 1fr) auto")
			padding(1.cssRem, 0.px)
			revealOnScroll()
		}

		"li:first-child" {
			paddingTop(0.px)
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			"li" {
				gridTemplateColumns("minmax(0, 1fr) auto")
			}

			"li > :first-child" {
				gridColumn("1 / -1")
			}
		}
	}

	val timelinePeriod by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontSize(.85.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val timelineRole by style {
		color(Color.white)
		fontWeight(600)
	}

	val articles by style {
		listStyle(ListStyle.None)
		margin(0.px)
		padding(0.px)
	}

	val article by style {
		borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		display(DisplayStyle.Grid)
		gap(.3.cssRem, 2.cssRem)
		gridTemplateColumns("11rem minmax(0, 1fr)")
		padding(1.3.cssRem, 0.px)
		revealOnScroll()

		"h3" {
			color(Color.white)
			fontSize(1.15.cssRem)
			margin(0.px, 0.px, .35.cssRem)
			transitions {
				properties("color") { duration(.2.s) }
			}
		}

		"p" {
			fontSize(1.cssRem)
			margin(0.px)
		}

		desc(hover(self), "h3") style {
			color(Color(AppStyle.ACCENT_COLOR))
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				gridTemplateColumns("minmax(0, 1fr)")
			}
		}
	}

	val articleDate by style {
		alignSelf("start")
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontSize(.85.cssRem)
		paddingTop(.2.cssRem)
	}

	val more by style {
		backgroundColor(Color(AppStyle.CARD_BACKGROUND))
		border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		borderRadius(.9.cssRem)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Start)
		height(100.percent)
		boxSizing(BoxSizing.BorderBox)
		padding(1.8.cssRem)
		revealOnScroll()

		"h2" {
			color(Color.white)
			fontSize(1.3.cssRem)
			margin(0.px)
		}

		"a svg" {
			fontSize(.8.em)
		}

		"p" {
			flexGrow(1)
			margin(.7.cssRem, 0.px, 1.4.cssRem)
		}
	}
}
