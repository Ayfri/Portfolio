package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.AppStyle
import io.github.ayfri.components.Dialog
import io.github.ayfri.components.GitHubIcon
import io.github.ayfri.components.Icon
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.localImage
import io.github.ayfri.markdownParagraph
import io.github.ayfri.tint
import io.github.ayfri.utils.gradientBorderBackground
import io.github.ayfri.utils.revealOnScroll
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

data class PortfolioStat(val label: String, val value: String, val icon: Icon)

val portfolioStats = listOf(
	PortfolioStat("Started", "June 2022") { LucideSprout() },
	PortfolioStat("Language", "100% Kotlin") { LucideCode() },
	PortfolioStat("Framework", "Kobweb + Compose HTML") { LucideLayers() },
	PortfolioStat("Hosting", "Cloudflare Pages") { LucideCloud() },
)

data class PortfolioSection(val title: String, val icon: Icon, val text: String, val image: String)

val portfolioSections = listOf(
	PortfolioSection(
		title = "Why I Built It",
		icon = { LucideLightbulb() },
		text = """
			My school asked for a portfolio to validate the first year, but I wanted more than a template someone could fill in a weekend.
			Something that could explain what I do, how, and why, beyond a bare GitHub profile.
		""".trimIndent(),
		image = localImage("portfolio-1.png"),
	),
	PortfolioSection(
		title = "Designing It",
		icon = { LucidePalette() },
		text = """
			The early visual identity started as mockups in Figma, with my internship tutor pushing me on layout and color while I focused on the code.
			These days the design lives directly in Compose HTML: styling and layout are iterated on in Kotlin itself, no separate design tool.
		""".trimIndent(),
		image = localImage("portfolio-2.png"),
	),
	PortfolioSection(
		title = "Building It",
		icon = { LucideHammer() },
		text = """
			Everything is Kotlin: pages, styling, and the build script itself, compiled to JavaScript through [Kobweb](https://kobweb.varabyte.com/) and Compose HTML.
			One typed language for the whole site means the compiler catches broken links and typo'd CSS colors before they ever ship.
		""".trimIndent(),
		image = localImage("portfolio-3.png"),
	),
	PortfolioSection(
		title = "Shipping It",
		icon = { LucideRocket() },
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
			Section({
				classes(PortfolioStyle.intro)
			}) {
				H1({
					classes(AppStyle.title)
				}) {
					Text("This portfolio")
				}

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
						LucideArrowRight()
					}

					A("https://github.com/Ayfri/Portfolio", {
						classes(AppStyle.button)
						target(ATarget.Blank)
					}) {
						GitHubIcon()
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
						stat.icon()
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
						val dialogId = "portfolio-image-$index"
						val alt = "${section.title} screenshot"

						Button({
							classes(PortfolioStyle.featureImage)
							attr("aria-label", "Enlarge the ${section.title.lowercase()} screenshot")
							attr("command", "show-modal")
							attr("commandfor", dialogId)
						}) {
							Img(section.image, alt) { attr("loading", "lazy") }
							LucideExpand()
						}

						Dialog({
							classes(PortfolioStyle.lightbox)
							id(dialogId)
							attr("closedby", "any")
						}) {
							Button({
								attr("aria-label", "Close")
								attr("command", "close")
								attr("commandfor", dialogId)
							}) {
								Img(section.image, alt)
							}
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
				IconHeading({ LucideLayers() }, "Built with")

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

		"h1" {
			margin(1.cssRem, 0.px, 0.px)
		}

		"p" {
			lineHeight(1.6.cssRem)
			fontSize(1.05.cssRem)
			margin(0.px)
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

		"svg" {
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

	val lightboxIn by keyframes {
		from {
			opacity(0)
			property("scale", ".94")
		}
	}

	/** Thumbnail button opening its [lightbox], the expand icon only shows up on hover or keyboard focus. */
	val featureImage by style {
		backgroundColor(Color.transparent)
		cursor(Cursor.ZoomIn)
		display(DisplayStyle.Block)
		flex(3)
		minWidth(0.px)
		padding(0.px)
		position(Position.Relative)
		width(100.percent)

		"img" {
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_STRONG_COLOR))
			borderRadius(.6.cssRem)
			boxSizing(BoxSizing.BorderBox)
			display(DisplayStyle.Block)
			height(auto)
			width(100.percent)

			transitions {
				properties("border-color", "box-shadow") {
					duration(.25.s)
				}
			}
		}

		"svg" {
			backgroundColor(Color(tint(AppStyle.CONTENT_BACKGROUND_COLOR, 80)))
			borderRadius(.4.cssRem)
			color(Color.white)
			fontSize(.85.cssRem)
			opacity(0)
			padding(.5.cssRem)
			position(Position.Absolute)
			property("right", ".7rem")
			property("top", ".7rem")

			transitions {
				properties("opacity") {
					duration(.25.s)
				}
			}
		}

		group(desc(hover(self), "img"), desc(self + focusVisible, "img")) style {
			borderColor(Color(tint(AppStyle.VIOLET_COLOR, 70)))
			boxShadow("0 14px 40px -14px ${tint(AppStyle.VIOLET_COLOR, 50)}")
		}

		group(desc(hover(self), "svg"), desc(self + focusVisible, "svg")) style {
			opacity(1)
		}
	}

	/** Modal `dialog` opened through invoker commands, closed by Escape or a click anywhere thanks to `closedby="any"`. */
	val lightbox by style {
		backgroundColor(Color.transparent)
		borderStyle(LineStyle.None)
		property("animation", "${lightboxIn.name} .25s cubic-bezier(.2, .8, .2, 1)")
		maxHeight(100.vh)
		maxWidth(100.vw)
		overflow(Overflow.Visible)
		padding(0.px)

		self + selector("::backdrop") style {
			backgroundColor(Color(tint(AppStyle.CONTENT_BACKGROUND_COLOR, 85)))
			property("backdrop-filter", "blur(6px)")
		}

		"button" {
			backgroundColor(Color.transparent)
			cursor(Cursor.ZoomOut)
			display(DisplayStyle.Block)
			padding(0.px)
		}

		"img" {
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_STRONG_COLOR))
			borderRadius(.8.cssRem)
			boxShadow("0 30px 90px -20px ${tint(AppStyle.VIOLET_COLOR, 45)}")
			display(DisplayStyle.Block)
			maxHeight(90.vh)
			maxWidth(94.vw)
		}
	}

	val featureText by style {
		flex(2)

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
