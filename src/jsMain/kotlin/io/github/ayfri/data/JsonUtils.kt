package io.github.ayfri.data

/**
 * Converts a snake_case string to camelCase
 */
fun String.snakeToCamel(): String {
	return this.split('_').mapIndexed { index, part ->
		if (index == 0) part else part.replaceFirstChar { it.uppercase() }
	}.joinToString("")
}

/**
 * JSON.parse reviver that converts snake_case keys to camelCase
 */
fun snakeCaseReviver(key: String, value: dynamic): dynamic {
	if (value != null && jsTypeOf(value) == "object" && !js("Array.isArray(value)") as Boolean) {
		val newObj = js("{}")
		val keys = js("Object.keys(value)") as Array<String>

		for (k in keys) {
			val camelKey = k.snakeToCamel()
			newObj[camelKey] = value[k]
		}

		return newObj
	}
	return value
}
