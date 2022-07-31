package style

import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.CSSNumericValue
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.StylePropertyEnum
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.css.width

fun StyleScope.size(height: CSSNumeric, width: CSSNumeric = height) {
	height(height)
	width(width)
}

fun StyleScope.size(height: CSSAutoKeyword, width: CSSAutoKeyword = height) {
	height(height)
	width(width)
}

interface ObjectFit : StylePropertyEnum {
	companion object {
		inline val Contain get() = ObjectFit("contain")
		inline val Cover get() = ObjectFit("cover")
		inline val Fill get() = ObjectFit("fill")
		inline val None get() = ObjectFit("none")
		inline val ScaleDown get() = ObjectFit("scale-down")
		
		inline val Inherit get() = ObjectFit("inherit")
		inline val Initial get() = ObjectFit("initial")
		inline val Revert get() = ObjectFit("revert")
		inline val Unset get() = ObjectFit("unset")
	}
}

fun ObjectFit(value: String) = value.unsafeCast<ObjectFit>()

fun StyleScope.objectFit(value: ObjectFit) {
	property("object-fit", value)
}

interface SizeKeyword : StylePropertyEnum

inline val fitContent get() = SizeKeyword("fit-content")
inline val maxContent get() = SizeKeyword("max-content")
inline val minContent get() = SizeKeyword("min-content")

fun SizeKeyword(value: String) = value.unsafeCast<SizeKeyword>()

fun StyleScope.size(value: SizeKeyword) {
	property("size", value)
}

fun StyleScope.width(value: SizeKeyword) {
	property("width", value)
}

fun StyleScope.height(value: SizeKeyword) {
	property("height", value)
	
	2.vw..5.vw
}

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