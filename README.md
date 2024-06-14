# Simple Web Crawler

A fairly simple web crawler that crawls one given domain, skipping any external links. 

## Requirements
- Java 17
- Gradle (when not using provided .jar)

## How to run
Extract kotlin-web-crawler-project-1.0-full.jar, and you should be able to run it with the following command, provided Java 17 is installed with `JAVA_HOME` and `PATH` configured:

```java -jar kotlin-web-crawler-project-1.0-full.jar {url to crawl} {optional depth}```

Depth is optional, the crawler should default to a depth of 5 if there is no depth provided.

Alternatively import into your IDE (requires gradle).

## Issues & Considerations

### Missing Features
- no politeness delay implemented, so risks overloading chosen domain with requests
- does not check whether domain allows crawling
- lacks proxy usage for crawling

### Possible Improvements
- better handling of problematic URL formatting
- retries on HttpStatusException
- full concurrency - avoiding merging of chunks before splitting back out again after each depth level
- better visualisation of results (e.g. as a graph with d3 or Neo4j)
- persisting crawler results

