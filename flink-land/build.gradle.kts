import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val flink_version: String by project
val hoplite_version: String by project
val klogging_version: String by project

plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
    application
}

group = "io.ipolyzos"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.flink:flink-clients:$flink_version")
    implementation("org.apache.flink:flink-runtime-web:$flink_version")
    implementation("org.apache.flink:flink-connector-kafka:$flink_version")
    implementation("org.apache.flink:flink-statebackend-rocksdb:$flink_version")

    implementation("org.apache.flink:flink-csv:$flink_version")
    implementation("org.apache.flink:flink-connector-files:$flink_version")
    implementation("org.apache.flink:flink-json:$flink_version")

    implementation("org.apache.flink:flink-table-api-java-bridge:$flink_version")
    implementation("org.apache.flink:flink-table-planner_2.12:$flink_version")

//    implementation("com.ververica:flink-connector-postgres-cdc:2.3.0")
    implementation("org.apache.flink:flink-sql-connector-kafka:$flink_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("org.rocksdb:rocksdbjni:7.6.0")

    implementation("com.sksamuel.hoplite:hoplite-core:$hoplite_version")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hoplite_version")
    implementation("io.github.microutils:kotlin-logging:$klogging_version")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}