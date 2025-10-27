package io.github.ayfri.data


val gitHubData by lazy {
	JSON.parse<GitHubData>(rawData, ::snakeCaseReviver)
}
