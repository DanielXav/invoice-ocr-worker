package com.danielxavier.ocrWorker.adapter.out.queue.Mapper

import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceEvent
import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceItemEvent
import com.danielxavier.ocrWorker.adapter.out.queue.Mapper.InvoiceMapper.toDomain
import com.danielxavier.ocrWorker.domain.Invoice
import com.danielxavier.ocrWorker.domain.InvoiceItem

object InvoiceMapper {

    fun Invoice.toDomain(): InvoiceEvent =
        InvoiceEvent(
            id = this.id,
            value = this.value,
            date = this.date,
            items = this.items.map { it.toDomain() }
        )

    fun InvoiceItem.toDomain(): InvoiceItemEvent =
        InvoiceItemEvent(
            establishment = this.establishment,
            value = this.value
        )
}