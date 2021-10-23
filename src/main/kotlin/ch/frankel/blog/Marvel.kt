package ch.frankel.blog

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.*
import org.springframework.web.util.UriBuilder
import java.math.BigInteger
import java.security.MessageDigest

@Configuration
class MarvelConfig {

    @Bean
    fun webClient(props: MarvelProperties) = WebClient.create("${props.serverUrl}/v1/public")

    @Bean
    fun md5(): MessageDigest = MessageDigest.getInstance("MD5")

    @Bean
    fun routes(client: WebClient, props: MarvelProperties, digest: MessageDigest) = router {
        GET("/") { request ->
            val mono = client
                .get()
                .uri {
                    it.path("/characters")
                        .queryParamsWith(request)
                        .queryParamsWith(props, digest)
                        .build()
                }.retrieve()
                .bodyToMono<Model>()
            ServerResponse.ok().body(mono)
        }
    }
}

private fun UriBuilder.queryParamsWith(request: ServerRequest) = apply {
    arrayOf("limit", "offset", "orderBy").forEach { param ->
        request.queryParam(param).ifPresent {
            queryParam(param, it)
        }
    }
}

private fun UriBuilder.queryParamsWith(props: MarvelProperties, digest: MessageDigest) = apply {
    val ts = System.currentTimeMillis().toString()
    queryParam("ts", ts)
    queryParam("apikey", props.apiKey)
    val md5 = "$ts${props.privateKey}${props.apiKey}".toMd5(digest)
    queryParam("hash", md5)
}

private fun String.toMd5(digest: MessageDigest) = BigInteger(1, digest.digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')
