buildscript{
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath ("io.realm:realm-gradle-plugin:10.15.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}