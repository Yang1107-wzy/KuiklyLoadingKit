import java.util.Properties

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/gradle-plugins/")
        }
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

fun configuredAndroidSdk(): String? {
    val explicit = System.getenv("ANDROID_SDK_ROOT")
        ?: System.getenv("ANDROID_HOME")
    if (!explicit.isNullOrBlank() && file(explicit).isDirectory) {
        return explicit
    }

    val localProperties = file("local.properties")
    if (!localProperties.isFile) {
        return null
    }
    val configured = Properties().apply {
        localProperties.inputStream().use(::load)
    }.getProperty("sdk.dir")
    return configured?.takeIf { file(it).isDirectory }
}

val requestedAndroid = providers.gradleProperty("enableAndroid").orNull?.let { rawValue ->
    when (rawValue) {
        "true", "TRUE", "True" -> true
        "false", "FALSE", "False" -> false
        else -> error("enableAndroid must be either true or false, but was '$rawValue'")
    }
}
val androidSdkPath = configuredAndroidSdk()
val androidEnabled = requestedAndroid ?: !androidSdkPath.isNullOrBlank()

rootProject.name = "KuiklyLoadingKit"

include(":loading-kit")
include(":shared")
if (androidEnabled) {
    include(":androidApp")
} else {
    println(
        "Android modules are disabled because no Android SDK is configured. " +
            "Set ANDROID_SDK_ROOT, ANDROID_HOME, or sdk.dir in local.properties."
    )
}
