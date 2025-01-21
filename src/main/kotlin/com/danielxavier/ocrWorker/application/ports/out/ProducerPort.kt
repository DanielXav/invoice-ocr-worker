package com.danielxavier.ocrWorker.application.ports.out

import com.danielxavier.ocrWorker.domain.Invoice

interface ProducerPort {

    fun sendMessage(invoice: Invoice)
}