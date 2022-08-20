package data

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.js.Promise
import kotlin.js.Promise.Companion.reject
import kotlin.js.Promise.Companion.resolve

val ktorClient = HttpClient(JsClient())

const val DATA_URL = "https://raw.githubusercontent.com/Ayfri/Ayfri.github.io/api/result.json"
val data by lazy {
	localStorage["data"]?.let {
		return@lazy Promise { resolve, _ -> resolve(JSON.parse(it)) }
	}
	
	CoroutineScope(window.asCoroutineDispatcher()).promise {
		try {
			val text = ktorClient.get(DATA_URL).bodyAsText()
			resolve(Json.decodeFromString<GitHubData>(text).also {
				localStorage["data"] = JSON.stringify(it)
			})
		} catch (e: Exception) {
			reject(e)
		}.await()
	}
}