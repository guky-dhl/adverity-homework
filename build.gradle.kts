import tanvd.kosogor.proxy.shadowJar as sJar

plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("tanvd.kosogor") version "1.0.7" apply true
}

group = "cool.db"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/guky-dhl/kotlin-exposed-entities-alfa")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // rest api
    implementation(Libs.ktor.core)
    implementation(Libs.ktor.netty)
    implementation(Libs.ktor.serialization)

    // db
    implementation(Libs.exposed.core)
    implementation(Libs.exposed.jdbc)
    implementation(Libs.exposed.`java-time`)
    implementation("cool.db:kotlin-exposed-entities-alfa:0.0.3")
    implementation(Libs.postgre)
    implementation("com.h2database:h2:1.4.199")

    implementation("org.koin:koin-core:2.1.6")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3")

    testImplementation(Libs.ktor.`client-core`)
    testImplementation(Libs.ktor.`client-serialization`)
    testImplementation(Libs.ktor.`client-engine-cio`)
    testImplementation("org.koin:koin-test:2.1.6")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.3")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks {
    shadowJar {
        dependsOn(build)
    }
}

sJar {
    jar {
        archiveName = "homework.jar"
        mainClass = "io.ktor.server.netty.EngineMain"
    }
}

tasks.test {
    useJUnitPlatform()
}
