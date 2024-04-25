pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        maven {
            url = uri("https://mobile.maven.couchbase.com/maven2/dev/")
        }
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven {
            url = uri("https://mobile.maven.couchbase.com/maven2/dev/")
        }
        mavenCentral()
    }
}

rootProject.name = "QuizAppByCouchBase"
include(":app")