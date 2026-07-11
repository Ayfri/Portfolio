---
nav-title: Building This Portfolio
title: How I Built This Portfolio in Kotlin with Kobweb
description: The story and the tech behind [ayfri.com](https://ayfri.com), a portfolio written entirely in Kotlin with [Kobweb](https://kobweb.varabyte.com/) and Compose for Web.
keywords: kotlin, kobweb, compose for web, compose html, kotlin/js, static site, portfolio, kotlin web framework, css in kotlin, ssg
date-created: 2026-07-10
date-modified: 2026-07-10
root: .layouts.ArticleLayout
routeOverride: /articles/building-this-portfolio/index
---

The website you are reading this on is written entirely in [Kotlin](https://kotlinlang.org/). No HTML files, no CSS files, no JavaScript I wrote by hand. Every page, every style rule, and the whole build pipeline is Kotlin compiled to JavaScript through [Kobweb](https://kobweb.varabyte.com/) and [Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html).

This article is the story of how it got here, why I picked this stack, and the parts of the build that I think are actually interesting.

## How it started: a school assignment

This portfolio was not a free choice at first. It was a mandatory summer project for my school, [Ynov Aix](https://www.ynov.com/campus/aix-en-provence), at the end of my first year. Everyone had to build a personal portfolio over the break. Most people reached for the usual HTML/CSS/JS or a template. I decided to make mine a bit harder on purpose and write the whole thing in Kotlin, because I wanted to actually learn something instead of filling in a template.

My internship tutor at the time helped me with the design side, which is not my strong suit. The overall layout, the color choices, and the general feel of those early versions came out of that back-and-forth. I handled the code, he pushed me on how it looked.

Here is the very first shipped version, from June 2023, still a single scrollable page with a contact form at the bottom:

![The June 2023 version of the portfolio, a single page with avatar, skills icons, and a contact form](/images/building-portfolio/2023-06-screenshot.avif)

## Why Kotlin for a website

I like Kotlin. That is honestly most of the reason. But there is a real argument too: I get one language with a strong type system for the UI, the styling, and the build script. If I rename a component, the compiler tells me every place I broke. If I typo a CSS color constant, it does not build. That safety net does not exist when you are gluing together HTML, CSS, and JS by hand.

The tradeoff is that the ecosystem is tiny compared to the JS world, and you feel the bleeding edge regularly. I was fine with that. What started as a school constraint turned into a long-running playground I keep coming back to.

## A short history of progression

The project started in **June 2022**. The first version was raw [Compose for Web](https://github.com/JetBrains/compose-multiplatform) with the [`routing-compose`](https://github.com/hfhbd/routing-compose) library bolted on for navigation. It worked, but I was hand-rolling a lot of things Kobweb gives you for free: routing, the head block, static export, a dev server with live reload.

Some milestones from the git log:

- **June 2022** - Initial commit. Plain Compose HTML plus `routing-compose`, Kotlin 1.6.21.
- **2022-2023** - Slow iteration. Meta tags moved into the HTML directly so they actually work for SEO, localStorage safe-guards, dependency bumps.
- **November 2023** - The big one. I [installed Kobweb](https://kobweb.varabyte.com/), moved everything into the `io.github.ayfri` package, switched to Gradle [version catalogs](https://docs.gradle.org/current/userguide/version_catalogs.html), and ripped out `routing-compose` in favor of Kobweb's file-based routing and layouts.
- **2024-2026** - Steady modernization. Kotlin 1.9 to 2.3, Compose 1.5 to 1.10, Kobweb 0.15 to 0.24, plus a 404 page, a generated sitemap, JSON-LD schema, and proper meta descriptions per page and article.

The migration to Kobweb in late 2023 is the moment this went from a hobby experiment to something maintainable. Dropping `routing-compose` for `@Page`-annotated files meant a new page is just a new Kotlin file in the right folder.

By August 2024 the home page had grown real project cards pulled from GitHub, an Articles tab, and a cleaner layout:

![The August 2024 version, now with GitHub project cards for Kore, Advanced-Command-Handler and PIXI-Kotlin, plus an Articles tab](/images/building-portfolio/2024-08-screenshot.avif)

## What Kobweb actually gives you

[Kobweb](https://kobweb.varabyte.com/) is a framework built on top of Compose HTML by [Varabyte](https://varabyte.com/). The pieces I lean on most:

- **File-based routing.** A `@Page` annotation on a composable in `pages/` becomes a route. `pages/AboutMe.kt` maps to `/about-me`.
- **Static export.** `./gradlew kobwebExport` renders every route to static HTML, so the site loads fast and is fully crawlable, then hydrates into a single-page app.
- **A typed `head` block.** I configure meta tags, preloads, and fonts in the Gradle build with a Kotlin DSL instead of an HTML template.
- **Live reload dev server.** `./gradlew kobwebStart -t` and the page reloads on save.

Here is what a page entry point looks like. This is the real home page:

```kotlin
@Page("/index")
@Composable
fun Home() {
	PageLayout("Home") {
		// sections composed here
	}
}
```

The `PageLayout` wraps the content with the header, footer, and shared page chrome. Articles use a different root, `ArticleLayout`, set right in their Markdown front matter.

## CSS in Kotlin, no stylesheet files

There are zero `.css` files I wrote for layout. Styling is done with Compose HTML's `StyleSheet` DSL, which is type-safe and colocated with the component. A real example, the back-to-top button:

```kotlin
data object BackToTopStyle : StyleSheet() {
	const val BUTTON_COLOR = "#FFFFFF1A"
	const val BUTTON_HOVER_COLOR = "#FFFFFF40"

	val button by style {
		position(Position.Fixed)
		bottom(30.px)
		right(30.px)
		size(50.px)
		borderRadius(50.percent)
		backgroundColor(Color(BUTTON_COLOR))
		opacity(0)

		transitions {
			"opacity" { duration(0.3.s) }
			"transform" { duration(0.3.s) }
		}

		self + hover style {
			backgroundColor(Color(BUTTON_HOVER_COLOR))
			transform { scale(1.1) }
		}
	}
}
```

`self + hover` compiles to a `:hover` selector. Units like `30.px`, `0.3.s`, and `50.percent` are real Kotlin extension properties, so a typo is a compile error, not a silently broken rule. I get autocomplete on CSS properties, which sounds small but adds up over hundreds of rules.

## The build script does the heavy lifting

The most interesting code in this repo is not a page, it is [`build.gradle.kts`](https://github.com/Ayfri/Portfolio/blob/master/build.gradle.kts). A few things happen at build time so the browser never has to.

### Markdown to Kotlin at compile time

Articles live as `.md` files with YAML front matter in `resources/markdown/articles/`. The KobwebX Markdown plugin converts each one into a Compose composable at build time. On top of that, I hook the build to generate a single `articlesEntries` list holding every article's metadata, sorted by date, so the article index page is fully static:

```kotlin
process = { markdownFiles ->
	// validate front matter, collect entries, then:
	generateKotlin("$projectGroup/articles.kt", buildString {
		appendLine("val articlesEntries = listOf(")
		blogEntries.sortedByDescending(BlogEntry::date).forEach { entry ->
			// emit an ArticleEntry(...) per article
		}
		appendLine(")")
	})
}
```

I also override the Markdown handlers so images get `loading="lazy"` and `decoding="async"` automatically, and code blocks route through my own `CodeBlock` composable for [Prism.js](https://prismjs.com/) highlighting. One gotcha with generating Kotlin from Markdown: dollar signs. Kotlin treats `$` as string interpolation, so any `$` in an article has to be escaped to `${"$"}` in the generated source, which the build does with a regex pass.

### Project data fetched at build time

My GitHub projects are not fetched in the browser. A `downloadData` Gradle task pulls a [JSON snapshot](https://raw.githubusercontent.com/Ayfri/Portfolio/api/result.json) of my repos and generates Kotlin source from it. The data is available during static export, so crawlers receive the final project content without hitting the GitHub API on every visit.

### A sitemap that reads my own source

The `generateSitemap` task walks the `pages/` directory looking for `@Page` annotations to discover static routes, then reads each article's `routeOverride` and `date-modified` from front matter for accurate `lastmod` values. The sitemap is generated from the source of truth, so it can never drift out of sync with the actual routes.

## The current stack

As of writing, the versions are:

- **Kotlin 2.4.0** targeting ES2015 with ES modules and ES classes
- **Compose HTML 1.11.1** for the UI, Compose runtime 1.10
- **Kobweb 0.25** for routing, export, and the head DSL
- **kotlinx wrappers** for typed browser APIs, plus a bit of `marked` via npm

The Kotlin/JS compiler flags are tuned for a smaller, faster bundle: `-Xir-property-lazy-initialization`, no polyfill generation, and `es2015` output so I ship modern JS instead of transpiled-down cruft.

Here is the home page today, July 2026, grown well past a single scrollable page:

![The July 2026 version of the portfolio, with featured projects, top skills, a portfolio teaser, professional experience, about me, articles, and contact sections all on the home page](/images/building-portfolio/2026-07-screenshot.avif)

## Was it worth it

For a production app with a team, I would probably reach for [SvelteKit](https://svelte.dev/). For my own portfolio, Kotlin all the way down has been genuinely enjoyable. I refactor without fear, styling and logic live in the same typed world, and the build script grew into a little static-site generator tailored exactly to what I need.

If you want to poke around, the whole thing is [open source on GitHub](https://github.com/Ayfri/Portfolio). Clone it, run `./gradlew kobwebStart -t`, and you have the same live-reload setup I develop with every day.
