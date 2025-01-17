package com.danielxavier.ocrWorker.adapter.`in`.queue

import com.danielxavier.ocrWorker.adapter.`in`.queue.event.InvoiceEntryEvent
import com.danielxavier.ocrWorker.application.usecase.ProcessInvoiceUseCase
import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InvoiceEntryListener(
    private val processInvoiceUseCase: ProcessInvoiceUseCase
) {

    @SqsListener("\${aws.queue.name}")
    fun invoiceEntry(
        invoiceEntryEvent: InvoiceEntryEvent,
        ack: Acknowledgement
    ) =  CoroutineScope(Dispatchers.IO).launch {
        runCatching {
            logger.info("Iniciando consumo da fila sqs")
            processInvoiceUseCase.processInvoice(invoiceEntryEvent.key)
        }.onSuccess {
            ack.acknowledge()
            logger.info("Processo finalizado com sucesso.")
        }.onFailure {
            logger.error("Processo falhou")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(InvoiceEntryListener::class.java.name)
    }
}