package io.github.ayfri.data

/** Keys are already camelCased by the `downloadData` Gradle task, so this is a plain parse. */
val portfolioData: GitHubData by lazy { JSON.parse(portfolioSnapshotJson) }
