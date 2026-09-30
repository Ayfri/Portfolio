package io.github.ayfri

fun String.titlecase() = split(" ").joinToString(" ") { word ->
	word.replaceFirstChar { char ->
		if (char.isLowerCase()) char.titlecase() else char.toString()
	}
}

inline fun String.ensureSuffix(suffix: String) = if (endsWith(suffix)) this else this + suffix

private val markdownLinkRegex = Regex("""\[([^\]]+)\]\([^)]+\)""")

/** `[label](url)` becomes `label`, for descriptions rendered inside a link where a nested `<a>` would be invalid. */
fun String.withoutMarkdownLinks() = replace(markdownLinkRegex, "$1")
inline fun String.ifNotBlank(block: (String) -> String) = if (isNotBlank()) block(this) else ""
