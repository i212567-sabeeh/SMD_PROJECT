plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.leaguex.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.leaguex.app"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Robolectric needs the merged resources and manifest to inflate layouts in unit tests.
    // Its native runtime calls System.load, which JDK 25 warns about unless native access is enabled.
    // FrameShotsTest renders screenshots only when Gradle gets -Pshots=<section slug|all|kit>; they go to LeagueX/shots.
    val shots = providers.gradleProperty("shots").getOrElse("")
    val shotsDir = rootProject.layout.projectDirectory.dir("shots").asFile.absolutePath
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.jvmArgs("--enable-native-access=ALL-UNNAMED")
                // Every frame test opens dozens of screens; the default heap runs out once all sections exist, and
                // Robolectric keeps some of each test's state for the life of its JVM, so a fresh JVM takes over
                // every five test classes.
                it.maxHeapSize = "2g"
                it.forkEvery = 5
                it.systemProperty("leaguex.shots", shots)
                it.systemProperty("leaguex.shotsDir", shotsDir)
                if (shots.isNotEmpty()) it.outputs.upToDateWhen { false }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
