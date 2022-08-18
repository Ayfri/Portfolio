package style

import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import style.utils.*

object AppStyle : StyleSheet() {
	const val contentBackgroundColor = "#212125"
	const val monoFontFamily = "JetBrains Mono"
	const val linkColor = "#75C9CE"
	const val linkHoverColor = "#95fbff"
	const val navbarColor = "#2A2B36"
	const val navbarColorSelected = "#1e1c28"
	const val titleBackgroundColor = "#252525"
	const val titleBorderColor = "#797979"
	const val specialTextColor = "#B4BBFF"
	
	
	val navbarHeight by variable<CSSNumeric>()
	val mobileNavBarFirstBreak = 860.px
	
	init {
		root {
			navbarHeight(5.cssRem)
		}
		
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
			marginTop(navbarHeight.value())
		}
	}
	
	val sections by style {
		margin(0.px, max(2.cssRem, 4.vw))
		padding(3.cssRem)
		
		media(mediaMaxWidth(600.px)) {
			self {
				padding(1.cssRem)
				margin(0.px, .5.cssRem)
			}
			
			child(self, type("section")) style {
				gap(1.cssRem)
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
		
		media(mediaMaxWidth(600.px)) {
			self {
				fontSize(1.8.cssRem)
				padding(.8.cssRem, 1.3.cssRem)
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
	}
	
	val navbar by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color(navbarColor))
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		position(Position.Fixed)
		size(navbarHeight.value(), 100.percent)
		top(0.px)
		zIndex(5)
		
		"i" style {
			fontSize(navbarHeight.value() * .6)
		}
	}
	
	val navbarPart by style {
		alignItems(AlignItems.Center)
		color(Color.white)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
	}
	
	val navbarLinks by style {
		"a" style {
			color(Color.white)
			height(100.percent)
			display(DisplayStyle.InlineBlock)
			lineHeight(navbarHeight.value())
			padding(0.px, clamp(1.5.cssRem, 3.vw, 2.8.cssRem))
			
			group(self + className("active"), hover(self)) style {
				backgroundColor(Color(navbarColorSelected))
			}
		}
	}
	
	val navbarGithub by style {
		borderRadius(1.5.cssRem)
		gap(1.cssRem)
		marginRight(2.cssRem)
		padding(.5.cssRem, 1.cssRem)
		
		hover(self) style {
			backgroundColor(Color(navbarColorSelected))
		}
		
		media(mediaMaxWidth(mobileNavBarFirstBreak)) {
			"p" {
				display(DisplayStyle.None)
			}
		}
	}
}
