package com.danielxavier.ocrWorker.adapter.out.queue

import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceEvent
import com.danielxavier.ocrWorker.application.ports.out.ProducerPort
import com.danielxavier.ocrWorker.domain.Invoice
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.stereotype.Component

@Component
class InvoiceProducer(
    private val sqsTemplate: SqsTemplate
): ProducerPort {

    override fun sendMessage(invoice: Invoice) {
        TODO("Not yet implemented")
    }
}