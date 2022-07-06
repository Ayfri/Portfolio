package style

import org.jetbrains.compose.web.css.*

object CSSVariables : StyleSheet() {
	val navbarHeight by variable<CSSNumeric>()
}

object AppStyle : StyleSheet() {
	const val navbarColor = "#363636"
	const val navbarColorSelected = "#2b2b2b"
	
	init {
		"body" {
			fontFamily("'Roboto'", "sans-serif")
			margin(0.px)
		}
		
		"a" style {
			textDecoration("none")
		}
	}
	
	val navbar by style {
		CSSVariables.navbarHeight(5.cssRem)
		
		alignItems(AlignItems.Center)
		backgroundColor(Color(navbarColor))
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		position(Position.Fixed)
		size(CSSVariables.navbarHeight.value(), 100.percent)
		
		"i" style {
			fontSize(CSSVariables.navbarHeight.value() * .6)
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
			lineHeight(CSSVariables.navbarHeight.value())
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
}
