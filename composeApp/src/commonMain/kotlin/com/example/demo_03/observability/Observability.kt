package com.example.demo_03.observability

data class AnalyticsEvent(
    val name: String,
    val properties: Map<String, String> = emptyMap(),
)

interface
AnalyticsTracker {
    fun track(event: AnalyticsEvent)
}

interface CrashReporter {
    fun capture(throwable: Throwable, metadata: Map<String, String> = emptyMap())
}

object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun track(event: AnalyticsEvent) = Unit
}

object NoOpCrashReporter : CrashReporter {
    override fun capture(throwable: Throwable, metadata: Map<String, String>) = Unit
}
