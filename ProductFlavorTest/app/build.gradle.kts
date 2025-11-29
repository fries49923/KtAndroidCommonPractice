plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.productflavortest"
    compileSdk = 36

    // 定義 Flavor 維度，必須先宣告才能在 productFlavors 使用
    flavorDimensions += "env"

    defaultConfig {
        applicationId = "com.example.productflavortest"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    productFlavors {
        // 開發環境 (dev)
        create("dev") {
            // 指定此 flavor 屬於 env 維度
            dimension = "env"

            // 在正式 applicationId 後加上 .dev，避免與 prod 衝突
            applicationIdSuffix = ".dev"

            // 在版本名稱後加上 -dev，方便辨識
            versionNameSuffix = "-dev"
            buildConfigField(
                "String",
                "API_URL",
                "\"https://api.dev.example.com\""
            ) // 在 BuildConfig 中注入 API_URL 常數
        }

        // 正式環境 (prod)
        create("prod") {
            dimension = "env"

            // prod 通常不加 suffix，保持正式版乾淨
            buildConfigField(
                "String",
                "API_URL",
                "\"https://api.example.com\""
            ) // 正式版使用正式 API endpoint
        }
    }

    // 開啟 BuildConfig 功能，以使用 productFlavors
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}