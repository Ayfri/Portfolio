package pages

import androidx.compose.runtime.Composable
import localImage
import markdownParagraph
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import style.AppStyle
import style.utils.TextAlign
import style.utils.linearGradient
import style.utils.textAlign

@Composable
fun Portfolio() {
	Style(PortfolioStyle)
	
	Div({
		classes(PortfolioStyle.portfolio)
	}) {
		PortfolioSection(
			"""
				I created this portfolio first because my school asked me to create one to validate my first year. But also to have something to show for recruiters other than just my GitHub profile.
				I can clearly explain what do I do, how, why, when I started etc. This is why I created it using new technologies that I don’t know and designed it using method my boss learned me during this time.
			""".trimIndent(),
			"In first place, why ?",
			localImage("portfolio-1.png")
		)
		
		PortfolioSection(
			"""
				The design, conception and general idea of the portfolio was realised on Figma.
				A tool to create mock-ups, usually for websites. All repeatable parts of the site is a composant that I can reuse and modify once to modify all, with also the ability to create variations of composants.
				It’s also working very well for teams as changes are seen real time. Overall, it is a great tool to visualise and design pages alone or for a team and is almost completely free, paid parts are optional and really useful only to big teams.
			""".trimIndent(),
			"Conception",
			localImage("portfolio-2.png")
		)
		
		PortfolioSection(
			"""
				The website was programmed using the language Kotlin, and the framework Compose for Web. I’m practicing with Kotlin since 2 to 3 years, so I know well how to program using it and I only knew about Compose for Desktop.
				Both have great documentation, so it’s pretty easy to learn them, Compose is pretty recent so support is maybe a bit tedious to found. The force of Kotlin is to be able to compile to JVM (like Java) but also to JavaScript, Native and even WebAssembler was recently started.
				And once you joined the Slack workspace for Kotlin, you’ll have response to your problems very easily and quickly.
			""".trimIndent(),
			"Realisation",
			localImage("portfolio-3.png")
		)
		
		PortfolioSection(
			"""
				The website is statically hosted on GitHub by the service GitHub Pages, statically means that the content of the website will be the same for everyone at a given time. As it is just showing information, I don’t need to have something dynamic.
				GitHub Pages is a free service when you have GitHub Pro, which is given freely by my school. The host domain is provided by Namecheap, I can get one free host domain with extension .me by an offer given by my school. So everything is done freely thanks to Ynov.
			""".trimIndent(),
			"Upload to the World",
			localImage("portfolio-4.png")
		)
	}
}

@Composable
fun PortfolioSection(text: String, title: String, image: String) {
	Section({
		classes(PortfolioStyle.section, AppStyle.monoFont)
	}) {
		Div {
			H2 {
				Text(title)
			}
			
			P({
				markdownParagraph(text.replace("\n", "<br>"))
			})
		}
		
		Div {
			Img(image, alt = "")
		}
	}
}

object PortfolioStyle : StyleSheet() {
	const val titleGradientStart = "#D375EB"
	const val titleGradientEnd = "#6276E0"
	
	const val backgroundGradientStart = "#1E1D40"
	const val backgroundGradientMiddle = "#1D2736"
	const val backgroundGradientEnd = "#1F0C29"
	val sectionsGap = 4.cssRem
	
	init {
		id("main") style {
			padding(0.px)
		}
	}
	
	val portfolio by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(sectionsGap)
		
		padding(2.cssRem)
		
		background(linearGradient(225.deg) {
			stop(Color(backgroundGradientStart))
			stop(Color(backgroundGradientMiddle))
			stop(Color(backgroundGradientEnd))
		})
	}
	
	@OptIn(ExperimentalComposeWebApi::class)
	val section by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		position(Position.Relative)
		gap(2.5.cssRem)
		
		textAlign(TextAlign.Right)
		
		self + nthChild(Nth.Even) style {
			textAlign(TextAlign.Left)
			flexDirection(FlexDirection.RowReverse)
		}
		
		self + not(lastChild) + after style {
			val height = .5.cssRem
			property("content", "''")
			
			display(DisplayStyle.Block)
			height(height)
			width(4.vw)
			
			position(Position.Absolute)
			bottom((sectionsGap + height) * -.5)
			left(50.percent)
			transform {
				translateY((-50).percent)
			}
			
			backgroundColor(Color.white)
			borderRadius(3.px)
		}
		
		"h2" {
			background(linearGradient(20.deg) {
				stop(Color(titleGradientStart))
				stop(Color(titleGradientEnd))
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
			width(auto)
		}
	}
}