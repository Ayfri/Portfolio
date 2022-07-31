package pages

import androidx.compose.runtime.Composable
import localImage
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import style.ObjectFit
import style.clamp
import style.objectFit
import style.size

@Composable
fun Home() {
	Style(HomeStyle)
	
	Section({
		classes(HomeStyle.top)
	}) {
		Div({
			classes(HomeStyle.topInfo)
		}) {
			Img(localImage("avatar.jpg"), "avatar")
			
			H2 {
				Text("Pierre Roy")
				
				Span {
					Text("alias")
				}
				
				Span {
					Text("Ayfri")
				}
			}
			
			H3 {
				Text("Born")
				
				Span {
					Text("19")
				}
				
				Text("years ago")
			}
			
			H3 {
				Text("IT Student")
			}
			
			H3 {
				Text("France")
			}
		}
		
		Div {
			P {
				Text(
					"""
					I am a first year student at Ynov Aix school, and I am passionate about computer science and especially programming.
					I realize all my projects with diligence, motivation, and always try to improve.
					I started programming as an autodidact in 2014 and my post-pac studies allow me to learn faster and new things.
					I am curious and always open to new technologies.
				""".trimIndent()
				)
			}
		}
	}
}

object HomeStyle : StyleSheet() {
	val top by style {
		display(DisplayStyle.Flex)
		height(15.cssRem)
		
		"img" style {
			objectFit(ObjectFit.Cover)
			borderRadius(100.vmax)
			size(15.cssRem)
		}
	}
	
	val topInfo by style {
		height(80.percent)
		width(clamp(34.cssRem, 23.vw, 38.cssRem))
	}
}