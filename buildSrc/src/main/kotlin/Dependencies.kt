object Versions {
    const val exposed = "0.26.2"
    const val kotlin = "1.4.0"
    const val ktor = "1.4.0"
}

object Libs {
    //database
    object exposed {
        const val core = "org.jetbrains.exposed:exposed-core:${Versions.exposed}"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc:${Versions.exposed}"
        const val `java-time` = "org.jetbrains.exposed:exposed-java-time:${Versions.exposed}"
    }

    val postgre = "org.postgresql:postgresql:42.2.6"

    object ktor {
        const val core = "io.ktor:ktor-server-core:${Versions.ktor}"
        const val netty = "io.ktor:ktor-server-netty:${Versions.ktor}"
        const val serialization = "io.ktor:ktor-serialization:${Versions.ktor}"
        const val `client-core` = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val `client-engine-cio` = "io.ktor:ktor-client-cio:${Versions.ktor}"
        const val `client-serialization` = "io.ktor:ktor-client-serialization-jvm:${Versions.ktor}"
    }
}
