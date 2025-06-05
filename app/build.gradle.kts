plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.syalux.quizapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.syalux.quizapp"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Core AndroidX libraries
    implementation(libs.appcompat)
    implementation(libs.activity)

    // UI components
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
