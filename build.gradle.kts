plugins {
    kotlin("jvm") version "1.3.72"
}

group = "cool.db"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
    maven("https://maven.pkg.github.com/guky-dhl/exposed-entities") {
        credentials {
            username = "guky-dhl"
            password = "c60e8d8efc007788e0ac20ac183b2c85e64656af" //read only token
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.`java-time`)
    implementation("cool.db:exposed-entities:0.0.3")

    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.3")
    testImplementation("io.mockk:mockk:1.9.3")
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

tasks.test {
    useJUnitPlatform()
}
