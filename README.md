# Portfolio Data API

The `api` branch generates the GitHub project snapshot consumed by [ayfri.com](https://ayfri.com/).

## Stack

- Kotlin 2.4.0
- Ktor HTTP client
- Kotlinx Serialization
- Gradle 9.6 wrapper
- JDK 25 toolchain

## Output

The application fetches the public GitHub profile and repositories for `Ayfri`, enriches each repository with README and aggregate API data, then writes the compact snapshot to `result.json`.

The frontend downloads this file from:

```text
https://raw.githubusercontent.com/Ayfri/Portfolio/api/result.json
```

## Run Locally

Set a GitHub token with access to the required public API endpoints, then run:

```powershell
$env:GITHUB_TOKEN = "github_pat_..."
.\gradlew.bat run
```

The token is required and is sent as a bearer token. The generated `result.json` replaces the existing snapshot.

## Automation

`.github/workflows/data.yml` refreshes the snapshot every Monday and can also be started manually. It runs the Gradle application with GitHub Actions' built-in `GITHUB_TOKEN` and commits only `result.json`.
