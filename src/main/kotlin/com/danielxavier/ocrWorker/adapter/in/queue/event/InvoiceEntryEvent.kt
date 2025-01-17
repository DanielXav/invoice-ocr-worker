package com.danielxavier.ocrWorker.adapter.`in`.queue.event

data class InvoiceEntryEvent(
    val key: String,
    val type: String,
    val size: Double
)
