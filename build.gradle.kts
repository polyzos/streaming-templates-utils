import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask

val hoplite_version: String by project
val logback_version: String by project
val parallel_consumer_version: String by project
val confluent_json_version: String by project
val avro_version: String by project
val kafka_avro_version: String by project
val klogging_version: String by project

plugins {
    kotlin("jvm") version "1.7.21"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.5.0"

    application
}

group = "io.ipolyzos"
version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

dependencies {
    implementation("io.confluent.parallelconsumer:parallel-consumer-core:$parallel_consumer_version")
    implementation("io.confluent:kafka-json-serializer:$confluent_json_version")
    implementation("io.confluent:kafka-avro-serializer:$kafka_avro_version") {
        exclude(group = "org.apache.kafka") // has conflicts with the parallel consumer
    }
    implementation("org.apache.avro:avro:$avro_version")

    implementation("com.sksamuel.hoplite:hoplite-core:$hoplite_version")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hoplite_version")
    implementation("io.github.microutils:kotlin-logging:$klogging_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")

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

tasks.getByName<GenerateAvroJavaTask>("generateAvroJava") {
    source("src/main/avro")
    setOutputDir(file("src/main/java"))
}

tasks.jar {
    from(
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    )
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}