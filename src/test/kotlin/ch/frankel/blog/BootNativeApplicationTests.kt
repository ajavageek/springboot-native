package ch.frankel.blog

import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = [
        "app.marvel.api-key=dummy",
        "app.marvel.private-key=dummy"
    ]
)
@Testcontainers
class BootNativeApplicationTests {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    companion object {

        @Container
        val mockServer = MockServerContainer(
            DockerImageName.parse("mockserver/mockserver")
        )

        @JvmStatic
        @DynamicPropertySource
        fun registerServerUrl(registry: DynamicPropertyRegistry) {
            registry.add("app.marvel.server-url") {
                "http://${mockServer.containerIpAddress}:${mockServer.serverPort}"
            }
        }
    }

    @Test
    fun `should deserialize JSON payload from server and serialize it back again`() {
        val mockServerClient = MockServerClient(mockServer.containerIpAddress, mockServer.serverPort)
        val sample = ClassPathResource("/sample.json").file.readText()
        mockServerClient.`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withPath("/v1/public/characters")
        ).respond(
            HttpResponse()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json")
                .withBody(sample)
        )
        webTestClient.get()
            .uri("/")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.data.count").isEqualTo(1)
            .jsonPath("\$.data.results").isArray
            .jsonPath("\$.data.results[0].name").isEqualTo("Anita Blake")
    }
}
