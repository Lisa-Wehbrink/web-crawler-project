package com.lisawehb.webcrawler.model

import com.lisawehb.webcrawler.createMockUrls
import com.lisawehb.webcrawler.mockEmptyUrlResource
import com.lisawehb.webcrawler.mockUrlEmpty
import com.lisawehb.webcrawler.mockUrlResource
import com.lisawehb.webcrawler.mockUrlSecondary
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertContains

open class UrlResourceKtTest {
    @Test
    fun `UrlResource toString should return the url with a list of linked urls`() {
        val urlResource = mockUrlResource.copy(links = createMockUrls(2, 17))
        val resourceString = urlResource.toString()

        val expectedLinks = "${urlResource.links.first()},\n${urlResource.links.last()}"

        assertContains(resourceString, urlResource.url.toString())

        assertEquals(expectedLinks, resourceString.substringAfter(": "))
    }

    @Test
    fun `UrlResource toString should only return the resource url if no links are present`() {
        val resourceString = mockEmptyUrlResource.toString()
        assertEquals("${mockEmptyUrlResource.url}: ", resourceString)
    }

    @Test
    fun `addLink should add link to resource links`() {
        val emptyResource = UrlResource(url = mockUrlEmpty, depth = 0)
        assertEquals(0, emptyResource.links.size)

        emptyResource.addLink(mockUrlSecondary)

        assertEquals(1, emptyResource.links.size)
        assertEquals(mockUrlSecondary, emptyResource.links.first())
    }
}
