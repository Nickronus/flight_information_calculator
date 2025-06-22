plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "nickronus.flight_information_calculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "nickronus.flight_information_calculator"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Добавьте эти новые зависимости
    implementation("androidx.sqlite:sqlite:2.3.1") // Для работы с SQLite
    implementation("androidx.sqlite:sqlite-ktx:2.3.1") // Kotlin расширения (если нужно)
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6") // Для LocalDateTime
}