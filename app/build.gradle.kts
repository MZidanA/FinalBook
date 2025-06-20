plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.booksforall"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.booksforall"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Retrofit & Moshi
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // OkHttp Logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Lifecycle (ViewModel & LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    // Added for lifecycleScope in Activity, useful for coroutines tied to lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // OkHttp (already included by logging-interceptor, but explicitly good)
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.3") // for JavaNetCookieJar

    // UI Components: CardView, RecyclerView
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // Image Loading Library: Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.activity)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Fragment KTX (for easy fragment transactions and arguments)
    implementation("androidx.fragment:fragment-ktx:1.6.0")

    // Room Persistence Library (for local database storage)
    // Remember to add 'kapt' plugin at the top for Room annotation processing:
    // id 'kotlin-kapt' (instead of kotlin-parcelize or add both)
    // If you add kapt, change annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") to kapt("com.github.bumptech.glide:compiler:4.16.0")
    // implementation("androidx.room:room-runtime:2.6.1")
    // kapt("androidx.room:room-compiler:2.6.1")
    // implementation("androidx.room:room-ktx:2.6.1") // Kotlin Extensions and Coroutines support for Room

    // Paging Library (for efficiently loading large lists of data)
    // implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    // DataStore (for modern data storage, preferences or typed objects)
    // implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Hilt (Dependency Injection) - Requires adding Hilt plugin at the top:
    // id 'com.google.dagger.hilt.android' version '2.44' apply false in project-level build.gradle
    // id 'dagger.hilt.android.plugin' at app-level
    // implementation("com.google.dagger:hilt-android:2.49")
    // kapt("com.google.dagger:hilt-compiler:2.49")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Optional: Mockito for unit testing with mocks
    // testImplementation("org.mockito:mockito-core:4.8.0")
    // androidTestImplementation("org.mockito:mockito-android:4.8.0")
}