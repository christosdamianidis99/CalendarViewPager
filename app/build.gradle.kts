plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.calendarviewpager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.calendarviewpager"
        minSdk = 24
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.rxjava)

    implementation (libs.rxbinding.appcompat.v7)

    implementation (libs.multidex)
    implementation (libs.threetenabp.v147)
    implementation (libs.play.services.oss.licenses)
    coreLibraryDesugaring(libs.desugar.jdk.libs.v203)
}