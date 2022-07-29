package style

import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitTime
import org.jetbrains.compose.web.css.StyleScope

data class CSSTransition(
	var property: String? = null,
	var duration: CSSSizeValue<out CSSUnitTime>? = null,
	var timingFunction: AnimationTimingFunction? = null,
	var delay: CSSSizeValue<out CSSUnitTime>? = null,
) {
	override fun toString(): String {
		if (property == null) return ""
		var result = property!!
		
		duration?.let { result += " $it" }
		timingFunction?.let { result += " $it" }
		delay?.let { result += " $it" }
		
		return result
	}
}

fun CSSTransition.duration(value: CSSSizeValue<out CSSUnitTime>) = apply { duration = value }
fun CSSTransition.timingFunction(value: AnimationTimingFunction) = apply { timingFunction = value }
fun CSSTransition.delay(value: CSSSizeValue<out CSSUnitTime>) = apply { delay = value }
fun CSSTransition.ease(ease: AnimationTimingFunction) = timingFunction(ease)
data class CSSTransitions(
	var transitions: List<CSSTransition> = emptyList(),
	private var defaultDuration: CSSSizeValue<out CSSUnitTime>? = null,
	private var defaultTimingFunction: AnimationTimingFunction? = null,
	private var defaultDelay: CSSSizeValue<out CSSUnitTime>? = null,
) {
	override fun toString() =
		transitions.distinctBy {
			it.property
		}.joinToString(", ") {
			it.apply {
				if (defaultDelay != null && delay == null) delay = defaultDelay!!
				if (defaultDuration != null && duration == null) duration = defaultDuration!!
				if (defaultTimingFunction != null && timingFunction == null) timingFunction = defaultTimingFunction!!
			}.toString()
		}
	
	inline operator fun String.invoke(block: CSSTransition.() -> Unit) = CSSTransition().apply(block).also {
		it.property = this
		transitions += it
	}
	
	inline operator fun Iterable<String>.invoke(block: CSSTransition.() -> Unit) = CSSTransition().apply(block).also { transition ->
		forEach {
			transitions += transition.copy(property = it)
		}
	}
	
	inline operator fun Array<out String>.invoke(block: CSSTransition.() -> Unit) = CSSTransition().apply(block).also { transition ->
		forEach {
			transitions += transition.copy(property = it)
		}
	}
	
	inline fun properties(vararg properties: String, block: CSSTransition.() -> Unit = {}) = properties.invoke(block)
	
	inline fun all(block: CSSTransition.() -> Unit) = CSSTransition().apply(block).also { transition ->
		transition.property = "all"
		transitions += transition
	}
	
	fun duration(value: CSSSizeValue<out CSSUnitTime>) = apply { defaultDuration = value }
	fun timingFunction(value: AnimationTimingFunction) = apply { defaultTimingFunction = value }
	fun delay(value: CSSSizeValue<out CSSUnitTime>) = apply { defaultDelay = value }
	fun ease(ease: AnimationTimingFunction) = timingFunction(ease)
}

inline fun StyleScope.transitions(transitions: CSSTransitions.() -> Unit) {
	val transitionsValue = CSSTransitions().apply(transitions)
	property("transition", transitionsValue.toString())
}