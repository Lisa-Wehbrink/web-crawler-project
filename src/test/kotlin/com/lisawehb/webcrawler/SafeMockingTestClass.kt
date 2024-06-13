package com.lisawehb.webcrawler

import io.mockk.unmockkAll
import kotlin.test.AfterTest

interface SafeMockingTestClass {
    @AfterTest
    fun tearDown() {
        unmockkAll()
    }
}
