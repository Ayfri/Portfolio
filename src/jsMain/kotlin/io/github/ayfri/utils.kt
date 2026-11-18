package io.github.ayfri

import io.github.ayfri.externals.parse
import js.date.Date
import js.intl.*
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.asList
import kotlin.math.max
import kotlin.math.roundToInt

inline fun localImage(path: String) = "/images/$path"

fun AttrsScope<HTMLParagraphElement>.markdownParagraph(
	text: String,
	breaks: Boolean = false,
	vararg classes: String,
) {
	ref {
		if (classes.isNotEmpty()) it.classList.add(*classes)

		val textToParse = if (breaks) text.replace("\n", "<br>") else text
		it.innerHTML = parse(textToParse)
		/** Code blocks are tagged after parsing because `marked.use()` only reads enumerable renderer props, which Kotlin class methods never are. */
		it.querySelectorAll("pre > code").asList().forEach { node ->
			val code = node.unsafeCast<Element>()
			if (code.className.isEmpty()) code.classList.add("nohighlight")
			code.classList.add("line-numbers")
		}
		onDispose {}
	}
}

private val wordSeparatorRegex = Regex("\\s+")

/** Estimated reading time in minutes, at an average 250 words per minute. */
fun calculateReadingTime(content: String) = max(1, (content.split(wordSeparatorRegex).size / 250.0).roundToInt())

private val longDateOptions = DateTimeFormatOptions(
	year = YearFormat.numeric,
	month = MonthFormat.long,
	day = DayFormat.numeric,
)

/** "November 13, 2023" for an ISO date, falling back to the raw day part rather than rendering "Invalid Date". */
fun formatLongDate(isoDate: String) = runCatching {
	val date = Date(isoDate)
	check(!date.getTime().isNaN())
	date.toLocaleDateString("en-US", longDateOptions)
}.getOrElse { isoDate.substringBefore("T") }
