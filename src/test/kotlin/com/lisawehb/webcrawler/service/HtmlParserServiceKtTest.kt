package com.lisawehb.webcrawler.service

import com.lisawehb.webcrawler.SafeMockingTestClass
import com.lisawehb.webcrawler.mockUrl
import com.lisawehb.webcrawler.mockUrlEmpty
import com.lisawehb.webcrawler.mockUrlSecondary
import com.lisawehb.webcrawler.utils.internalLink
import com.lisawehb.webcrawler.utils.parseURL
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.jsoup.Connection
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.URL
import kotlin.test.assertFailsWith

open class HtmlParserServiceKtTest : SafeMockingTestClass {
    private val mockHtml =
        """
        <!DOCTYPE html>
        <html>
            <body>
                <h1>Test heading</h1>
                    <a href="hpp:/invalid.com">Mock URL</a>
                    <p>test paragraph
                        <a href="https://mock.url">Mock URL</a> 
                        <a href="/internal">Mock URL</a> 
                    </p>
            </body>
        </html>
        """.trimIndent()

    private val mockDocument = Jsoup.parse(mockHtml)

    @Before
    fun setUp() {
        mockkStatic("com.lisawehb.webcrawler.service.HtmlParserServiceKt")
        mockkStatic("com.lisawehb.webcrawler.utils.ParserUtilsKt")
        mockkStatic("org.jsoup.Jsoup")
    }

    @Test
    fun `extractPages should extract href, pass them to parseUrl and filter out any return null values`() {
        every { retrievePage(any()) } returns mockDocument
        every { parseURL(any(), any()) } returnsMany listOf(mockUrlSecondary, mockUrl, null)
        every { internalLink(any(), any()) } returns true

        val pages = extractPages(mockUrlEmpty, mockUrl)
        assertEquals(2, pages.size)

        verify(exactly = 1) {
            retrievePage(mockUrlEmpty.toString())
            parseURL(mockUrlEmpty, "hpp:/invalid.com")
            parseURL(mockUrlEmpty, "https://mock.url")
            parseURL(mockUrlEmpty, "/internal")
        }
    }

    @Test
    fun `extractPages should filter out links where internalLink returns false`() {
        val mockHtmlExternal = ("<a href=\"http://anothermock.com\">Mock URL</a> ").plus(mockHtml)
        val mockDocExternal = Jsoup.parse(mockHtmlExternal)
        val externalUrl = URL("http://anothermock.com")

        every { retrievePage(any()) } returns mockDocExternal
        every { parseURL(any(), any()) } returnsMany listOf(externalUrl, mockUrlSecondary, mockUrl, null)
        every { internalLink(any(), any()) } returns false andThen true

        val pages = extractPages(mockUrlEmpty, mockUrl)
        assertEquals(2, pages.size)

        verify(exactly = 1) {
            retrievePage(mockUrlEmpty.toString())
            parseURL(mockUrlEmpty, "hpp:/invalid.com")
            parseURL(mockUrlEmpty, "http://anothermock.com")
            parseURL(mockUrlEmpty, "https://mock.url")
            parseURL(mockUrlEmpty, "/internal")
            internalLink(externalUrl, mockUrl)
            internalLink(mockUrlSecondary, mockUrl)
            internalLink(mockUrl, mockUrl)
        }
    }

    @Test
    fun `extractPages should catch HttpStatusException from retrievePage and return empty set`() {
        every { retrievePage(any()) } throws HttpStatusException("Test message", 401, mockUrl.toString())

        val pages = extractPages(mockUrlEmpty, mockUrl)
        assertEquals(0, pages.size)

        verify(exactly = 0) {
            parseURL(any(), any())
            internalLink(any(), any())
        }
    }

    @Test
    fun `extractPages should catch IOException from retrievePage and return empty set`() {
        every { retrievePage(any()) } throws IOException()

        val pages = extractPages(mockUrlEmpty, mockUrl)
        assertEquals(0, pages.size)

        verify(exactly = 0) {
            parseURL(any(), any())
            internalLink(any(), any())
        }
    }

    @Test
    fun `retrievePage should return Jsoup document`() {
        val mockConnection = mockk<Connection>()
        every { Jsoup.connect(any()) } returns mockConnection
        every { mockConnection.timeout(any()) } returns mockConnection
        every { mockConnection.get() } returns mockDocument

        val page = retrievePage(mockUrl.toString())
        assertEquals(mockDocument, page)

        verify(exactly = 1) {
            mockConnection.timeout(10000)
            mockConnection.get()
        }
    }

    @Test
    fun `retrievePage should throw exception if page returns anything but 200`() {
        val expectedException = HttpStatusException("Test exception", 401, mockUrl.toString())
        every { Jsoup.connect(any()) } throws expectedException

        assertFailsWith<HttpStatusException>("Test exception") {
            retrievePage(mockUrl.toString())
        }
    }
}
