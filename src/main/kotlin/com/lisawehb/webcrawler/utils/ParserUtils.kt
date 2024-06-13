package com.lisawehb.webcrawler.utils

import mu.KotlinLogging
import java.net.MalformedURLException
import java.net.URL

private val logger = KotlinLogging.logger {}

fun internalLink(
    link: URL,
    baseUrl: URL,
): Boolean {
    return baseUrl.host == link.host
}

fun parseURL(
    pageToSearch: URL,
    link: String,
): URL? {
    val cleanedLink = cleanLink(link)
    if (cleanedLink.length < 2) return null
    return try {
        if (cleanedLink.startsWith("/") || cleanedLink.startsWith("..")) {
            URL(pageToSearch.protocol, pageToSearch.host, cleanedLink)
        } else {
            URL(cleanedLink)
        }
    } catch (e: MalformedURLException) {
        logger.warn { "Skipping malformed link $link" }
        null
    }
}

fun cleanLink(link: String): String {
    var cleanedLink = link.replace("/$".toRegex(), "")
    cleanedLink = cleanedLink.replace("^//".toRegex(), "http://")

    if (isRelativePath(cleanedLink)) cleanedLink = "/$cleanedLink"

    return cleanedLink
}

fun isRelativePath(link: String): Boolean {
    val pattern = "^[a-zA-Z0-9#?_-].*"
    val matchesPattern = Regex(pattern).matches(link)
    return link.length > 2 && matchesPattern && !link.contains(".") && !link.contains("http")
}
