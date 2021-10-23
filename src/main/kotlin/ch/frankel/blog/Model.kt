package ch.frankel.blog

import java.net.URI

data class Model(
    val code: Int,
    val status: String,
    val copyright: String,
    val attributionText: String,
    val attributionHTML: String,
    val etag: String,
    val data: Data
)

data class Data(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<Result>
)

data class Result(
    val id: Int,
    val name: String,
    val description: String,
    val modified: String,
    val thumbnail: Thumbnail?,
    val resourceURI: URI,
    val comics: Collection,
    val series: Collection,
    val stories: Collection,
    val events: Collection,
    val urls: List<Url>
)

data class Thumbnail(
    val path: String,
    val extension: String
)

data class Collection(
    val available: Int,
    val collectionURI: URI,
    val items: List<Resource>,
    val returned: Int
)

data class Resource(
    val resourceURI: URI,
    val name: String,
    val type: String?
)

data class Url(
    val type: String,
    val url: URI
)
