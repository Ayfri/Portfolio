package style.utils

import org.jetbrains.compose.web.css.CSSNumericValue
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit

data class CSSSizeValueRange<T : CSSUnit>(val min: CSSSizeValue<T>, val max: CSSSizeValue<T>) {
	operator fun contains(value: CSSSizeValue<T>) = value.value >= min.value && value.value <= max.value
	
	override fun toString() = throw UnsupportedOperationException("Cannot convert CSSSizeValueRange to string.")
}

operator fun <T : CSSUnit> CSSSizeValue<T>.rangeTo(other: CSSSizeValue<T>) = CSSSizeValueRange(this, other)

data class CSSClamp<T : CSSUnit>(
	val value: CSSSizeValue<out T>,
	val min: CSSSizeValue<out T>,
	val max: CSSSizeValue<out T>,
) : CSSNumericValue<T> {
	override fun toString() = "clamp($min, $value, $max)"
}

fun clamp(
	min: CSSSizeValue<out CSSUnit>,
	value: CSSSizeValue<out CSSUnit>,
	max: CSSSizeValue<out CSSUnit>,
) = CSSClamp(value, min, max)

fun clamp(
	value: CSSSizeValue<out CSSUnit>,
	range: CSSSizeValueRange<out CSSUnit>,
) = CSSClamp(value, range.min, range.max)

class CSSMin<T : CSSUnit>(vararg val values: CSSSizeValue<out T>) : CSSNumericValue<T> {
	override fun toString() = "min(${values.joinToString(", ")})"
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CSSMin<*>) return false
		
		if (!values.contentEquals(other.values)) return false
		
		return true
	}
	
	override fun hashCode() = values.contentHashCode()
}

fun min(vararg values: CSSSizeValue<out CSSUnit>) = CSSMin(*values)

class CSSMax<T : CSSUnit>(vararg val values: CSSSizeValue<out T>) : CSSNumericValue<T> {
	override fun toString() = "max(${values.joinToString(", ")})"
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CSSMax<*>) return false
		
		if (!values.contentEquals(other.values)) return false
		
		return true
	}
	
	override fun hashCode() = values.contentHashCode()
}

fun max(vararg values: CSSSizeValue<out CSSUnit>) = CSSMax(*values)