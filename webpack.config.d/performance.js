// Merged by Kotlin/JS webpack; keeps production builds usable without noisy size hints.
// See https://kotlinlang.org/docs/js-project-setup.html#webpack-bundling
if (config.mode === "production") {
	config.performance = {
		hints: false,
		maxAssetSize: 6_000_000,
		maxEntrypointSize: 6_000_000,
	};
}
