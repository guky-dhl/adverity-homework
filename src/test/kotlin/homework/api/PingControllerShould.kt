package homework.api

import homework.infrastructure.ApiTest
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.test.inject

internal class PingControllerShould : ApiTest() {
    val client: HttpClient by inject()

    @Test
    fun ping() {
        runBlocking {
            client.get<String> {
                url {
                    encodedPath = "/ping"
                }
            }
        } shouldBe "OK"
    }

    @AfterEach
    internal fun tearDown() {
        stopKoin()
    }
}
