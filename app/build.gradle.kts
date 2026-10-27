plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.hilt)
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose.compiler)
}

// IDs do AdMob. Nao sao segredos (vao dentro do APK). O App ID e o mesmo em todos os builds;
// os IDs de bloco abaixo so valem no release, o debug usa sempre os blocos de teste do Google.
val admobAppId = "ca-app-pub-8136331239459607~3825484269"
val admobBannerId = "ca-app-pub-8136331239459607/8582986927"
val admobInterstitialId = "ca-app-pub-8136331239459607/8709437444"
val admobRewardedId = ""

android {
    namespace = "com.kaajjo.libresudoku"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.vieira85lucas.sudokubr"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "${projectDir}/schemas")
        }

        manifestPlaceholders["admobAppId"] = admobAppId
    }

    // F-Droid
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    buildTypes {
        debug {
            buildConfigField("String", "ADMOB_BANNER_ID", "\"ca-app-pub-3940256099942544/9214589741\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "ADMOB_REWARDED_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        release {
            buildConfigField("String", "ADMOB_BANNER_ID", "\"$admobBannerId\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"$admobInterstitialId\"")
            buildConfigField("String", "ADMOB_REWARDED_ID", "\"$admobRewardedId\"")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    sourceSets.getByName("androidTest").assets.srcDir("$projectDir/schemas")
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Impede publicar um release com IDs de bloco em branco (anuncios nao carregariam).
tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    doFirst {
        check(listOf(admobBannerId, admobInterstitialId, admobRewardedId).all { it.isNotBlank() }) {
            "Preencha admobBannerId, admobInterstitialId e admobRewardedId em app/build.gradle.kts antes do build de release."
        }
    }
}

aboutLibraries {
    // Remove the "generated" timestamp to allow for reproducible builds
    excludeFields = arrayOf("generated")
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)
    implementation(libs.ui)
    implementation(libs.ui.util)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(project(":Color-Picker"))
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation(libs.kxml2)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.test.core)
    testImplementation("io.mockk:mockk:1.13.13")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation("androidx.test:runner:1.6.2")
    implementation(libs.graphics.shape)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.pager.indicators)

    implementation(libs.hilt)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.datastore.preferences)

    implementation(libs.appcompat)

    implementation(libs.aboutLibraries)

    implementation(libs.compose.destinations)
    ksp(libs.compose.destinations.ksp)
    implementation(libs.navigation.compose)

    implementation(libs.serialization.json)
    implementation(libs.documentFile)
    implementation(libs.workRuntimeKtx)
    implementation(libs.hilt.work)
    implementation(libs.hilt.common)
    ksp(libs.hilt.common.compiler)
    ksp(libs.hilt.work)
    implementation(libs.materialKolor)

    implementation(libs.play.services.ads)
    implementation(libs.ump)

}
