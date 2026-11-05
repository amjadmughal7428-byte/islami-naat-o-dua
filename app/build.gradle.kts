plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.islamicstories.naatdua"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.islamicstories.naatdua"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    val ksProps = java.util.Properties()
    val ksFile = rootProject.file("keystore.properties")
    if (ksFile.exists()) {
        ksFile.inputStream().use { ksProps.load(it) }
    }
    signingConfigs {
        create("release") {
            storeFile = file(ksProps.getProperty("storeFile", "../upload-keystore.jks"))
            storePassword = ksProps.getProperty("storePassword", "")
            keyAlias = ksProps.getProperty("keyAlias", "")
            keyPassword = ksProps.getProperty("keyPassword", "")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            // debug uses default debug key
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
