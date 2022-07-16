plugins {
    kotlin("js") version "1.7.0"
}

group = "io.github"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.5")
    implementation("io.github.rkbalgi:iso4k:0.0.5")

    val localPath = rootDir.absolutePath + "/src/main/js/specs"
    implementation(npm("specs", File("$localPath")))


}



kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }

    }
}