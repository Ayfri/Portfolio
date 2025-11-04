package io.github.ayfri

fun String.titlecase() = split(" ").joinToString(" ") { word ->
	word.replaceFirstChar { char ->
		if (char.isLowerCase()) char.titlecase() else char.toString()
	}
}

inline fun String.ensureSuffix(suffix: String) = if (endsWith(suffix)) this else this + suffix
inline fun String.ifNotBlank(block: (String) -> String) = if (isNotBlank()) block(this) else ""
