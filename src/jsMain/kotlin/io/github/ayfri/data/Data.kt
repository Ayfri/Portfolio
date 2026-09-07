package io.github.ayfri.data

/** Keys are already camelCased by the `downloadData` Gradle task, so this is a plain parse. */
private val portfolioSnapshot: GitHubData by lazy { JSON.parse(portfolioSnapshotJson) }

fun portfolioData() = portfolioSnapshot
