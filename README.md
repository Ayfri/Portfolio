# Portfolio

This is the source code of my Portfolio, [ayfri.com](https://ayfri.com).

The website is made with [Kobweb](https://kobweb.varabyte.com/), a Kotlin Web Framework made on top of Compose HTML with static site generation.

## How to run

To run the website, you need to have the JDK installed on your computer.

Then, you can run the following command:

```shell
./gradlew kobwebStart
```

Add `-t` for live reload.
Add `-PkobwebEnv=PROD` to run in production mode.
Add `-PkobwebRunLayout=STATIC` to run in static mode (HTML generation, slower to live reload).

### Exporting the website

To export the website, you can run the following command:

```shell
./gradlew kobwebExport
```

Add `-PkobwebEnv=PROD` to export in production mode.
Add `-PkobwebRunLayout=STATIC` to export in static mode (HTML generation).

The website will be exported in the `.kobweb/client` directory.

## Articles

The articles are written in `src/jsMain/resources/markdown/articles` in Markdown format with a front matter.
During the build, the articles are converted to HTML and added to the website.

### Front matter

Here's an example of a front matter:

```yaml
nav-title: Kore Hello World
title: Creating an Hello World Datapack with Kore
description: Learn how to create a simple Minecraft datapack that displays a "Hello World" message using the Kore library.
keywords: minecraft, datapack, kore, kotlin, tutorial
date-created: 2024-05-19
date-modified: 2024-05-19
root: .layouts.ArticleLayout
routeOverride: /articles/kore-hello-world/index
```

- `nav-title` is the slug of the page and what will be displayed for SEO.
- `title` is the title of the article that will be displayed in the article list.
- `description` is the description of the article that will be used everywhere.
- `keywords` are the keywords of the article that will be used for SEO.
- `date-created` is the date the article was created.
- `date-modified` is the date the article was last modified.
- `root` is the layout of the article, usually `.layouts.ArticleLayout` (which means the article will be displayed with the `src/jsMain/kotlin/layouts/DocLayout.kt` layout).
- `routeOverride` is the route of the article, it's the URL of the article.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
