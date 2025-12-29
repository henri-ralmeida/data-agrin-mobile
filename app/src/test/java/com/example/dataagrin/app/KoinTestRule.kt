package com.example.dataagrin.app

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.context.stopKoin

/**
 * Test rule to handle Koin context in tests
 */
class KoinTestRule : TestRule {
    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        object : Statement() {
            override fun evaluate() {
                try {
                    // Stop Koin if it's running from previous tests or application
                    stopKoin()
                } catch (e: Exception) {
                    // Ignore if Koin is not started
                }
                base.evaluate()
            }
        }
}
