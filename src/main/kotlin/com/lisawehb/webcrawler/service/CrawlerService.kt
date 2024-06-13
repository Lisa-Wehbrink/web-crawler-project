package com.lisawehb.webcrawler.service

import com.lisawehb.webcrawler.model.UrlResource
import com.lisawehb.webcrawler.model.addLink
import com.lisawehb.webcrawler.utils.calculateChunkSize
import com.lisawehb.webcrawler.utils.convertToJson
import com.lisawehb.webcrawler.utils.saveAndPrint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

private val crawledPages = ConcurrentHashMap<URL, UrlResource>()
private val logger = KotlinLogging.logger {}

class CrawlerService(private val origin: UrlResource, private val maxDepth: Int) {
    suspend fun startCrawl() {
        val links = crawlPage(origin)
        logger.info { "Retrieved ${links.size} links at depth 1, beginning concurrent crawl." }

        if (links.isNotEmpty()) {
            crawl(pages = links, depth = 1)

            val json = convertToJson(crawledPages.values.toList())
            saveAndPrint(json)
        } else {
            logger.info { "No internal links found in origin page, aborting crawl" }
        }
    }

    private suspend fun crawl(
        pages: Set<UrlResource>,
        depth: Int,
    ) {
        val pagesChunked = pages.chunked(calculateChunkSize(pages.size))
        val links = mutableSetOf<UrlResource>()

        logger.info { "Beginning crawl at depth $depth" }
        coroutineScope {
            pagesChunked.map { pagesChunk ->
                launch(Dispatchers.IO) {
                    pagesChunk.forEach { urlResource ->
                        val childPagesChunk = crawlPage(urlResource)
                        synchronized(links) {
                            links.addAll(childPagesChunk)
                        }
                    }
                }
            }
        }
        if (depth == maxDepth) {
            logger.info { "Reached maximum depth, ending crawl." }
        } else if (links.isEmpty()) {
            logger.info { "No new links found, ending crawl at $depth" }
        } else {
            crawl(links, depth + 1)
        }
    }

    private fun crawlPage(page: UrlResource): Set<UrlResource> {
        val addedPage = crawledPages.putIfAbsent(page.url, page)
        if (addedPage == null) {
            val extractedPages = extractPages(page.url, origin.url)
            val createdPages =
                extractedPages.mapNotNull { url ->
                    page.addLink(url)
                    if (!crawledPages.containsKey(url)) {
                        return@mapNotNull UrlResource(url, page.depth + 1)
                    } else {
                        return@mapNotNull null
                    }
                }
            logger.info {
                "Finished crawl of ${page.url}, found ${extractedPages.size} internal links at depth ${page.depth}."
            }
            return createdPages.toSet()
        } else {
            logger.info { "Skipping previously crawled page ${addedPage.url}" }
            return emptySet()
        }
    }
}
