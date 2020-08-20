package homework.infrastructure

import homework.databaseModule
import homework.mainModule
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.time.LocalDateTime.now
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@Suppress("UnnecessaryAbstractClass")
@OptIn(ExperimentalTime::class, KtorExperimentalAPI::class)
abstract class ApiTest : KoinTest {
    private val port by lazy {
        Random(now().nano).nextInt(5000, 6000)
    }

    @KtorExperimentalAPI
    private val applicationEngine = embeddedServer(
        Netty,
        environment = testEnvironment(
            arrayOf("-port=$port")
        ) {
            startKoin {
                modules(mainModule(this@testEnvironment), databaseModule, httpClientModule(port))
            }
        }
    ).start(false)
}

@OptIn(KtorExperimentalAPI::class)
fun httpClientModule(defaultPort: Int) = module {
    single {
        HttpClient(CIO) {
            defaultRequest {
                host = "localhost"
                port = defaultPort
                contentType(ContentType.Application.Json)
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
        }
    }
}
