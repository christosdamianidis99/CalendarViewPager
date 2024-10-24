// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.1")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
}
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}