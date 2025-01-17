package com.danielxavier.ocrWorker.domain

data class Invoice(
    val id: String,
    val items: List<InvoiceItem>
)

data class InvoiceItem(
    val establishment: String,
    val value: Double
)