plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.mycalendar"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.mycalendar"
        minSdk = 16
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.test:monitor:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val multidex_version = "2.0.1"
    implementation("androidx.multidex:multidex:$multidex_version")

    // Rounded Image View
    implementation("com.makeramen:roundedimageview:2.3.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0-beta01")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0-beta01")
    implementation("androidx.work:work-runtime:2.8.1")

    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
}

configurations.getByName("implementation") {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
}
