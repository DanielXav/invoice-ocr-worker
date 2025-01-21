package com.danielxavier.ocrWorker.adapter.out.queue.Event

import java.time.LocalDateTime

data class InvoiceEvent(
    val id: String,
    var value: Double?,
    val date: LocalDateTime,
    val items: List<InvoiceItemEvent>
)

data class InvoiceItemEvent(
    val establishment: String,
    val value: Double
)