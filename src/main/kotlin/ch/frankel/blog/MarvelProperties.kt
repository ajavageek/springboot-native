package ch.frankel.blog

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("app.marvel")
@ConstructorBinding
data class MarvelProperties(
    val serverUrl: String,
    val apiKey: String,
    val privateKey: String
)
