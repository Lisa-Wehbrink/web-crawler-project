package com.lisawehb.webcrawler

import com.lisawehb.webcrawler.model.UrlResource
import java.net.URL

fun createMockUrls(
    count: Int,
    offset: Int,
): MutableSet<URL> {
    val urls = (1..count).map { URL("http://mock${it + offset}.url") }
    return urls.toMutableSet()
}

val mockUrl = URL("http://mock.url")
val mockUrlEmpty = URL("http://mock.url/empty")
val mockUrlSecondary = URL("http://mock.url/secondary")

val mockUrlResource =
    UrlResource(
        url = mockUrl,
        depth = 0,
        links = createMockUrls(10, 0),
    )

val mockEmptyUrlResource =
    UrlResource(
        url = mockUrlEmpty,
        depth = 1,
        links = mutableSetOf(),
    )

val mockUrlResources =
    listOf(
        mockUrlResource,
        mockEmptyUrlResource,
        UrlResource(
            url = mockUrlSecondary,
            depth = 1,
            links = createMockUrls(5, 10),
        ),
    )
