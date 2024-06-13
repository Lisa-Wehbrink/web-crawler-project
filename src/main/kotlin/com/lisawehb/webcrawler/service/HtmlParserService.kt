package com.lisawehb.webcrawler.service

import com.lisawehb.webcrawler.utils.internalLink
import com.lisawehb.webcrawler.utils.parseURL
import mu.KotlinLogging
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.URL

private val logger = KotlinLogging.logger {}

fun extractPages(
    pageToSearch: URL,
    baseUrl: URL,
): Set<URL> {
    return try {
        val pageContent = retrievePage(pageToSearch.toString())
        val links = pageContent.select("a")

        val uriList = links.mapNotNull { parseURL(pageToSearch, it.attr("href")) }
        uriList.filter { internalLink(it, baseUrl) }.toSet()
    } catch (httpException: HttpStatusException) {
        logger.error { "Failed to retrieve page content for $pageToSearch with ${httpException.message}" }
        emptySet()
    } catch (ioException: IOException) {
        logger.error { "Failed to load $pageToSearch with ${ioException.message}" }
        emptySet()
    }
}

fun retrievePage(url: String): Document {
    return Jsoup
        .connect(url)
        .timeout(10000)
        .get()
}

