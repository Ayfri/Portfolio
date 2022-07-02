
import org.jetbrains.compose.web.css.*

object CSSVariables : StyleSheet() {
//	val navbarBackground by variable<CSSColorValue>()
}

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
		alignItems(AlignItems.Center)
		backgroundColor(Color(navbarColor))
		display(DisplayStyle.Flex)
		height(6.cssRem)
		
		"a" style {
			color(Color.white)
			property("padding", "0 2rem")
			
			":hover" style {
				backgroundColor(Color(navbarColorSelected))
			}
		}
	}
}