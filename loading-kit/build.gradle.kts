import com.android.build.gradle.LibraryExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

val androidEnabled = rootProject.extra["kuiklyAndroidEnabled"] as Boolean
if (androidEnabled) {
    apply(plugin = "com.android.library")
}

kotlin {
    jvm("desktop")

    if (androidEnabled) {
        androidTarget {
            publishLibraryVariants("release")
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting
        val kuiklyMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(libs.kuikly.core)
                api(libs.kuikly.core.annotations)
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        val iosX64Main by getting {
            dependsOn(kuiklyMain)
        }
        val iosArm64Main by getting {
            dependsOn(kuiklyMain)
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(kuiklyMain)
        }
        if (androidEnabled) {
            val androidMain by getting {
                dependsOn(kuiklyMain)
            }
        }
    }
}

// Pure request/controller tests run on JVM. A standalone Kotlin/Native test
// executable cannot link Kuikly core without the native render host that owns
// com_tencent_kuikly_IsCurrentOnContextThread. Keep the aggregate task honest
// by skipping only those unsupported native executables; iOS compatibility is
// verified separately by compiling and linking the real CocoaPods framework.
val unsupportedNativeTestTasks = setOf(
    "iosSimulatorArm64Test",
    "iosX64Test",
    "linkDebugTestIosArm64",
    "linkDebugTestIosSimulatorArm64",
    "linkDebugTestIosX64",
)
tasks.matching { it.name in unsupportedNativeTestTasks }.configureEach {
    enabled = false
}

if (androidEnabled) {
    extensions.configure<LibraryExtension> {
        namespace = "io.github.yang1107.kuikly.loading"
        compileSdk = 34
        defaultConfig {
            minSdk = 21
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
}
