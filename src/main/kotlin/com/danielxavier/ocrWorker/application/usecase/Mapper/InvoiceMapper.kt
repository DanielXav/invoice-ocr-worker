package com.danielxavier.ocrWorker.application.usecase.Mapper

import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceEvent
import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceItemEvent
import com.danielxavier.ocrWorker.domain.Invoice
import com.danielxavier.ocrWorker.domain.InvoiceItem

object InvoiceMapper {

    fun Invoice.toInvoiceEvent(): InvoiceEvent =
        InvoiceEvent(
            id = this.id,
            value = this.value,
            date = this.date,
            items = this.items.map { it.toInvoiceItem() }
        )

    fun InvoiceItem.toInvoiceItem(): InvoiceItemEvent =
        InvoiceItemEvent(
            establishment = this.establishment,
            value = this.value
        )

    fun InvoiceEvent.toDomain(): Invoice =
        Invoice(
            id = this.id,
            value = this.value,
            date = this.date,
            items = this.items.map { it.toDomain() }
        )

    fun InvoiceItemEvent.toDomain(): InvoiceItem =
        InvoiceItem(
            establishment = this.establishment,
            value = this.value
        )
}