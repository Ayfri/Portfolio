package style.utils

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword
import org.jetbrains.compose.web.css.selectors.CSSSelector
import org.jetbrains.compose.web.css.selectors.Nth
import pages.HomeStyle.selector

fun CSSBuilder.list(vararg selectors: String) = group(*(selectors.map { desc(self, it) }.toTypedArray()))
fun CSSBuilder.list(vararg selectors: CSSSelector) = group(*(selectors.map { desc(self, it) }.toTypedArray()))

fun StyleScope.size(height: CSSNumeric, width: CSSNumeric = height) {
	height(height)
	width(width)
}

fun StyleScope.size(height: CSSAutoKeyword, width: CSSAutoKeyword = height) {
	height(height)
	width(width)
}

fun StyleScope.size(value: SizeKeyword) {
	height(value)
	width(value)
}

fun StyleScope.height(value: SizeKeyword) = property("height", value)
fun StyleScope.width(value: SizeKeyword) = property("width", value)

interface SizeKeyword : StylePropertyEnum

inline val fitContent get() = SizeKeyword("fit-content")
inline val maxContent get() = SizeKeyword("max-content")
inline val minContent get() = SizeKeyword("min-content")

fun SizeKeyword(value: String) = value.unsafeCast<SizeKeyword>()

fun StyleScope.accentColor(value: CSSColorValue) = property("accent-color", value)

fun StyleScope.borderImageSource(value: String) = property("border-image-source", value)
fun StyleScope.borderImageSlice(value: Int) = property("border-image-slice", value)

fun StyleScope.borderBottom(block: CSSBorder.() -> Unit) = property("border-bottom", CSSBorder().apply(block))
fun StyleScope.borderLeft(block: CSSBorder.() -> Unit) = property("border-left", CSSBorder().apply(block))
fun StyleScope.borderRight(block: CSSBorder.() -> Unit) = property("border-right", CSSBorder().apply(block))
fun StyleScope.borderTop(block: CSSBorder.() -> Unit) = property("border-top", CSSBorder().apply(block))

fun StyleScope.boxShadow(
	color: CSSColorValue,
	offset: CSSNumeric = 0.number,
	blur: CSSNumeric = 0.number,
	spread: CSSNumeric = 0.number,
	inset: Boolean = false,
) = property("box-shadow", "${if (inset) "inset " else ""}${offset} $blur $spread $color")

interface Cursor : StylePropertyEnum {
	companion object {
		inline val Auto get() = Cursor("auto")
		inline val Default get() = Cursor("default")
		inline val None get() = Cursor("none")
		inline val ContextMenu get() = Cursor("context-menu")
		inline val Help get() = Cursor("help")
		inline val Pointer get() = Cursor("pointer")
		inline val Progress get() = Cursor("progress")
		inline val Wait get() = Cursor("wait")
		inline val Cell get() = Cursor("cell")
		inline val Crosshair get() = Cursor("crosshair")
		inline val Text get() = Cursor("text")
		inline val VerticalText get() = Cursor("vertical-text")
		inline val Alias get() = Cursor("alias")
		inline val Copy get() = Cursor("copy")
		inline val Move get() = Cursor("move")
		inline val NoDrop get() = Cursor("no-drop")
		inline val Grab get() = Cursor("grab")
		inline val Grabbing get() = Cursor("grabbing")
		inline val NotAllowed get() = Cursor("not-allowed")
		inline val AllScroll get() = Cursor("all-scroll")
		inline val ColResize get() = Cursor("col-resize")
		inline val RowResize get() = Cursor("row-resize")
		inline val NResize get() = Cursor("n-resize")
		inline val EResize get() = Cursor("e-resize")
		inline val SResize get() = Cursor("s-resize")
		inline val WResize get() = Cursor("w-resize")
		inline val NeResize get() = Cursor("ne-resize")
		inline val NwResize get() = Cursor("nw-resize")
		inline val SeResize get() = Cursor("se-resize")
		inline val SwResize get() = Cursor("sw-resize")
		inline val EwResize get() = Cursor("ew-resize")
		inline val NsResize get() = Cursor("ns-resize")
		inline val NeswResize get() = Cursor("nesw-resize")
		inline val NwseResize get() = Cursor("nwse-resize")
		inline val ZoomIn get() = Cursor("zoom-in")
		inline val ZoomOut get() = Cursor("zoom-out")
	}
}

fun Cursor(value: String) = value.unsafeCast<Cursor>()
fun StyleScope.cursor(value: Cursor) = property("cursor", value)

fun StyleScope.inset(top: CSSNumeric, right: CSSNumeric, bottom: CSSNumeric, left: CSSNumeric) =
	property("inset", "$top $right $bottom $left")

fun StyleScope.inset(top: CSSNumeric, horizontal: CSSNumeric, bottom: CSSNumeric) =
	property("inset", "$top $horizontal $bottom")

fun StyleScope.inset(vertical: CSSNumeric, horizontal: CSSNumeric) = property("inset", "$vertical $horizontal")
fun StyleScope.inset(value: CSSNumeric) = property("inset", value)


fun StyleScope.margin(value: CSSNumeric, auto: CSSAutoKeyword) = property("margin", "$value $auto")
fun StyleScope.margin(auto: CSSAutoKeyword, value: CSSNumeric) = property("margin", "$auto $value")
fun StyleScope.margin(value: CSSAutoKeyword) = property("margin", value)

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
fun StyleScope.objectFit(value: ObjectFit) = property("object-fit", value)

interface Overflow : StylePropertyEnum {
	companion object {
		inline val Auto get() = Overflow("auto")
		inline val Hidden get() = Overflow("hidden")
		inline val Scroll get() = Overflow("scroll")
		inline val Visible get() = Overflow("visible")

		inline val Inherit get() = Overflow("inherit")
		inline val Initial get() = Overflow("initial")
		inline val Revert get() = Overflow("revert")
		inline val Unset get() = Overflow("unset")
	}
}

fun Overflow(value: String) = value.unsafeCast<Overflow>()
fun StyleScope.overflow(value: Overflow) = property("overflow", value)

interface TextAlign : StylePropertyEnum {
	companion object {
		inline val Left get() = TextAlign("left")
		inline val Right get() = TextAlign("right")
		inline val Center get() = TextAlign("center")
		inline val Justify get() = TextAlign("justify")
		inline val Start get() = TextAlign("start")
		inline val End get() = TextAlign("end")

		inline val Inherit get() = TextAlign("inherit")
		inline val Initial get() = TextAlign("initial")
		inline val Revert get() = TextAlign("revert")
		inline val Unset get() = TextAlign("unset")
	}
}

interface Resize : StylePropertyEnum {
	companion object {
		inline val Both get() = Resize("both")
		inline val Horizontal get() = Resize("horizontal")
		inline val None get() = Resize("none")
		inline val Vertical get() = Resize("vertical")

		inline val Inherit get() = Resize("inherit")
		inline val Initial get() = Resize("initial")
		inline val Revert get() = Resize("revert")
		inline val Unset get() = Resize("unset")
	}
}

fun Resize(value: String) = value.unsafeCast<Resize>()
fun StyleScope.resize(value: Resize) = property("resize", value)

fun TextAlign(value: String) = value.unsafeCast<TextAlign>()
fun StyleScope.textAlign(value: TextAlign) = property("text-align", value)

fun StyleScope.zIndex(value: Int) = property("z-index", value)

val SelectorsScope.placeholder get() = slotted(selector("placeholder"))
val CSSStyleRuleBuilder.self get() = selector(":scope")

val Int.n get() = Nth.Functional(this)
