package com.lisawehb.webcrawler.utils

import com.lisawehb.webcrawler.SafeMockingTestClass
import com.lisawehb.webcrawler.mockUrl
import com.lisawehb.webcrawler.mockUrlEmpty
import com.lisawehb.webcrawler.mockUrlSecondary
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.URL

open class ParserUtilsKtTest : SafeMockingTestClass {
    @Before
    fun setUp() {
        mockkStatic("com.lisawehb.webcrawler.utils.ParserUtilsKt")
    }

    @Test
    fun `internalLink should return true if both hosts are the same`() {
        val result = internalLink(mockUrl, mockUrlSecondary)
        assertTrue(result)
    }

    @Test
    fun `internalLink should return false if hosts are different`() {
        val externalUrl = URL("http://another.mock")
        val result = internalLink(mockUrl, externalUrl)
        assertFalse(result)
    }

    @Test
    fun `parseUrl should return String as URL with no changes if it is correctly formatted`() {
        val urlString = "http://mock.url/empty"
        every { cleanLink(any()) } returns urlString

        val result = parseURL(mockUrl, urlString)
        assertEquals(mockUrlEmpty, result)

        verify(exactly = 1) {
            cleanLink(urlString)
        }
    }

    @Test
    fun `parseUrl should call clean link and return null if cleaned link length is less than two`() {
        val urlString = ""
        every { cleanLink(any()) } returns urlString

        val result = parseURL(mockUrl, urlString)
        assertNull(result)

        verify(exactly = 1) {
            cleanLink(urlString)
        }
    }

    @Test
    fun `parseUrl should add internal hostname and protocol to urls starting with a slash`() {
        val urlString = "/empty"
        every { cleanLink(any()) } returns urlString

        val result = parseURL(mockUrl, urlString)
        assertEquals(mockUrlEmpty, result)

        verify(exactly = 1) {
            cleanLink(urlString)
        }
    }

    @Test
    fun `parseUrl should add internal hostname and protocol to urls starting with two dots`() {
        val urlString = "..empty"
        val expectedUrlString = "http://mock.url$urlString"
        every { cleanLink(any()) } returns urlString

        val result = parseURL(mockUrl, urlString)
        assertEquals(expectedUrlString, result.toString())

        verify(exactly = 1) {
            cleanLink(urlString)
        }
    }

    @Test
    fun `parseUrl should return null if the URL is malformed and throws an exception`() {
        val urlString = "h//:wrong.url"
        every { cleanLink(any()) } returns urlString

        val result = parseURL(mockUrl, urlString)
        assertNull(result)

        verify(exactly = 1) {
            cleanLink(urlString)
        }
    }

    @Test
    fun `cleanLink should remove slashes at the end of a link`() {
        every { isRelativePath(any()) } returns false

        val urlString = "http://mock.url/"
        val expectedUrlString = "http://mock.url"
        val cleanedLink = cleanLink(urlString)
        assertEquals(expectedUrlString, cleanedLink)

        verify(exactly = 1) {
            isRelativePath(expectedUrlString)
        }
    }

    @Test
    fun `cleanLink should replace double slashes the full host`() {
        every { isRelativePath(any()) } returns false

        val urlString = "//mock.url"
        val expectedUrlString = "http://mock.url"
        val cleanedLink = cleanLink(urlString)
        assertEquals(expectedUrlString, cleanedLink)

        verify(exactly = 1) {
            isRelativePath(expectedUrlString)
        }
    }

    @Test
    fun `cleanLink should add a slash to the front of string if isRelativePath is true`() {
        every { isRelativePath(any()) } returns true

        val urlString = "test"
        val cleanedLink = cleanLink(urlString)
        assertEquals("/test", cleanedLink)

        verify(exactly = 1) {
            isRelativePath(urlString)
        }
    }

    @Test
    fun `isRelativePath should return true if string of sufficient length matches the criteria`() {
        val urlString = "#main"
        assertTrue(isRelativePath(urlString))
    }

    @Test
    fun `isRelativePath should return false if string is too short`() {
        val urlString = "#"
        assertFalse(isRelativePath(urlString))
    }

    @Test
    fun `isRelativePath should return false if string does not match matcher`() {
        val urlString = "/test"
        assertFalse(isRelativePath(urlString))
    }

    @Test
    fun `isRelativePath should return false if string contains http`() {
        val urlString = "http://mock.url"
        assertFalse(isRelativePath(urlString))
    }

    @Test
    fun `isRelativePath should return false if string contains a dot`() {
        val urlString = "mock.url"
        assertFalse(isRelativePath(urlString))
    }
}
