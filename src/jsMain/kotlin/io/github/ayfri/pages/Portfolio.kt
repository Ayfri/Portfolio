package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AppStyle
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.I
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.localImage
import io.github.ayfri.markdownParagraph
import io.github.ayfri.utils.gradientBorderBackground
import io.github.ayfri.utils.revealOnScroll
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

data class PortfolioStat(val label: String, val value: String, val icon: String)

val portfolioStats = listOf(
	PortfolioStat("Started", "June 2022", "seedling"),
	PortfolioStat("Language", "100% Kotlin", "code"),
	PortfolioStat("Framework", "Kobweb + Compose HTML", "layer-group"),
	PortfolioStat("Hosting", "Cloudflare Pages", "cloud"),
)

data class PortfolioSection(val title: String, val icon: String, val text: String, val image: String)

val portfolioSections = listOf(
	PortfolioSection(
		title = "Why I Built It",
		icon = "lightbulb",
		text = """
			My school asked for a portfolio to validate the first year, but I wanted more than a template someone could fill in a weekend.
			Something that could explain what I do, how, and why, beyond a bare GitHub profile.
		""".trimIndent(),
		image = localImage("portfolio-1.png"),
	),
	PortfolioSection(
		title = "Designing It",
		icon = "palette",
		text = """
			The early visual identity started as mockups in Figma, with my internship tutor pushing me on layout and color while I focused on the code.
			These days the design lives directly in Compose HTML: styling and layout are iterated on in Kotlin itself, no separate design tool.
		""".trimIndent(),
		image = localImage("portfolio-2.png"),
	),
	PortfolioSection(
		title = "Building It",
		icon = "hammer",
		text = """
			Everything is Kotlin: pages, styling, and the build script itself, compiled to JavaScript through [Kobweb](https://kobweb.varabyte.com/) and Compose HTML.
			One typed language for the whole site means the compiler catches broken links and typo'd CSS colors before they ever ship.
		""".trimIndent(),
		image = localImage("portfolio-3.png"),
	),
	PortfolioSection(
		title = "Shipping It",
		icon = "rocket",
		text = """
			The site is statically exported with `kobwebExport`, then deployed to Cloudflare Pages straight from GitHub Actions on every push to master.
			No manual uploads, no dynamic server to maintain, just static files served from the edge.
		""".trimIndent(),
		image = localImage("portfolio-4.png"),
	),
)

val techBadges = listOf("Kotlin 2.4", "Kobweb 0.25", "Compose HTML 1.12", "Gradle 9.8", "GitHub Actions", "Cloudflare Pages")

@Page("/portfolio/index")
@Composable
fun Portfolio() {
	PageLayout(
		"Portfolio",
		description = "How this Kotlin and Compose for Web portfolio was designed and built, from the first school-project prototype in 2022 to the site you're browsing today.",
		keywords = "Kotlin Compose for Web, Kobweb, portfolio website design, Kotlin web development, static site generation",
	) {
		Style(PortfolioStyle)

		Div({
			classes(PortfolioStyle.portfolio)
		}) {
			H1({
				classes(AppStyle.title)
			}) {
				Text("This portfolio")
			}

			Section({
				classes(PortfolioStyle.intro)
			}) {
				P({
					markdownParagraph(
						"""
						I created this portfolio first because my school asked me to, but it turned into a long-running playground I keep coming back to.
						No HTML files, no CSS files, no JavaScript I wrote by hand, every page and every style rule is Kotlin compiled to JavaScript through [Kobweb](https://kobweb.varabyte.com/) and Compose HTML.
					""".trimIndent(), true
					)
				})

				Div({
					classes(PortfolioStyle.introActions)
				}) {
					A("/articles/building-this-portfolio/", {
						classes(AppStyle.button, AppStyle.primaryButton)
					}) {
						Text("Read the full story")
						I(FontAwesomeType.SOLID, "arrow-right")
					}

					A("https://github.com/Ayfri/Portfolio", {
						classes(AppStyle.button)
						target(ATarget.Blank)
					}) {
						I(FontAwesomeType.BRAND, "github")
						Text("View source")
					}
				}
			}

			Section({
				classes(PortfolioStyle.stats)
			}) {
				portfolioStats.forEach { stat ->
					Div({
						classes(PortfolioStyle.statTile)
					}) {
						I(FontAwesomeType.SOLID, stat.icon)
						Div {
							P({
								classes(PortfolioStyle.statValue)
							}) {
								Text(stat.value)
							}
							P({
								classes(PortfolioStyle.statLabel)
							}) {
								Text(stat.label)
							}
						}
					}
				}
			}

			Div({
				classes(PortfolioStyle.features)
			}) {
				portfolioSections.forEachIndexed { index, section ->
					Div({
						classes(PortfolioStyle.feature)
						if (index % 2 == 0) classes(PortfolioStyle.featureReverse)
					}) {
						Img(section.image, alt = "${section.title} screenshot") {
							classes(PortfolioStyle.featureImage)
						}

						Div({
							classes(PortfolioStyle.featureText)
						}) {
							IconHeading(section.icon, section.title)

							P({
								markdownParagraph(section.text, true)
							})
						}
					}
				}
			}

			Section({
				classes(PortfolioStyle.techStack)
			}) {
				IconHeading("layer-group", "Built with")

				Div({
					classes(PortfolioStyle.techStackList)
				}) {
					techBadges.forEach { Span({ classes(AppStyle.tag) }) { Text(it) } }
				}
			}

			Section({
				classes(PortfolioStyle.outro)
			}) {
				P({
					markdownParagraph(
						"""
						Want the deep dive, the actual code samples, and why I'd still pick a boring stack for a team project? Read [how this portfolio was built](/articles/building-this-portfolio/).
					""".trimIndent(), true
					)
				})
			}
		}
	}
}

object PortfolioStyle : StyleSheet() {
	val portfolio by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(3.cssRem)

		padding(2.cssRem)

		media(mediaMaxWidth(AppStyle.mobileSecondBreak)) {
			self {
				padding(1.2.cssRem, .8.cssRem)
			}
		}
	}

	// Narrower column for prose (intro/outro paragraphs, tech stack blurb).
	val textWidth = 44.cssRem

	// Wider column for visual blocks (stats grid, image/text feature rows).
	val wideWidth = 68.cssRem

	val intro by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.5.cssRem)
		maxWidth(textWidth)
		textAlign(TextAlign.Center)

		"p" {
			lineHeight(1.6.cssRem)
			fontSize(1.05.cssRem)
		}
	}

	val introActions by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.cssRem)
		flexWrap(FlexWrap.Wrap)
		justifyContent(JustifyContent.Center)
	}

	val stats by style {
		display(DisplayStyle.Grid)
		gap(1.2.cssRem)
		gridTemplateColumns {
			repeat(GridEntry.Repeat.Auto.Type.AutoFit) {
				minmax(13.cssRem, 1.fr)
			}
		}
		maxWidth(wideWidth)
		width(100.percent)
	}

	val statTile by style {
		alignItems(AlignItems.Center)
		border(1.px, LineStyle.Solid, Color.transparent)
		gradientBorderBackground(Color(AppStyle.CARD_BACKGROUND))
		borderRadius(.8.cssRem)
		display(DisplayStyle.Flex)
		gap(1.cssRem)
		padding(1.2.cssRem)
		revealOnScroll()

		group(desc(self, "svg"), desc(self, "i")) style {
			color(Color(AppStyle.VIOLET_COLOR))
			fontSize(1.3.cssRem)
		}

		"p" {
			margin(0.px)
		}
	}

	val statValue by style {
		fontWeight(700)
	}

	val statLabel by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontSize(.85.cssRem)
	}

	val features by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(3.cssRem)
		maxWidth(wideWidth)
		width(100.percent)
	}

	val feature by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(2.5.cssRem)
		revealOnScroll()

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				flexDirection(FlexDirection.Column)
			}
		}
	}

	val featureReverse by style {
		media(mediaMinWidth(AppStyle.mobileFirstBreak + 1.px)) {
			self {
				flexDirection(FlexDirection.RowReverse)
			}
		}
	}

	val featureImage by style {
		border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_STRONG_COLOR))
		borderRadius(.6.cssRem)
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Block)
		flex(1)
		minWidth(0.px)
		height(16.cssRem)
		objectFit(ObjectFit.Cover)
		property("object-position", "top")
		width(100.percent)
	}

	val featureText by style {
		flex(1)

		"h2" {
			color(Color.white)
			fontSize(1.5.cssRem)
			margin(0.px, 0.px, .8.cssRem)
		}

		"p" {
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			margin(0.px)
		}
	}

	val techStack by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.5.cssRem)
		maxWidth(textWidth)
		textAlign(TextAlign.Center)

		"h2" {
			color(Color.white)
			fontSize(1.6.cssRem)
			margin(0.px)
		}
	}

	val techStackList by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.6.cssRem)
		justifyContent(JustifyContent.Center)
	}

	val outro by style {
		maxWidth(textWidth)
		textAlign(TextAlign.Center)

		"p" {
			lineHeight(1.5.cssRem)
		}
	}
}
