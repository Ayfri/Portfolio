import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import style.utils.transitions

@Composable
fun I(vararg text: String) {
	I({
		classes(*text)
	})
}

enum class FontAwesomeType(val value: String) {
	REGULAR("far"),
	SOLID("fas"),
	LIGHT("fal"),
	DOUBLE("fad"),
	BRAND("fab");
}

@Composable
fun I(type: FontAwesomeType = FontAwesomeType.SOLID, icon: String) {
	I({
		classes(type.value, "fa-$icon")
	})
}

@Composable
fun P(text: String, vararg classes: String = emptyArray()) {
	P({
		classes(*classes)
	}) {
		Text(text)
	}
}

@Composable
fun A(href: String, content: String = "", vararg classes: String = arrayOf("link")) {
	A(href, {
		classes(*classes)
	}) {
		Text(content)
	}
}

@Composable
fun RichText(text: String) {
	val resultingText = text.split(Regex("<br>|\\[.*?\\]\\(.*?\\)", RegexOption.MULTILINE))
	resultingText.forEach {
		if (it.startsWith("[")) {
			val icon = it.substring(1, it.length - 1)
			I(icon = icon)
		} else if (it.startsWith("(")) {
			val icon = it.substring(1, it.length - 1)
			I(icon = icon)
		} else {
			Text(it)
		}
	}
}

@Composable
inline fun <T> Carousel(children: List<T>, carouselClass: String? = null, itemClass: String? = null, crossinline render: @Composable (T) -> Unit) {
	Style(CarouselStyle)
	
	Div({
		classes(CarouselStyle.carousel)
		carouselClass?.let { classes(it.split(" ")) }
	}) {
		var activeIndex by mutableStateOf(1)
		
		children.forEachIndexed { index, item ->
			Div({
				classes("item")
				itemClass?.let { classes(it.split(" ")) }
				
				if (index == activeIndex) classes("active")
				
				if (index in activeIndex - 1..activeIndex + 1) classes("current")
				
				onClick {
					activeIndex = index
					
					val element = it.nativeEvent.currentTarget as? HTMLDivElement ?: return@onClick
					val parent = element.parentElement as? HTMLDivElement ?: return@onClick
					parent.style.setProperty("--${CarouselStyle.carouselOffset.name}", "-${(index - 2) * 100 / children.size}%")
				}
			}) {
				render(item)
			}
		}
		
		// TODO : Find a way to loop around 
	}
}

object CarouselStyle : StyleSheet() {
	val carouselOffset by variable<CSSLengthValue>()
	
	@OptIn(ExperimentalComposeWebApi::class)
	val carousel by style {
		carouselOffset
		position(Position.Relative)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		justifyContent(JustifyContent.Center)
		alignItems(AlignItems.Center)
		
		gap(3.cssRem)
		
		child(self, not(className("current"))) style {
//			display(DisplayStyle.None)
			opacity(0.0)
		}
		
		transform {
			translateX(carouselOffset.value() as CSSLengthValue)
		}
		
		transitions {
			properties("transform", "opacity")
			duration(.2.s)
			ease(AnimationTimingFunction.EaseInOut)
		}
	}
}