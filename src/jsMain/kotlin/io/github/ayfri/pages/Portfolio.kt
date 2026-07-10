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
import io.github.ayfri.utils.pageBackground
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.ExperimentalComposeWebApi
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

data class PortfolioSection(
	val title: String,
	val text: String,
	val image: String,
	val imageOnRight: Boolean,
)

val portfolioSections = listOf(
	PortfolioSection(
		title = "Why I Built It",
		text = """
			My school asked for a portfolio to validate the first year, but I wanted more than a template someone could fill in a weekend.
			Something that could explain what I do, how, and why, beyond a bare GitHub profile.
		""".trimIndent(),
		image = localImage("portfolio-1.png"),
		imageOnRight = true,
	),
	PortfolioSection(
		title = "Designing It",
		text = """
			The early visual identity started as mockups in Figma, with my internship tutor pushing me on layout and color while I focused on the code.
			These days the design lives directly in Compose HTML: styling and layout are iterated on in Kotlin itself, no separate design tool.
		""".trimIndent(),
		image = localImage("portfolio-2.png"),
		imageOnRight = false,
	),
	PortfolioSection(
		title = "Building It",
		text = """
			Everything is Kotlin: pages, styling, and the build script itself, compiled to JavaScript through [Kobweb](https://kobweb.varabyte.com/) and Compose HTML.
			One typed language for the whole site means the compiler catches broken links and typo'd CSS colors before they ever ship.
		""".trimIndent(),
		image = localImage("portfolio-3.png"),
		imageOnRight = true,
	),
	PortfolioSection(
		title = "Shipping It",
		text = """
			The site is statically exported with `kobwebExport`, then deployed to Cloudflare Pages straight from GitHub Actions on every push to master.
			No manual uploads, no dynamic server to maintain, just static files served from the edge.
		""".trimIndent(),
		image = localImage("portfolio-4.png"),
		imageOnRight = false,
	),
)

data class TechBadge(val name: String)

val techBadges = listOf(
	TechBadge("Kotlin 2.4"),
	TechBadge("Kobweb 0.25"),
	TechBadge("Compose HTML 1.11"),
	TechBadge("Gradle 9.6"),
	TechBadge("GitHub Actions"),
	TechBadge("Cloudflare Pages"),
)

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
				Span {
					Text("This Portfolio")
				}
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
						classes(AppStyle.button)
					}) {
						Text("Read the full story")
						I(FontAwesomeType.SOLID, "arrow-right") {
							marginLeft(0.5.cssRem)
						}
					}

					A("https://github.com/Ayfri/Portfolio", {
						classes(PortfolioStyle.secondaryButton)
						target(ATarget.Blank)
					}) {
						I(FontAwesomeType.BRAND, "github") {
							marginRight(0.5.cssRem)
						}
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
				portfolioSections.forEach { section ->
					Div({
						classes(PortfolioStyle.feature)
						if (section.imageOnRight) classes(PortfolioStyle.featureReverse)
					}) {
						Div({
							classes(PortfolioStyle.featureImageFrame)
						}) {
							Div({
								classes(PortfolioStyle.featureImageBar)
							}) {
								Span({ classes(PortfolioStyle.dot, PortfolioStyle.dotRed) })
								Span({ classes(PortfolioStyle.dot, PortfolioStyle.dotYellow) })
								Span({ classes(PortfolioStyle.dot, PortfolioStyle.dotGreen) })
							}

							Img(section.image, alt = "${section.title} screenshot") {
								classes(PortfolioStyle.featureImage)
							}
						}

						Div({
							classes(PortfolioStyle.featureText)
						}) {
							H2 {
								Text(section.title)
							}

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
				H2 {
					Text("Built With")
				}

				Div({
					classes(PortfolioStyle.techStackList)
				}) {
					techBadges.forEach { badge ->
						Span({
							classes(PortfolioStyle.techBadge, AppStyle.monoFont)
						}) {
							Text(badge.name)
						}
					}
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

@OptIn(ExperimentalComposeWebApi::class)
object PortfolioStyle : StyleSheet() {
	val portfolio by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(3.cssRem)

		padding(2.cssRem)

		pageBackground()

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

	val secondaryButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(2.px, LineStyle.Solid, Color("#3A3450"))
		borderRadius(.4.cssRem)
		color(Color.white)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(1.1.cssRem)
		fontWeight(700)
		padding(.65.cssRem, 1.2.cssRem)
		textDecorationLine(TextDecorationLine.None)

		transitions {
			defaultDelay(.25.s)
			properties("border-color", "background-color")
		}

		hover(self) style {
			backgroundColor(Color("#ffffff10"))
			borderColor(Color(AppStyle.BRAND_GRADIENT_FROM))
		}
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
		border(2.px, LineStyle.Solid, Color.transparent)
		gradientBorderBackground(Color(AppStyle.CARD_BACKGROUND))
		borderRadius(.8.cssRem)
		display(DisplayStyle.Flex)
		gap(1.cssRem)
		padding(1.2.cssRem)

		"svg" {
			color(Color("#00D4FF"))
			fontSize(1.5.cssRem)
		}

		"p" {
			margin(0.px)
		}
	}

	val statValue by style {
		fontWeight(700)
	}

	val statLabel by style {
		color(Color("#B0AEC0"))
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

	val featureImageFrame by style {
		backgroundColor(Color("#0F0B17"))
		border(1.px, LineStyle.Solid, Color("#3A3450"))
		borderRadius(.6.cssRem)
		flex(1)
		overflow(Overflow.Hidden)
		width(100.percent)
	}

	val featureImageBar by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color("#1D1730"))
		borderBottom(1.px, LineStyle.Solid, Color("#3A3450"))
		display(DisplayStyle.Flex)
		gap(.4.cssRem)
		padding(.55.cssRem, .8.cssRem)
	}

	val dot by style {
		borderRadius(50.percent)
		size(.6.cssRem)
	}

	val dotRed by style {
		backgroundColor(Color("#FF5F56"))
	}

	val dotYellow by style {
		backgroundColor(Color("#FFBD2E"))
	}

	val dotGreen by style {
		backgroundColor(Color("#27C93F"))
	}

	val featureImage by style {
		display(DisplayStyle.Block)
		height(16.cssRem)
		objectFit(ObjectFit.Cover)
		property("object-position", "top")
		width(100.percent)
	}

	val featureText by style {
		flex(1)

		"h2" {
			backgroundClip(BackgroundClip.Text)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			fontSize(1.6.cssRem)
			margin(0.px, 0.px, .8.cssRem)
			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
			property("-moz-text-fill-color", "transparent")
			property("-moz-background-clip", "text")
		}

		"p" {
			lineHeight(1.5.cssRem)
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
			backgroundClip(BackgroundClip.Text)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			fontSize(1.8.cssRem)
			margin(0.px)
			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
			property("-moz-text-fill-color", "transparent")
			property("-moz-background-clip", "text")
		}
	}

	val techStackList by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.8.cssRem)
		justifyContent(JustifyContent.Center)
	}

	val techBadge by style {
		border(1.px, LineStyle.Solid, Color.transparent)
		gradientBorderBackground(Color("#252525"))
		borderRadius(1.cssRem)
		color(Color.white)
		fontSize(.9.cssRem)
		padding(.5.cssRem, 1.1.cssRem)
	}

	val outro by style {
		maxWidth(textWidth)
		textAlign(TextAlign.Center)

		"p" {
			lineHeight(1.5.cssRem)
		}
	}
}
