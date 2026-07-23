import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.cocoapods) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
}

fun configuredAndroidSdk(): String? {
    val explicit = providers.gradleProperty("androidSdkPath").orNull
        ?: System.getenv("ANDROID_SDK_ROOT")
        ?: System.getenv("ANDROID_HOME")
    if (!explicit.isNullOrBlank()) {
        return explicit
    }

    val localProperties = file("local.properties")
    if (!localProperties.isFile) {
        return null
    }
    return Properties().apply {
        localProperties.inputStream().use(::load)
    }.getProperty("sdk.dir")
}

val requestedAndroid = providers.gradleProperty("enableAndroid").orNull?.let { rawValue ->
    when (rawValue) {
        "true", "TRUE", "True" -> true
        "false", "FALSE", "False" -> false
        else -> error("enableAndroid must be either true or false, but was '$rawValue'")
    }
}

extra["kuiklyAndroidEnabled"] =
    requestedAndroid ?: !configuredAndroidSdk().isNullOrBlank()

allprojects {
    group = "io.github.yang1107"
    version = "0.1.0-SNAPSHOT"
}
