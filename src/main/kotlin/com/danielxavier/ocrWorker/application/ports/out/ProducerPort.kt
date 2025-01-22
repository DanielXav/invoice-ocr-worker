package com.danielxavier.ocrWorker.application.ports.out

import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceEvent

interface ProducerPort {

    fun sendMessage(message: InvoiceEvent)

}