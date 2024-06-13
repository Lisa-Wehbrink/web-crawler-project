package com.lisawehb.webcrawler.service

import com.lisawehb.webcrawler.SafeMockingTestClass
import com.lisawehb.webcrawler.createMockUrls
import com.lisawehb.webcrawler.mockEmptyUrlResource
import com.lisawehb.webcrawler.mockUrlResource
import com.lisawehb.webcrawler.mockUrlSecondary
import com.lisawehb.webcrawler.utils.convertToJson
import com.lisawehb.webcrawler.utils.saveAndPrint
import io.mockk.every
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.net.URL

open class CrawlerServiceKtTest : SafeMockingTestClass {
    @Before
    fun setUp() {
        mockkStatic("com.lisawehb.webcrawler.service.HtmlParserServiceKt")
        mockkStatic("com.lisawehb.webcrawler.service.CrawlerServiceKt")
        mockkStatic("com.lisawehb.webcrawler.utils.CrawlerUtilsKt")
    }

    private val returnedUrls = (1..100).map { createMockUrls(3, (it * 2)) }

    @Test
    fun `startCrawl should initiate crawl, and extract pages`() {
        every { extractPages(any(), any()) } returnsMany returnedUrls
        every { saveAndPrint(any()) } just runs

        val crawlerService = CrawlerService(origin = mockUrlResource, maxDepth = 3)

        runBlocking {
            crawlerService.startCrawl()
        }
        verify(exactly = 1) {
            extractPages(mockUrlResource.url, mockUrlResource.url)
            convertToJson(any())
            saveAndPrint(any())
        }
        verify(exactly = 22) {
            extractPages(any(), mockUrlResource.url)
        }
    }

    @Test
    fun `startCrawl should not initiate crawl if not links are retrieved at depth 0`() {
        every { extractPages(any(), any()) } returns mutableSetOf()

        val crawlerService = CrawlerService(origin = mockEmptyUrlResource, maxDepth = 3)

        runBlocking {
            crawlerService.startCrawl()
        }
        verify(exactly = 1) {
            extractPages(any(), any())
        }
        verify(exactly = 0) {
            convertToJson(any())
            saveAndPrint(any())
        }
    }

    @Test
    fun `startCrawl should end crawl early if no links are returned`() {
        every { extractPages(any(), any()) } returns mutableSetOf(mockUrlSecondary)
        val additionalUrlResource = mockUrlResource.copy(url = URL("http://mock.com/additional"))

        val crawlerService = CrawlerService(origin = additionalUrlResource, maxDepth = 3)

        runBlocking {
            crawlerService.startCrawl()
        }
        verify(exactly = 2) {
            extractPages(any(), any())
        }
        verify(exactly = 1) {
            convertToJson(any())
            saveAndPrint(any())
        }
    }
}
