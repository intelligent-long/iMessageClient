pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://maven.aliyun.com/repository/central")}
        maven { url = uri("https://maven.aliyun.com/repository/google")}
        maven { url = uri("https://maven.aliyun.com/repository/public")}
        maven { url = uri("https://jitpack.io")}
    }
}

rootProject.name = "iMessage Client"
include(":app")
include(":keep-alive")
include(":material-you-preference")
include(":recycler-view-enhance")
