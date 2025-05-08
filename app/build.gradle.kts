plugins {
    id("com.android.application")
}

android {
    namespace = "com.longx.intelligent.android.imessage"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.longx.intelligent.android.imessage"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("releaseConfig") {
            keyAlias = "LONG"
            keyPassword = "19990619"
            storeFile = file("/Users/longtianxiang/Code/其他/Android Signature/FINAL_RELEASE_KEY_STORE.jks")
            storePassword = "19990619"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("releaseConfig")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("releaseConfig")
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
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.github.xchengDroid:retrofit-helper:3.2.4")
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    implementation(project(":material-you-preference"))
    implementation("cn.zhxu:okhttps-stomp:4.0.2")
    implementation("cn.hutool:hutool-all:5.8.11")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-io:commons-io:2.16.0")
    implementation("commons-codec:commons-codec:1.16.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.davemorrissey.labs:subsampling-scale-image-view-androidx:3.10.0")
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation(project(":recycler-view-enhance"))
    implementation("com.belerweb:pinyin4j:2.5.1")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.12.0")
    implementation("q.rorbin:badgeview:1.1.3")
    implementation("org.apache.tika:tika-core:1.0")
    implementation("com.google.android.exoplayer:exoplayer:2.18.0")
    implementation("com.nex3z:flow-layout:1.3.3")

}