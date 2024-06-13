package com.lisawehb.webcrawler.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class UrlResource(
    @Serializable(with = URLSerializer::class)
    val url: URL,
    val depth: Int,
    val links: MutableSet<
        @Serializable(with = URLSerializer::class)
        URL,
    > = ConcurrentHashMap.newKeySet(),
) {
    override fun toString(): String = "$url: ${links.joinToString(separator = ",\n")}"
}

fun UrlResource.addLink(url: URL) {
    this.links.add(url)
}

object URLSerializer : KSerializer<URL> {
    override val descriptor = PrimitiveSerialDescriptor("URL", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): URL {
        return URL(decoder.decodeString())
    }

    override fun serialize(
        encoder: Encoder,
        value: URL,
    ) {
        encoder.encodeString(value.toString())
    }
}
