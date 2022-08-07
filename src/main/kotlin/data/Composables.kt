package data

import A
import P
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import style.utils.Overflow
import style.utils.TextAlign
import style.utils.overflow
import style.utils.textAlign
import style.utils.transitions

@Composable
fun HomeCard(repository: GitHubRepository) {
	H3 {
		Text(repository.name)
	}
	
	Div {
		P(repository.description ?: "No description provided.")
		
		P {
			Text("GitHub URL: ")
			
			A(repository.htmlUrl, repository.htmlUrl.substringAfter("github.com/"))
		}
	}
}

object DataStyle : StyleSheet() {
	const val homeCardBackground = "#181818"
	const val homeCardTitleBackground = "#0e0e0e"
	const val homeCardColor = "#cacaca"
	
	@OptIn(ExperimentalComposeWebApi::class)
	val homeCard by style {
		borderRadius(.8.cssRem)
		overflow(Overflow.Hidden)
		width(25.cssRem)
		
		transitions {
			ease(AnimationTimingFunction.EaseInOut)
			duration(.2.s)
			
			properties("transform")
		}
		
		transform {
			scale(1.0)
		}
		
		"h3" {
			backgroundColor(Color(homeCardTitleBackground))
			color(Color.white)
			margin(0.px)
			padding(1.cssRem)
		}
		
		"p" {
			color(Color(homeCardColor))
		}
		
		child(self, selector("div")) style {
			backgroundColor(Color(homeCardBackground))
			textAlign(TextAlign.Start)
			
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(3.cssRem)
			
			padding(1.cssRem)
		}
		
		combine(self, className("active")) style {
			transform {
				scale(1.05)
			}
		}
	}
}