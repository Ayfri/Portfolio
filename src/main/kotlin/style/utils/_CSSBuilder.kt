package style.utils

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword
import org.jetbrains.compose.web.css.selectors.CSSSelector
import pages.HomeStyle.plus
import pages.HomeStyle.selector
import pages.HomeStyle.style

data class CSSSelectorList(val selectors: List<CSSSelector>) {
	constructor(vararg selectors: String) : this(selectors.map { selector(it) })
	
	override fun toString() = selectors.joinToString(", ")
	fun asSelector() = selector(toString())
	
	operator fun plus(other: CSSSelectorList) = CSSSelectorList(selectors + other.selectors)
	operator fun plus(other: CSSSelector) = CSSSelectorList(selectors + other)
	operator fun plus(other: String) = CSSSelectorList(selectors + selector(other))
	
	infix fun combined(other: CSSSelector) = CSSSelectorList(selectors.map { it + " " + other })
	infix fun combined(other: String) = CSSSelectorList(selectors.map { it + " " + selector(other) })
	
	fun desc(other: CSSSelector) = CSSSelectorList(selectors.map { it + ">" + other })
	fun desc(other: String) = CSSSelectorList(selectors.map { it + ">" + selector(other) })
	
	infix fun style(block: CSSStyleRuleBuilder.() -> Unit) = style(asSelector(), block)
}

fun SelectorsScope.list(vararg selectors: String) = CSSSelectorList(*selectors)

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

val CSSStyleRuleBuilder.self: CSSSelector get() = selector(":scope")

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

fun StyleScope.boxShadow(color: CSSColorValue, offset: CSSNumeric = 0.number, blur: CSSNumeric = 0.number, spread: CSSNumeric = 0.number, inset: Boolean = false) {
	property("box-shadow", "${if (inset) "inset " else ""}${offset} $blur $spread $color")
}
