package io.github.ayfri.data

import js.promise.await
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.promise
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.w3c.dom.get
import org.w3c.dom.set
import web.http.fetchAsync
import kotlin.js.Promise.Companion.resolve

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
	ignoreUnknownKeys = true
	namingStrategy = JsonNamingStrategy.SnakeCase
}

const val DATA_URL = "https://raw.githubusercontent.com/Ayfri/Ayfri.github.io/api/result.json"
const val LOCAL_DATA_KEY = "data"

val data by lazy {
	val resolve = runCatching {
		localStorage[LOCAL_DATA_KEY]?.let {
			return@runCatching json.decodeFromString<GitHubData>(it)
		}
	}

	val resolved = resolve.getOrNull()

	return@lazy when {
		resolve.isFailure || resolved == null -> {
			CoroutineScope(window.asCoroutineDispatcher()).promise {
				val response = fetchAsync(DATA_URL).await()
				val jsonData = response.text().await()
				val result = json.decodeFromString<GitHubData>(jsonData)
				localStorage[LOCAL_DATA_KEY] = jsonData
				return@promise result
			}
		}

		else -> resolved.let(::resolve)
	}
}
