import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}

// Leer credenciales de Gmail desde local.properties o variables de entorno
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

val gmailUsername = localProperties.getProperty("gmail.username")
    ?: System.getenv("GMAIL_USERNAME")
    ?: "default@gmail.com"

val gmailAppPassword = localProperties.getProperty("gmail.app.password")
    ?: System.getenv("GMAIL_APP_PASSWORD")
    ?: "defaultpassword"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("androidx.navigation:navigation-compose:2.7.7")
            implementation(libs.ktor.client.cio)

            // HTTP simple para subida de archivos
            implementation("com.squareup.okhttp3:okhttp:4.11.0")
            implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

            // PDF Generation
            implementation("com.itextpdf:itext7-core:7.2.5")

            // Email sending
            implementation("com.sun.mail:android-mail:1.6.7")
            implementation("com.sun.mail:android-activation:1.6.7")

            // Coil para carga de imágenes
            implementation("io.coil-kt:coil-compose:2.4.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            // EmailJS para envío de emails
            implementation(npm("emailjs-com", "^4.3.2"))
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)

            // HTTP Client dependencies
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.marimon.sigc"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.marimon.sigc"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        // BuildConfig fields para credenciales de email
        buildConfigField("String", "GMAIL_USERNAME", "\"$gmailUsername\"")
        buildConfigField("String", "GMAIL_APP_PASSWORD", "\"$gmailAppPassword\"")
    }

    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/NOTICE"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    // Dependencias Firebase comentadas temporalmente
    // implementation("com.google.firebase:firebase-storage-ktx:20.2.1")
    // implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    // implementation("com.google.firebase:firebase-common-ktx:20.4.0")
}
