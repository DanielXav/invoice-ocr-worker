package com.danielxavier.ocrWorker.domain

import java.time.LocalDateTime

data class Invoice(
    val id: String,
    var value: Double?,
    val date: LocalDateTime,
    val items: List<InvoiceItem>
)

data class InvoiceItem(
    val establishment: String,
    val value: Double
)