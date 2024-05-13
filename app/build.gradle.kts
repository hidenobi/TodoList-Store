import org.jetbrains.kotlin.konan.properties.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.dagger)
//    alias(libs.plugins.ksp)
    kotlin("kapt")
    alias(libs.plugins.deploygate)
    alias(libs.plugins.parcelize)
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}

deploygate {
//    appOwnerName = "tuanpd"
//    apiToken = "5da2cb99-ec96-4bc5-92a6-389ff3686945"
    appOwnerName = "cuongtc"
    apiToken = "deploygate_xusr_OBp48s5vh3p3eCwr9HOkFgVAcUqlr1_1kzD6Z"
}

android {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keyfile = readProperties(keystorePropertiesFile)

    namespace = "com.padi.todo_list"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.padi.todo_list"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // We use a bundled debug keystore, to allow debug builds from CI to be upgradable
        getByName("debug") {
            keyAlias = keyfile.getProperty("keyAlias")
            keyPassword = keyfile.getProperty("keyPassword")
            storeFile = rootProject.file(keyfile.getProperty("storeFile"))
            storePassword = keyfile.getProperty("storePassword")
        }
        create("release") {
            keyAlias = keyfile.getProperty("keyAlias")
            keyPassword = keyfile.getProperty("keyPassword")
            storeFile = rootProject.file(keyfile.getProperty("storeFile"))
            storePassword = keyfile.getProperty("storePassword")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.rxAndroid)
    implementation(libs.rxKotlin)

    kapt(libs.glide.compiler)
    implementation (libs.androidx.work.runtime)
    implementation(libs.androidx.work.rxjava3)
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.dagger.compiler)

    implementation(libs.dagger)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.androidx.activity)
    annotationProcessor(libs.androidx.room.compiler)
//    ksp(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.rxjava3)

//    ksp(libs.dagger.compiler)
    implementation(libs.gson)
    implementation(libs.deploygate)

    implementation(libs.timber)
    implementation(libs.sdp)
    implementation(libs.ssp)
    implementation (libs.glide)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.customcalendarview)
    implementation (libs.number.picker)

    implementation (libs.kizitonwose.calendar.view)
    implementation (libs.philjay.mpAndroidChart)
    implementation (libs.viewpagerindicator)
    implementation (libs.simpleratingbar)
    implementation (libs.tedimagepicker)
    implementation (libs.lottie)
}

kapt {
    correctErrorTypes = true
}