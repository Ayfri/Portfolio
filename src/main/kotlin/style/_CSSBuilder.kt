package style

import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.StylePropertyEnum
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword
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
}
