# Portfolio Data API

The `api` branch generates the GitHub project snapshot consumed by [ayfri.com](https://ayfri.com/).

## Stack

- Kotlin 2.4.21
- Ktor HTTP client
- Kotlinx Serialization
- Gradle 9.9 wrapper
- JDK 25 toolchain, also running the Gradle daemon (`gradle/gradle-daemon-jvm.properties`), so `gradlew` starts whatever `JAVA_HOME` points to

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

A request GitHub refuses (rate limit, invalid token, server error after 3 retries) stops the run before anything is written, so the existing snapshot stays in place.

## Automation

`.github/workflows/data.yml` runs the Gradle application with GitHub Actions' built-in `GITHUB_TOKEN` and commits only `result.json`. It only has a manual trigger, because GitHub fires `schedule` from the default branch only.

`.github/workflows/data-cron.yml` on `master` holds the schedule: every Monday at 00:17 UTC it starts `data.yml` on `api`, about 6 hours before the weekly site deployment that reads the snapshot.

A run makes 4 requests per repository. The built-in token allows 1,000 requests per hour, which covers about 249 repositories.
