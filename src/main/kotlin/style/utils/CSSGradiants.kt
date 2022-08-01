package style.utils

import org.jetbrains.compose.web.css.CSSAngleValue
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.deg

data class LinearGradient(
	val angle: CSSAngleValue = 0.deg,
	var stops: List<Stop> = emptyList(),
) {
	override fun toString() = "linear-gradient($angle, ${stops.joinToString(", ") { it.toString() }})"
	
	fun stop(color: CSSColorValue, start: CSSSizeValue<out CSSUnit>? = null, end: CSSSizeValue<out CSSUnit>? = null) {
		stops += Stop(color, start, end)
	}
}

data class Stop(
	val color: CSSColorValue,
	val start: CSSSizeValue<out CSSUnit>? = null,
	val end: CSSSizeValue<out CSSUnit>? = null,
) {
	override fun toString() = "$color ${start?.toString() ?: ""} ${end?.toString() ?: ""}"
}

fun linearGradient(
	angle: CSSAngleValue = 0.deg,
	vararg stops: Stop,
) = LinearGradient(angle, stops.toList()).toString()

fun linearGradient(
	angle: CSSAngleValue = 0.deg,
	block: LinearGradient.() -> Unit,
) = LinearGradient(angle).apply(block).toString()
