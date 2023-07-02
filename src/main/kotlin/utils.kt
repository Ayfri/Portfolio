fun Any.prettyPrint(): String {
	var indentLevel = 0
	val indentWidth = 4

	fun padding() = "".padStart(indentLevel * indentWidth)

	val toString = toString()

	val stringBuilder = StringBuilder(toString.length)

	var i = 0
	while (i < toString.length) {
		when (val char = toString[i]) {
			'(', '[', '{' -> {
				indentLevel++
				stringBuilder.appendLine(char).append(padding())
			}

			')', ']', '}' -> {
				indentLevel--
				stringBuilder.appendLine().append(padding()).append(char)
			}

			',' -> {
				stringBuilder.appendLine(char).append(padding())
				val nextChar = toString.getOrElse(i + 1) { char }
				if (nextChar == ' ') i++
			}

			else -> stringBuilder.append(char)
		}
		i++
	}

	return stringBuilder.toString()
}
