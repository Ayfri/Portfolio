# Portfolio

<p align="center">
	<a href="https://ayfri.com"><img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fayfri.com&label=ayfri.com&style=flat-square"></a>
	<a href="https://github.com/Ayfri/Portfolio/actions/workflows/CD.yml"><img alt="CD" src="https://img.shields.io/github/actions/workflow/status/Ayfri/Portfolio/CD.yml?label=CD&style=flat-square"></a>
	<a href="https://kotlinlang.org/"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.4.21-7F52FF?logo=kotlin&logoColor=white&style=flat-square"></a>
	<a href="https://kobweb.varabyte.com/"><img alt="Kobweb" src="https://img.shields.io/badge/Kobweb-0.25.1-3EE6F0?style=flat-square"></a>
</p>

The source of [ayfri.com](https://ayfri.com), the portfolio and blog of [Pierre Roy](https://github.com/Ayfri) (Ayfri).

Pages, styles and build pipeline are all Kotlin, compiled to JavaScript with [Kobweb](https://kobweb.varabyte.com/) and Compose HTML. The export is static HTML for every page, project pages included, so a crawler reads the final content without running a script.

## Stack

| Part | Choice |
|------|--------|
| Language | Kotlin 2.4.21, Kotlin/JS targeting ES2015 |
| UI | Compose HTML 1.12.2, Kobweb 0.25.1, Lucide icons |
| Articles | Markdown through KobwebX Markdown, Prism for code blocks |
| Build | Gradle 9.9.1 through the wrapper, JDK 25, npm packages locked in `kotlin-js-store/package-lock.json` |
| Bundler | esbuild 0.28.2 |
| Hosting | Cloudflare Pages |

## Pages

| Route | Content |
|-------|---------|
| `/` | Hero, featured projects, top skills, experience timeline, latest articles |
| `/about-me/` | The story, year by year |
| `/skills/` | Languages and tools, each with the GitHub projects using it |
| `/experiences/` | Roles on a timeline |
| `/portfolio/` | This site itself: its story, screenshots and stack |
| `/projects/` | Every public GitHub repository, with search and filters |
| `/projects/{user}/{project}/` | One page per repository, with its README |
| `/articles/` and `/articles/{slug}/` | The blog |

## Run it

JDK 25 is the only requirement, the Gradle wrapper downloads the rest.

```powershell
# Dev server on http://localhost:8080, rebuilt on every change
.\gradlew.bat kobwebStart -t

# Stop it
.\gradlew.bat kobwebStop
```

## Export

```powershell
.\gradlew.bat kobwebExport -PkobwebExportLayout=STATIC -PkobwebBuildTarget=RELEASE -PkobwebReuseServer=false
.\gradlew.bat kobwebStop
```

The site lands in `.kobweb/site/`. The export leaves its server running on port 8080, and the next export refuses to start while it is up, hence `kobwebStop`. `kobweb export --layout static` from the [Kobweb CLI](https://github.com/varabyte/kobweb-cli) runs both tasks.

The export downloads the project snapshot, then snapshots every page in a headless Chromium: the static pages, the articles and one page per project. It fails when a project page is missing, since Kobweb only logs a page that timed out.

## Project data

The [`api`](https://github.com/Ayfri/Portfolio/tree/api) branch holds a snapshot of the GitHub repositories, refreshed every week:

```text
https://raw.githubusercontent.com/Ayfri/Portfolio/api/result.json
```

`downloadData` converts its keys to camelCase and writes it as a Kotlin source, `PortfolioSnapshot.kt`, so the browser parses it with one `JSON.parse` and never calls the GitHub API.

- Dev builds keep the snapshot they have. `.\gradlew.bat downloadData --rerun` refreshes it.
- The export always downloads it, at configuration time, because the project page routes come from it.

## Articles

Articles are Markdown files in `src/jsMain/resources/markdown/articles/`, each starting with this front matter:

```yaml
---
nav-title: My Article
title: My Article Title
description: A short description for search and social previews.
keywords: kotlin, kobweb, web
date-created: 2026-01-01
date-modified: 2026-01-01
root: .layouts.ArticleLayout
routeOverride: /articles/my-article/index
---
```

`routeOverride` is the public route. The build fails without `title`, `description` or `routeOverride`, or with a date that is not `yyyy-MM-dd`. An article missing a date or `nav-title` is left out of the article list, and `keywords` is optional.

## Generated files

The build writes these next to the pages, from the same sources:

| File | Content |
|------|---------|
| `/sitemap.xml` | Static pages, articles with their modification date, and project pages in an export |
| `/feed.xml` | RSS 2.0 feed of the articles |
| `/llms.txt`, `/llms-full.txt` | The site summary and every article in one file, for agents |
| `/articles/{slug}/index.md` | A Markdown copy of each article |

## Build notes

- esbuild bundles the modules the Kotlin compiler links (`bundleDevelopmentSite`, `bundleProductionSite`), in place of webpack, whose tasks stay as disabled shells because Kobweb depends on them by name.
- `kspKotlinJs`, the task Kobweb uses to find pages, is skipped while no declaration line changed. `.\gradlew.bat compileKotlinJs '-Pportfolio.ksp'` forces it, quoted in PowerShell.
- A new npm dependency needs `.\gradlew.bat kotlinUpgradePackageLock`, the CI fails on a lock file that differs.

## Deployment

[`CD.yml`](.github/workflows/CD.yml) exports the site and deploys `.kobweb/site/` to Cloudflare Pages:

- on every push to `master`,
- every Monday at 06:00 UTC,
- on a manual run.

[`data-cron.yml`](.github/workflows/data-cron.yml) starts the Data workflow of the `api` branch every Monday at 00:17 UTC, so the scheduled deployment exports a fresh snapshot.

Repository secrets: `CF_API_TOKEN`, `CF_ACCOUNT_ID` and `GRADLE_ENCRYPTION_KEY`.

## License

[GNU General Public License v3.0](LICENSE).
