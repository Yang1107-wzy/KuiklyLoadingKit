import com.android.build.gradle.LibraryExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.ksp)
}

val androidEnabled = rootProject.extra["kuiklyAndroidEnabled"] as Boolean
if (androidEnabled) {
    apply(plugin = "com.android.library")
}

kotlin {
    if (androidEnabled) {
        androidTarget()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    )

    cocoapods {
        summary = "Interactive iOS host framework for KuiklyLoadingKit"
        homepage = "https://github.com/Yang1107-wzy/KuiklyLoadingKit"
        version = "0.1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "KuiklyLoadingShared"
            isStatic = true
            export(project(":loading-kit"))
            freeCompilerArgs += "-Xbinary=bundleId=io.github.yang1107.KuiklyLoadingShared"
        }
        license = "Apache-2.0"
        authors = "Yang1107-wzy"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":loading-kit"))
            implementation(libs.kuikly.core)
            implementation(libs.kuikly.core.annotations)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        if (androidEnabled) {
            androidMain.dependencies {
                api(libs.kuikly.core.render.android)
            }
        }
    }
}

ksp {
    arg("pageName", "LoadingGalleryPage")
}

dependencies {
    add("kspIosX64", libs.kuikly.core.ksp)
    add("kspIosArm64", libs.kuikly.core.ksp)
    add("kspIosSimulatorArm64", libs.kuikly.core.ksp)
    if (androidEnabled) {
        add("kspAndroid", libs.kuikly.core.ksp)
    }
}

if (androidEnabled) {
    extensions.configure<LibraryExtension> {
        namespace = "io.github.yang1107.kuikly.loading.demo.shared"
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
