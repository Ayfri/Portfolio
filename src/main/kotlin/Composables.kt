import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

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