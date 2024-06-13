package com.lisawehb.webcrawler.utils

import com.lisawehb.webcrawler.mockUrl
import com.lisawehb.webcrawler.mockUrlResources
import com.lisawehb.webcrawler.model.UrlResource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import org.junit.Assert.assertEquals
import org.junit.Test

open class CrawlerUtilsKtTest {
    @Test
    fun `calculateChunkSize should return correct chunk size`() {
        val maxChunks = 64
        val listSize = 400
        val expectedChunkSize = (listSize / maxChunks) + 1
        val chunkSize = calculateChunkSize(listSize)

        assertEquals(expectedChunkSize, chunkSize)
    }

    @Test
    fun `calculateChunkSize should return 1 if listSize is smaller than maxChunks`() {
        val chunkSize = calculateChunkSize(listSize = 10)
        assertEquals(1, chunkSize)
    }

    @Test
    fun `convertToJson should correctly convert UrlResources to Json and sort by depth`() {
        val json = convertToJson(links = mockUrlResources)
        val jsonArray = Json.parseToJsonElement(json).jsonArray
        val urlResources =
            jsonArray.map {
                Json.decodeFromJsonElement<UrlResource>(it)
            }
        assertEquals(3, urlResources.size)
        assertEquals(mockUrl, urlResources.first().url)
        assertEquals(mockUrlResources.map { it.url }, urlResources.map { it.url })
    }

    @Test
    fun `convertToJson should return empty Json array if links is empty`() {
        val json = convertToJson(links = emptyList())
        val jsonArray = Json.parseToJsonElement(json).jsonArray

        assertEquals(0, jsonArray.size)
    }
}
