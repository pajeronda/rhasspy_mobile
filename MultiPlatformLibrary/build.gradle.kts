import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.codingfeline.buildkonfig")
}

version = "0.2"

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "MultiPlatformLibrary"
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {

        all {
            //Warning: This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("stdlib"))
                implementation("co.touchlab:kermit:_")
                implementation(Icerock.Mvvm.core)
                implementation(Icerock.Mvvm.state)
                implementation(Icerock.Mvvm.livedata)
                implementation(Icerock.Mvvm.livedataResources)
                runtimeOnly(Icerock.permissions)
                implementation(Icerock.Resources)
                implementation(Russhwolf.multiplatformSettings)
                implementation(Russhwolf.multiplatformSettingsNoArg)
                implementation(Russhwolf.multiplatformSettingsSerialization)
                implementation(Jetbrains.Kotlinx.dateTime)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Ktor.Client.core)
                implementation(Ktor.Client.websockets)
                implementation(Ktor.Network.network)
                implementation(Ktor2.Server.core)
                implementation(Ktor2.Server.cors)
                implementation(Ktor2.Server.dataConversion)
                implementation(Ktor2.Server.cio)
                implementation(Benasher.uuid)
            }
        }
        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.Compose.foundation)
                implementation(AndroidX.multidex)
                implementation(AndroidX.window)
                implementation(AndroidX.activity)
                implementation(AndroidX.Compose.ui)
                implementation(AndroidX.Compose.material3)
                implementation(Icerock.Resources.resourcesCompose)
                implementation(Picovoice.porcupineAndroid)
                implementation(Ktor.Client.cio)
                implementation(Ktor.Server.core)
                implementation(Ktor.Server.netty)
                implementation(Ktor.Network.network)
                implementation(Slf4j.simple)
                implementation(Ktor2.Server.compression)
                implementation(Ktor2.Server.callLogging)
                implementation(files("libs/org.eclipse.paho.client.mqttv3-1.2.5.jar"))
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }
    packagingOptions {
        resources.pickFirsts.add("META-INF/*")
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "org.rhasspy.mobile" // required
}

aboutLibraries {
    registerAndroidTasks = true
    // Enable the duplication mode, allows to merge, or link dependencies which relate
    duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.LINK
    // Configure the duplication rule, to match "duplicates" with
    duplicationRule = com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
}


buildkonfig {
    packageName = "org.rhasspy.mobile"
    objectName = "BuildKonfig"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(STRING, "changelog", generateChangelog())
    }
}

fun generateChangelog(): String {
    var os = org.apache.commons.io.output.ByteArrayOutputStream()

    exec {
        standardOutput = os
        commandLine = listOf("git")
        args = listOf("describe", "--tags", "--abbrev=0")
    }

    val lastTag = String(os.toByteArray()).trim()
    os.close()

    os = org.apache.commons.io.output.ByteArrayOutputStream()
    exec {
        standardOutput = os
        commandLine = listOf("git")
        args = listOf("log", "$lastTag..HEAD", "--oneline", "--no-merges", "--pretty=format:\\\"%s\\\"")
    }
    val gitLogCmd = String(os.toByteArray()).trim()
    os.close()

    var changelog = "\""
    gitLogCmd.split("\n").forEach { line ->
//Remove surrounding quotation marks generated by the git log comand
        var escapedLine = line.substring(1, line.length)
        escapedLine = escapedLine.replace("(\\)/", "\\/")
        escapedLine = escapedLine.replace("\"", "\\\\")

//Add each item to the changelog as a bullet point
        changelog += "• $escapedLine \\n"

    }
    return changelog
}
