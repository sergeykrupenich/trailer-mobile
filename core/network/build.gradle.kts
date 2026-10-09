import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val bearerToken = localProperties.getProperty("BEARER_TOKEN") 
    ?: System.getenv("BEARER_TOKEN") 
    ?: ""

val formattedBearerToken = if (bearerToken.startsWith("\"") && bearerToken.endsWith("\"")) {
    bearerToken
} else {
    "\"$bearerToken\""
}

android {
    namespace = "com.siarheikrupenich.trailermobile.core.network"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "BEARER_TOKEN", formattedBearerToken)
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "BEARER_TOKEN", formattedBearerToken)
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.timber)

    // Network
    implementation(libs.retrofit)
    implementation(libs.okHttp)
    implementation(libs.retrofit.converterGson)
    implementation(libs.loggingInterceptor)

    // DI
    implementation(libs.hilt)
    kapt(libs.hilt.kapt)
}

kapt {
    correctErrorTypes = true
}
