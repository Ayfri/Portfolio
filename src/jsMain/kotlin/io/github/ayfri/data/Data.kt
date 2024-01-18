package io.github.ayfri.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
	ignoreUnknownKeys = true
	namingStrategy = JsonNamingStrategy.SnakeCase
}

val gitHubData by lazy {
	json.decodeFromString<GitHubData>(rawData)
}
