package style

import org.jetbrains.compose.web.css.*
import style.CSSVariables.navbarHeight
import style.utils.size
import style.utils.transitions

object CSSVariables : StyleSheet() {
	val navbarHeight by variable<CSSNumeric>()
}

object AppStyle : StyleSheet() {
	const val navbarColor = "#2A2B36"
	const val navbarColorSelected = "#1e1c28"
	const val footerColor = "#1a1120"
	const val footerLinkHover = "#cccccc"
	const val contentBackgroundColor = "#212125"
	const val specialTextColor = "#B4BBFF"
	const val linkColor = "#75C9CE"
	const val linkHoverColor = "#95fbff"
	
	init {
		root {
			CSSVariables.navbarHeight(5.cssRem)
		}
		
		"body" {
			fontFamily("Open Sans", "sans-serif")
			margin(0.px)
		}
		
		"a" style {
			color(Color(linkColor))
			
			transitions {
				delay(.25.s)
				properties("background-color", "color")
			}
			
			textDecoration("none")
			
			hover(selector("a")) style {
				color(Color(linkHoverColor))
			}
		}
		
		id("main") style {
			backgroundColor(Color(contentBackgroundColor))
			color(Color.white)
			marginTop(navbarHeight.value())
			padding(1.cssRem)
		}
	}
	
	val monoFont by style {
		fontFamily("JetBrains Mono", "monospace")
	}
	
	val numberColor by style {
		color(Color(specialTextColor))
	}
	
	val navbar by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color(navbarColor))
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		position(Position.Fixed)
		size(navbarHeight.value(), 100.percent)
		top(0.px)
		
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
			padding(0.px, 2.cssRem)
			
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
	}
	
	val footerContact by style {
	
	}
	
	val footerInfo by style {
		backgroundColor(Color(footerColor))
		color(Color.white)
		padding(1.cssRem, 0.px)
		textAlign("center")
		width(100.percent)
		
		className("top") style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			gap(2.cssRem)
			justifyContent(JustifyContent.Center)
			listStyle("none")
			padding(0.px)
			
			"a" style {
				color(Color.white)
				fontSize(2.5.cssRem)
				
				hover(self) style {
					color(Color(footerLinkHover))
				}
			}
		}
	}
}
