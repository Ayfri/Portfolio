package style

import header.HeaderStyle
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import style.utils.*

object AppStyle : StyleSheet() {
	const val contentBackgroundColor = "#212125"
	const val monoFontFamily = "JetBrains Mono"
	const val linkColor = "#75C9CE"
	const val linkHoverColor = "#95fbff"
	const val titleBackgroundColor = "#252525"
	const val titleBorderColor = "#797979"
	const val specialTextColor = "#B4BBFF"
	
	init {
		"body" {
			fontFamily("Open Sans", "sans-serif")
			margin(0.px)
		}
		
		"a" style {
			transitions {
				delay(.25.s)
				properties("background-color", "color")
			}
			
			textDecoration("none")
		}
		
		desc("p", "a") style {
			color(Color(linkColor))
			
			hover(desc("p", "a")) style {
				color(Color(linkHoverColor))
			}
		}
		
		"button" {
			property("border", "none")
			
			transitions {
				delay(.25.s)
				properties("background-color", "color")
			}
		}
		
		id("main") style {
			backgroundColor(Color(contentBackgroundColor))
			color(Color.white)
			marginTop(HeaderStyle.navbarHeight.value())
		}
	}
	
	val sections by style {
		margin(0.px, max(2.cssRem, 4.vw))
		padding(3.cssRem)
		
		media(mediaMaxWidth(HeaderStyle.mobileFirstBreak)) {
			self {
				padding(1.5.cssRem)
				margin(0.px, .8.cssRem)
			}
			
			child(self, type("section")) style {
				gap(1.cssRem)
			}
		}
		
		media(mediaMaxWidth(HeaderStyle.mobileSecondBreak)) {
			self {
				padding(.8.cssRem)
				margin(0.px)
			}
		}
	}
	
	val title by style {
		backgroundColor(Color(titleBackgroundColor))
		border {
			color(Color(titleBorderColor))
			style(LineStyle.Solid)
			width(2.px)
		}
		borderRadius(1.cssRem)
		fontSize(2.1.cssRem)
		
		margin(0.px, 0.px, 3.cssRem)
		padding(1.cssRem, 1.5.cssRem)
		
		media(mediaMaxWidth(HeaderStyle.mobileFirstBreak)) {
			self {
				fontSize(1.8.cssRem)
				padding(.8.cssRem, 1.4.cssRem)
			}
		}
		
		media(mediaMaxWidth(HeaderStyle.mobileSecondBreak)) {
			self {
				fontSize(1.6.cssRem)
				marginBottom(1.8.cssRem)
			}
		}
	}
	
	val monoFont by style {
		fontFamily(monoFontFamily, "monospace")
	}
	
	val numberColor by style {
		color(Color(specialTextColor))
	}
	
	@OptIn(ExperimentalComposeWebApi::class)
	val avatar by style {
		objectFit(ObjectFit.Cover)
		borderRadius(100.vmax)
		size(15.cssRem)
		filter {
			dropShadow(
				offsetX = 0.px,
				offsetY = 0.px,
				blurRadius = 30.px,
				color = rgba(0, 0, 0, .75)
			)
		}
		
		media(mediaMaxWidth(HeaderStyle.mobileSecondBreak)) {
			self {
				size(max(7.cssRem, 30.vw))
			}
		}
	}
}
