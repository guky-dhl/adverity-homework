package homework.infrastructure

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import io.ktor.config.HoconApplicationConfig
import io.ktor.config.tryGetString
import io.ktor.config.tryGetStringList
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Creates an [ApplicationEngineEnvironment] instance from command line arguments
 */
@KtorExperimentalAPI
fun testEnvironment(args: Array<String> = arrayOf(), module: Application.() -> Unit): ApplicationEngineEnvironment {
    val argsMap = args.mapNotNull { it.splitPair('=') }.toMap()

    val configFile = ApplicationEnvironment::class.java.getResource("/test-application.conf")?.let { File(it.path) }
    val commandLineMap = argsMap.filterKeys { it.startsWith("-P:") }.mapKeys { it.key.removePrefix("-P:") }

    val environmentConfig = ConfigFactory.systemProperties().withOnlyPath("ktor")
    val fileConfig = configFile?.let { ConfigFactory.parseFile(it) } ?: ConfigFactory.load()
    val argConfig = ConfigFactory.parseMap(commandLineMap, "Command-line options")
    val combinedConfig = argConfig.withFallback(fileConfig).withFallback(environmentConfig).resolve()

    val applicationIdPath = "ktor.application.id"

    val hostConfigPath = "ktor.deployment.host"
    val hostPortPath = "ktor.deployment.port"
    val hostWatchPaths = "ktor.deployment.watch"

    val rootPathPath = "ktor.deployment.rootPath"

    val applicationId = combinedConfig.tryGetString(applicationIdPath) ?: "Application"
    val appLog = LoggerFactory.getLogger(applicationId)
    if (configFile != null && !configFile.exists()) {
        appLog.error("Configuration file test-application.conf was not found")
        appLog.warn("Will attempt to start without loading configuration…")
    }
    val rootPath = argsMap["-path"] ?: combinedConfig.tryGetString(rootPathPath) ?: ""

    val environment = applicationEngineEnvironment {
        log = appLog
        classLoader = ApplicationEnvironment::class.java.classLoader
        config = HoconApplicationConfig(combinedConfig)
        this.rootPath = rootPath

        val contentHiddenValue = ConfigValueFactory.fromAnyRef("***", "Content hidden")
        if (combinedConfig.hasPath("ktor")) {
            log.trace(
                combinedConfig.getObject("ktor")
                    .withoutKey("security")
                    .withValue("security", contentHiddenValue)
                    .render()
            )
        } else {
            log.trace(
                "No configuration provided: neither application.conf " +
                        "nor system properties nor command line options (-config or -P:ktor...=) provided"
            )
        }

        val host = argsMap["-host"] ?: combinedConfig.tryGetString(hostConfigPath) ?: "0.0.0.0"
        val port = argsMap["-port"] ?: combinedConfig.tryGetString(hostPortPath)


        if (port != null) {
            connector {
                this.host = host
                this.port = port.toInt()
            }
        }


        if (port == null) {
            throw IllegalArgumentException(
                "Neither port nor sslPort specified. Use command line options -port/-sslPort " +
                        "or configure connectors in application.conf"
            )
        }

        (argsMap["-watch"]?.split(",") ?: combinedConfig.tryGetStringList(hostWatchPaths))?.let {
            watchPaths = it
        }

        modules.clear()
        module(module)
    }

    return environment
}

private fun String.splitPair(ch: Char): Pair<String, String>? = indexOf(ch).let { idx ->
    when (idx) {
        -1 -> null
        else -> Pair(take(idx), drop(idx + 1))
    }
}
