package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.*
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.*

@Page("/portfolio/index")
@Composable
fun Portfolio() {
	PageLayout("Portfolio") {
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

			PortfolioSection(
				"""
				I created this portfolio first because my school asked me to create one to validate my first year.
				But also to have something to show for recruiters other than just a GitHub profile.
				I can explain what do I do, how, why, when I started etc.
				This is why I created it using new technologies that I don't know and designed it using the method my boss learned me during this time.
			""".trimIndent(),
				title = "In the first place, why?",
				image = localImage("portfolio-1-small.png"),
				width = 367,
				height = 226
			)

			PortfolioSection(
				"""
				The design, conception, and general idea of the portfolio was realized on Figma.
				A tool to create mock-ups, usually for websites.
				All repeatable parts of the site are a composant that I can reuse and modify once to modify all, with also the ability to create variations of composants.
				It is also working very well for teams as changes are seen in real time.<br>
				Overall, it is a great tool to visualise and design pages alone or for a team and is almost completely free, paid parts are optional and useful only for big teams.
			""".trimIndent(),
				title = "Conception",
				image = localImage("portfolio-2-small.png"),
				width = 480,
				height = 270
			)

			PortfolioSection(
				"""
				The website was programmed using the language Kotlin, and the framework Kobweb built on top of Compose HTML.
				I'm practicing with Kotlin since 2020, meaning I know well how to program in Kotlin.
				It has pretty good documentation, and it is pretty straightforward to learn, Compose is pretty recent, so support is maybe a bit tedious to find.
				The force of Kotlin is to be able to compile to JVM (like Java) but also to JavaScript, Native, and WebAssembler was recently started.
				And once you join the Slack workspace for Kotlin, you'll have response to your problems very easily and quickly.
			""".trimIndent(),
				title = "Realisation",
				image = localImage("portfolio-3-small.png"),
				width = 480,
				height = 258
			)

			PortfolioSection(
				"""
				This website is statically hosted on Cloudflare using the Cloudflare Pages service.
				When we say `statically`, it means the website isn't dynamic; it's simply a collection of files.
				Since it primarily displays information, there's no need for dynamic functionality.

				Cloudflare Pages is a free service that allows you to host your website on Cloudflare.
				It's user-friendly and doesn't require any payment.
				Namecheap provides the domain hosting, which is a paid service, but it's cost-effective and straightforward to use.
			""".trimIndent(),
				title = "Upload to the World",
				image = localImage("portfolio-4-small.png"),
				width = 480,
				height = 270
			)
		}
	}
}

@Composable
fun PortfolioSection(text: String, title: String, image: String, width: Int, height: Int) {
	Section({
		classes(PortfolioStyle.section, AppStyle.monoFont)
	}) {
		Div {
			H2 {
				Text(title)
			}

			P({
				markdownParagraph(text, true)
			})
		}

		Div {
			Img(image, alt = "Portfolio creation") {
				height(height)
				width(width)
			}
		}
	}
}

object PortfolioStyle : StyleSheet() {
	const val TITLE_GRADIENT_START = "#00D4FF"  // Cyan néon
	const val TITLE_GRADIENT_END = "#FF0080"   // Magenta néon

	const val BACKGROUND_GRADIENT_START = "#0A0A0F"  // Plus sombre
	const val BACKGROUND_GRADIENT_MIDDLE = "#1A1225" // Violet foncé
	const val BACKGROUND_GRADIENT_END = "#2A1B3D"    // Violet plus clair
	val sectionsGap = 4.cssRem



	val portfolio by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(sectionsGap)

		padding(2.cssRem)

		background(linearGradient(225.deg) {
			stop(Color(BACKGROUND_GRADIENT_START))
			stop(Color(BACKGROUND_GRADIENT_MIDDLE))
			stop(Color(BACKGROUND_GRADIENT_END))
		})

		media(mediaMaxWidth(AppStyle.mobileSecondBreak)) {
			self {
				padding(1.2.cssRem, .8.cssRem)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val section by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		position(Position.Relative)
		gap(2.5.cssRem)

		textAlign(TextAlign.Right)

		media(mediaMinWidth(AppStyle.mobileFirstBreak + 1.px)) {
			self + nthChild(Nth.Even) style {
				textAlign(TextAlign.Left)
				flexDirection(FlexDirection.RowReverse)
			}
		}

		self + not(lastChild) + after style {
			val height = .5.cssRem
			property("content", "''")

			display(DisplayStyle.Block)
			height(height)
			width(4.vw)

			position(Position.Absolute)
			bottom((sectionsGap * -.5) - height)
			left(50.percent)
			transform {
				translateX((-50).percent)
			}

			backgroundColor(Color.white)
			borderRadius(3.px)
		}

		"h2" {
			background(linearGradient(20.deg) {
				stop(Color(TITLE_GRADIENT_START))
				stop(Color(TITLE_GRADIENT_END))
			})

			fontSize(2.5.cssRem)
			marginTop(0.px)
			marginBottom(1.cssRem)

			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
			property("-moz-text-fill-color", "transparent")
			property("-moz-background-clip", "text")
		}

		"p" {
			lineHeight(1.3.cssRem)
		}

		"img" {
			borderRadius(.8.cssRem)
			property("box-shadow", "0 0 .75rem #71A0E8")

			height(14.cssRem)
			objectFit(ObjectFit.Cover)
			width(auto)
		}

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				flexDirection(FlexDirection.Column)
				alignItems(AlignItems.Stretch)
				justifyContent(JustifyContent.Center)
				textAlign(TextAlign.Center)

				"h2" {
					fontSize(2.cssRem)
				}

				"img" {
					maxWidth(100.percent)
				}
			}
		}

		media(mediaMaxWidth(AppStyle.mobileSecondBreak)) {
			self {
				"img" {
					height(auto)
				}
			}
		}
	}
}
