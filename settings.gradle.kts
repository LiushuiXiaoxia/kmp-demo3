rootProject.name = "Demo03"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val aliyunGoogle = "https://maven.aliyun.com/repository/google"
val aliyunPublic = "https://maven.aliyun.com/repository/public"
val aliyunGradlePlugin = "https://maven.aliyun.com/repository/gradle-plugin"

pluginManagement {
    repositories {
        maven(url = aliyunGoogle) {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven(url = aliyunPublic)
        maven(url = aliyunGradlePlugin)
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url = aliyunGoogle) {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven(url = aliyunPublic)
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
