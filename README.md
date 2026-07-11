# Portfolio

[ayfri.com](https://ayfri.com) is the personal portfolio and blog of [Pierre Roy](https://github.com/Ayfri), also known as Ayfri.

It is a Kotlin/Compose for Web site built with [Kobweb](https://kobweb.varabyte.com/). The static export contains the final page content, including the GitHub project catalogue, so crawlers do not receive a client-side loading shell.

## Stack

- Kotlin 2.4
- Compose for Web
- Kobweb 0.25
- KobwebX Markdown for articles
- Gradle 9.6 via the included wrapper
- Cloudflare Pages for deployment

## What Is Here

- Responsive portfolio pages: home, about, skills, experience, projects, and selected work.
- Markdown articles with front matter, syntax highlighting, SEO metadata, JSON-LD, related articles, and table of contents.
- A generated sitemap at `/sitemap.xml`.
- A GitHub project catalogue embedded into the build and rendered into static HTML.

## Requirements

- JDK 25, matching the deployment workflow.
- No global Gradle installation is required.
- Kobweb CLI only for manual static exports. The CI workflow downloads it automatically.

## Local Development

Start the development server:

```powershell
.\gradlew.bat kobwebStart -t
```

Open [http://localhost:8080](http://localhost:8080). Gradle recompiles the site after source changes.

Useful commands:

```powershell
# Compile the production Kotlin/JS bundle
.\gradlew.bat compileProductionExecutableKotlinJs

# Refresh only the project snapshot used by the site
.\gradlew.bat downloadData

# Regenerate the sitemap
.\gradlew.bat generateSitemap
```

## Static Export

Kobweb CLI performs the static export. The generated site is written to `.kobweb/site/`.

```powershell
kobweb export --layout static
```

The CI workflow in [`.github/workflows/CD.yml`](.github/workflows/CD.yml) uses the same command with a pinned Kobweb CLI version and deploys `.kobweb/site/` to Cloudflare Pages.

## Project Data

Project data comes from the [`api`](https://github.com/Ayfri/Portfolio/tree/api) branch:

```text
https://raw.githubusercontent.com/Ayfri/Portfolio/api/result.json
```

During the build, `downloadData` downloads and minifies that snapshot, then generates `PortfolioSnapshot.kt` under `build/generated/portfolio-data/`. Kotlin/JS compilation depends on this task, so data is available synchronously while Kobweb snapshots each page. The browser does not fetch this data after the site has loaded.

This is what ensures the home page, skills, and projects pages export with complete HTML rather than `Loading...`.

## Articles

Articles are Markdown files in `src/jsMain/resources/markdown/articles/`. Each article needs front matter like this:

```yaml
---
nav-title: My Article
title: My Article Title
description: A short description for search and social previews.
keywords: kotlin,kobweb,web
date-created: 2026-01-01
date-modified: 2026-01-01
root: .layouts.ArticleLayout
routeOverride: /articles/my-article/index
---
```

`routeOverride` determines the public route. The sitemap task reads the article metadata to add article URLs and modification dates automatically.

## Deployment

GitHub Actions deploys to Cloudflare Pages when:

- A commit is pushed to `master`.
- The workflow is run manually.
- The weekly schedule runs every Monday at 06:00 UTC.

The weekly deployment refreshes the GitHub project snapshot before exporting and deploying the site.

The workflow uses caches for Gradle, Kobweb's Playwright browser, and the pinned Kobweb CLI archive. Deployment requires these GitHub repository secrets:

- `CF_API_TOKEN`
- `CF_ACCOUNT_ID`
- `GRADLE_ENCRYPTION_KEY`

## License

Licensed under the [GNU General Public License v3.0](LICENSE).
