package com.lisawehb.webcrawler

import com.lisawehb.webcrawler.model.UrlResource
import com.lisawehb.webcrawler.service.CrawlerService
import kotlinx.coroutines.runBlocking
import java.net.MalformedURLException
import java.net.URL
import kotlin.system.exitProcess

private const val MAX_DEPTH = 5

fun main(args: Array<String>) {
    try {
        val url = URL(args.first())
        val origin = UrlResource(url = url, depth = 0)

        var userMaxDepth: Int? = null
        if (args.size == 2) {
            userMaxDepth = args[1].toIntOrNull()
        }

        val crawlerService = CrawlerService(origin, userMaxDepth ?: MAX_DEPTH)
        runBlocking {
            crawlerService.startCrawl()
        }
    } catch (e: NoSuchElementException) {
        println("Please provide the URL to crawl as a parameter ")
        println("(example: java -jar filename.jar http://google.com")
        exitProcess(2)
    } catch (e: MalformedURLException) {
        println("Please only provide URLs in this format: protocol://host/path (e.g. http:google.com/search")
    }
}
