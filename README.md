# Portfolio

Welcome to the source code of my personal portfolio website, [ayfri.com](https://ayfri.com).

## 🚀 Overview

This is a modern, fully-featured portfolio website showcasing my skills, projects, experiences, and blog articles. Built with **Kotlin** and **Compose for Web**, it combines the power of a modern web framework with type-safe, functional programming.

The website is built using [Kobweb](https://kobweb.varabyte.com/), a cutting-edge Kotlin web framework that provides:
- Type-safe DSLs for HTML and CSS
- Compose for Web components (functional UI)
- Static site generation capabilities
- Built-in routing and navigation
- Live reload development server

## 📋 Features

- **Responsive Design**: Fully responsive pages that work on all devices
- **Blog System**: Markdown-based articles with front matter metadata
- **Project Showcase**: Display of GitHub projects with integration via GitHub API
- **Skills Section**: Categorized technical skills and expertise
- **Experience Timeline**: Professional experience and internships
- **About & Contact**: Personal information and contact details
- **Static Export**: Generate optimized HTML files for deployment
- **SEO Optimized**: Structured metadata and schema markup for search engines
- **Syntax Highlighting**: Code blocks with language-specific highlighting (Prism.js)
- **Custom Theme**: Dark-themed design with smooth animations

## 🏗️ Project Structure

```
src/jsMain/
├── kotlin/io/github/ayfri/
│   ├── Main.kt                    # App entry point and initialization
│   ├── Style.kt                   # Global styles and themes
│   ├── animations.kt              # Animation utilities
│   ├── CodeTheme.kt               # Code syntax highlighting theme
│   ├── components/                # Reusable UI components
│   │   ├── Header.kt              # Navigation header
│   │   ├── Head.kt                # HTML head metadata
│   │   └── Tabs.kt                # Tab component
│   ├── pages/                     # Website pages
│   │   ├── Index.kt               # Home page
│   │   ├── AboutMe.kt             # About page
│   │   ├── Skills.kt              # Skills showcase
│   │   ├── Experiences.kt         # Experience timeline
│   │   ├── Portfolio.kt           # Portfolio grid
│   │   └── projects/              # Project pages
│   ├── layouts/                   # Layout components (ArticleLayout, etc.)
│   ├── jsonld/                    # JSON-LD schema markup
│   └── utils/                     # Utility functions and helpers
│
└── resources/
    ├── markdown/articles/         # Blog articles in Markdown
    │   ├── DatapackGenerators.md
    │   ├── KoreHelloWorld.md
    │   ├── KoreIntroduction.md
    │   └── ... (more articles)
    └── public/                    # Static files
        ├── cv.pdf                 # Resume/CV
        ├── logo.png               # Site logo
        ├── prism.min.js           # Code highlighting
        └── images/                # Static images
```

## 🔧 Prerequisites

- **JDK 21+** (Java Development Kit)
- **Gradle** (included via Gradle Wrapper)
- A terminal/shell with access to `./gradlew` commands

## 🎯 Getting Started

### Development Server

To run the development server with live reload:

```powershell
./gradlew kobwebStart -t
```

The website will be available at `http://localhost:8080` and will automatically reload on file changes.

### Additional Build Options

- **Production Mode**: Add `-PkobwebEnv=PROD` for optimized production build
- **Static Mode**: Add `-PkobwebRunLayout=STATIC` for HTML generation (useful for static exports)
- **Live Reload**: `-t` flag enables automatic page reload on code changes

Example with options:
```powershell
./gradlew kobwebStart -t -PkobwebEnv=PROD
```

### Exporting the Website

To generate optimized static HTML files:

```powershell
./gradlew kobwebExport
```

Export options:
- **Production Mode**: `-PkobwebEnv=PROD` for optimized production build
- **Static Mode**: `-PkobwebRunLayout=STATIC` for full HTML generation

The exported website will be available in the `.kobweb/client` directory.

## 📝 Blog Articles

Articles are stored in `src/jsMain/resources/markdown/articles/` as Markdown files with YAML front matter.

### Creating New Articles

1. Create a new `.md` file in `src/jsMain/resources/markdown/articles/`
2. Add front matter at the top of the file
3. Write your article content in Markdown below the front matter

### Front Matter Format

```yaml
---
nav-title: Article URL Slug
title: Display Title of the Article
description: Short description for SEO and previews (50-160 characters recommended)
keywords: comma,separated,keywords,for,seo
date-created: YYYY-MM-DD
date-modified: YYYY-MM-DD
root: .layouts.ArticleLayout
routeOverride: /articles/article-url-slug/index
---

# Article content starts here
```

### Front Matter Fields

| Field           | Purpose                                                |
|-----------------|--------------------------------------------------------|
| `nav-title`     | URL slug for the article (e.g., `kore-hello-world`)    |
| `title`         | Display title shown in article lists and headers       |
| `description`   | SEO description and preview text                       |
| `keywords`      | Comma-separated keywords for search engines            |
| `date-created`  | Article creation date in YYYY-MM-DD format             |
| `date-modified` | Last modification date in YYYY-MM-DD format            |
| `root`          | Layout component (usually `.layouts.ArticleLayout`)    |
| `routeOverride` | Full URL path for the article (`/articles/slug/index`) |

### Article Features

- **Markdown Formatting**: Full Markdown support (headings, lists, tables, emphasis, etc.)
- **Code Blocks**: Syntax highlighting via Prism.js
- **Images**: Support for local and external images
- **Links**: Internal and external links with proper routing
- **SEO**: Automatic meta tags and JSON-LD schema

## 🔗 Data Integration

The website integrates with an external GitHub API for dynamic project data:

- GitHub projects are fetched from a hosted JSON API
- Project data includes stars, forks, and descriptions
- The data is embedded during the build process in `build/generated/ayfri/src/jsMain/kotlin/io/github/ayfri/data/Data.kt`
- API endpoint: `https://raw.githubusercontent.com/Ayfri/Portfolio/api/result.json`

Data download happens automatically during the build via the `downloadData` Gradle task.

## 🏗️ Technologies Used

- **Kotlin 2.2.21**: Type-safe language for web development
- **Compose for Web 1.9.1**: Functional UI framework
- **Kobweb 0.23.3**: Full-stack Kotlin web framework
- **Markdown Processing**: KobwebX Markdown plugin for article conversion
- **HTML/CSS**: Compose HTML and CSS DSLs for styling

## 📦 Build System

The project uses **Gradle** with several custom tasks:

- `kobwebStart`: Start development server with live reload
- `kobwebExport`: Export static HTML files
- `downloadData`: Download and process GitHub project data

## 🎨 Customization

### Styling

Global styles are defined in `Style.kt`. The site uses:
- CSS-in-Code via Compose DSL
- Dark theme with accent colors
- Custom scrollbar styling
- Responsive media queries

### Components

Reusable components are in `components/`:
- `Header.kt`: Navigation and branding
- `Head.kt`: Metadata and SEO tags
- Custom components for portfolio sections

### Layouts

Page layouts are in `layouts/`:
- `ArticleLayout`: Template for blog articles
- `PageLayout`: General page wrapper with header/footer

## 🚀 Deployment

The website can be deployed anywhere that serves static files:

1. Run `./gradlew kobwebExport -PkobwebEnv=PROD`
2. Upload contents of `.kobweb/client` to your hosting
3. Ensure your server is configured for SPA routing (redirect 404s to index.html)

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
