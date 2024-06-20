plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.transitready"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.transitready"
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:+")
    implementation("androidx.databinding:compiler:3.2.0-alpha11")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation ("org.apache.commons:commons-csv:1.10.0")

    implementation ("io.reactivex.rxjava3:rxjava:3.1.8") // replace with the latest version
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.2") // replace with the latest version
    implementation ("androidx.room:room-rxjava3:2.6.1")

    implementation ("com.android.volley:volley:1.2.1")
    configurations.all {
        resolutionStrategy {
            force ("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
        }
    }

}