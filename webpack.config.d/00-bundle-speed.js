// Kotlin/JS IR emits the whole app as one huge module, the worst case for both webpack production defaults:
// `ModuleConcatenationPlugin` scope-hoists the siblings into it for ~1% of transfer size, and Terser then
// minifies the single resulting asset on one thread. SWC is native and multi-threaded within a file.
;(function () {
	if (config.mode !== 'production') return;
	const MinimizerPlugin = require('minimizer-webpack-plugin');
	config.optimization = config.optimization || {};
	config.optimization.concatenateModules = false;
	config.optimization.minimizer = [
		new MinimizerPlugin({
			extractComments: false,
			minify: MinimizerPlugin.swcMinify,
			terserOptions: {
				compress: true,
				format: { comments: false },
				mangle: true,
			},
		}),
	];
})();
