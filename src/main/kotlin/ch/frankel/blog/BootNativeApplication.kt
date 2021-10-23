package ch.frankel.blog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.nativex.hint.*
import java.net.URI

@SpringBootApplication
@ConfigurationPropertiesScan
@NativeHint(options = ["--enable-https"])
@TypeHint(
    types = [Model::class, Data::class, Result::class, Thumbnail::class, Collection::class, Resource::class, Url::class, URI::class],
    access = AccessBits.FULL_REFLECTION
)
class BootNativeApplication

fun main(args: Array<String>) {
    runApplication<BootNativeApplication>(*args)
}
