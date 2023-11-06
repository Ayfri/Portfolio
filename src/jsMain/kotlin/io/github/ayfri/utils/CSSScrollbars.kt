package io.github.ayfri.utils

import org.jetbrains.compose.web.css.*

//region Firefox


fun StyleScope.scrollbarColor(color: CSSColorValue, color2: CSSColorValue? = null) {
	property("scrollbar-color", "$color ${color2?.toString() ?: ""}")
}

fun StyleScope.scrollbarWidth(width: CSSSizeValue<out CSSUnit>) {
	property("scrollbar-width", width.toString())
}

//endregion

//region webkit

val SelectorsScope.webkitResizer get() = selector("::-webkit-resizer")
val SelectorsScope.webkitScrollbar get() = selector("::-webkit-scrollbar")
val SelectorsScope.webkitScrollbarButton get() = selector("::-webkit-scrollbar-button")
val SelectorsScope.webkitScrollbarCorner get() = selector("::-webkit-scrollbar-corner")
val SelectorsScope.webkitScrollbarThumb get() = selector("::-webkit-scrollbar-thumb")
val SelectorsScope.webkitScrollbarTrack get() = selector("::-webkit-scrollbar-track")
val SelectorsScope.webkitScrollbarTrackPiece get() = selector("::-webkit-scrollbar-track-piece")

//endregion
