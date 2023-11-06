package io.github.ayfri.style.utils

import org.jetbrains.compose.web.css.CSSNumericValue
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

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

		return values.contentEquals(other.values)
	}

	override fun hashCode() = values.contentHashCode()
}

fun min(vararg values: CSSSizeValue<out CSSUnit>) = CSSMin(*values)

class CSSMax<T : CSSUnit>(vararg val values: CSSSizeValue<out T>) : CSSNumericValue<T> {
	override fun toString() = "max(${values.joinToString(", ")})"

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CSSMax<*>) return false

		return values.contentEquals(other.values)
	}

	override fun hashCode() = values.contentHashCode()
}

fun max(vararg values: CSSSizeValue<out CSSUnit>) = CSSMax(*values)

class CSSMinMax<T : CSSUnit>(val min: CSSSizeValue<out T>, val max: CSSSizeValue<out T>) : CSSNumericValue<T> {
	constructor(min: CSSSizeValue<out T>, max: CSSAutoKeyword) : this(min, max.unsafeCast<CSSSizeValue<out T>>())
	constructor(min: CSSAutoKeyword, max: CSSSizeValue<out T>) : this(min.unsafeCast<CSSSizeValue<out T>>(), max)

	override fun toString() = "minmax($min, $max)"

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CSSMinMax<*>) return false

		if (min != other.min) return false
		return max == other.max
	}

	override fun hashCode() = min.hashCode() * 31 + max.hashCode()
}

fun minmax(min: CSSSizeValue<out CSSUnit>, max: CSSSizeValue<out CSSUnit>) = CSSMinMax(min, max)
fun minmax(min: CSSSizeValue<out CSSUnit>, max: CSSAutoKeyword) = CSSMinMax(min, max)
fun minmax(min: CSSAutoKeyword, max: CSSSizeValue<out CSSUnit>) = CSSMinMax(min, max)
fun minmax(range: CSSSizeValueRange<out CSSUnit>) = CSSMinMax(range.min, range.max)

fun repeat(count: Int, value: CSSNumericValue<out CSSUnit>) = "repeat($count, $value)"
fun repeat(count: String, value: CSSNumericValue<out CSSUnit>) = "repeat($count, $value)"
