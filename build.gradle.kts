import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

group = "com.nodil"
version = "1.0"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.useK2 = true
}

tasks.getByName<Jar>("jar"){
    enabled = false
}


dependencies {
    testImplementation(kotlin("test"))
    implementation("org.json:json:20220320")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.ta4j:ta4j-core:0.15")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.nield:kotlin-statistics:1.2.1")



}

tasks.test {
    useJUnitPlatform()
}
application {
    mainClass.set("MainKt")
}
