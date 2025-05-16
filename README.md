# Portfolio Back API

This repository serves as the backend API for [Ayfri\'s Portfolio](https://ayfri.com/). It fetches and processes data from GitHub to be displayed on the portfolio website.

## Overview

The main purpose of this project is to gather information about Ayfri\'s GitHub user profile and repositories. This data is then consolidated into a `result.json` file, which the portfolio frontend consumes to display up-to-date project information, statistics, and activity.

The project is built using Kotlin and leverages the following key technologies:

*   **Ktor**: For making HTTP requests to the GitHub API.
*   **Kotlinx Serialization**: For parsing JSON responses from the GitHub API and serializing the final `result.json` file.

## Project Structure

*   `src/main/kotlin/`: Contains the Kotlin source code.
    *   `entities/`: Defines the data classes (`User.kt`, `Repository.kt`, `Result.kt`) that model the structure of the data retrieved from GitHub and the final `result.json`.
    *   `Main.kt`: The main entry point of the application, responsible for orchestrating the data fetching and processing.
    *   `GitHubAPI.kt`: (Assumed based on usage in `Repository.kt`) Likely contains the logic for interacting with the GitHub API.
*   `build.gradle.kts`: The Gradle build script defining project dependencies and build configurations.
*   `result.json`: The output file containing the processed data for the portfolio.

## How it Works

1.  The application starts by executing the `main` function in `MainKt`.
2.  It makes requests to the GitHub API to fetch:
    *   User information for the user "Ayfri".
    *   A list of repositories belonging to "Ayfri" and relevant organizations.
3.  For each repository, it may fetch additional details like:
    *   README content.
    *   Commit count.
    *   Contributor count.
    *   Watcher/subscriber count.
4.  The fetched data is then transformed and structured according to the `ResultUser` and `ResultRepository` data classes defined in `src/main/kotlin/entities/Result.kt`.
5.  Finally, the consolidated data is serialized into the `result.json` file.

## Usage

To run this project:

1.  Ensure you have a JDK and Gradle installed.
2.  A GitHub token might be required for API calls, typically set as an environment variable or in a configuration file (the exact mechanism would be in `GitHubAPI.kt` or `Main.kt`).
3.  Execute the Gradle `run` task:
    ```bash
    ./gradlew run
    ```
    Or on Windows:
    ```bash
    gradlew.bat run
    ```
4.  This will generate/update the `result.json` file.

The portfolio frontend then fetches this `result.json` to display the information.

## Contributing

Please refer to the main portfolio project for contribution guidelines if you wish to contribute to the overall portfolio. For this backend specifically, ensure any changes to data fetching or processing are reflected in the `result.json` structure as expected by the frontend.