package style

import org.jetbrains.compose.web.css.*

object CSSVariables : StyleSheet()

object AppStyle : StyleSheet() {
	const val navbarColor = "#363636"
	const val navbarColorSelected = "#2b2b2b"
	
	init {
		"body" {
			property("margin", "0")
			fontFamily("'Roboto'", "sans-serif")
		}
		
		"a" style {
			textDecoration("none")
		}
	}
	
	val navbar by style {
		val height = 5.cssRem
		
		backgroundColor(Color(navbarColor))
		display(DisplayStyle.Flex)
		height(height)
		
		"a" style {
			color(Color.white)
			height(100.percent)
			lineHeight(height)
			property("padding", "0 2rem")
			
			group(self + className("active"), hover(self)) style {
				backgroundColor(Color(navbarColorSelected))
			}
		}
	}
}
