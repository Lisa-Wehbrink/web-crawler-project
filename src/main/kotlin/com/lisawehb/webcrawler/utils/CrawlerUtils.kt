package com.lisawehb.webcrawler.utils

import com.lisawehb.webcrawler.model.UrlResource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private const val MAX_CHUNKS = 64

fun calculateChunkSize(listSize: Int): Int {
    val chunkSize = listSize / MAX_CHUNKS
    return chunkSize + 1
}

@OptIn(ExperimentalSerializationApi::class)
fun convertToJson(links: List<UrlResource>): String {
    val prettyJson =
        Json {
            prettyPrint = true
            prettyPrintIndent = " "
        }
    return prettyJson.encodeToString(links.sortedBy { it.depth })
}

fun saveAndPrint(json: String) {
    File("output.json").writeText(json)
    print("Saved crawl data to output file")
}
