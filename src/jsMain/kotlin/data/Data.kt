package data

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.promise
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.js.Promise.Companion.resolve

val ktorClient = HttpClient(JsClient())

const val DATA_URL = "https://raw.githubusercontent.com/Ayfri/Ayfri.github.io/api/result.json"

val data by lazy {
	val resolve = runCatching {
		localStorage["data"]?.let {
			return@runCatching Json.decodeFromString<GitHubData>(it)
		}
	}

	val resolved = resolve.getOrNull()

	return@lazy when {
		resolve.isFailure || resolved == null -> {
			CoroutineScope(window.asCoroutineDispatcher()).promise {
				val response = ktorClient.get(DATA_URL)
				val result = response.bodyAsText()
				localStorage["data"] = result
				return@promise Json.decodeFromString<GitHubData>(result)
			}
		}

		else -> resolved.let(::resolve)
	}
}
