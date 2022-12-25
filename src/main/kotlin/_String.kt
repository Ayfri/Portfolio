fun String.titlecase() = split(" ").joinToString(" ") { word ->
	word.replaceFirstChar { char ->
		if (char.isLowerCase()) char.titlecase() else char.toString()
	}
}
