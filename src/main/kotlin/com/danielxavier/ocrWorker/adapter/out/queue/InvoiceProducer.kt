package com.danielxavier.ocrWorker.adapter.out.queue

import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceEvent
import com.danielxavier.ocrWorker.application.ports.out.ProducerPort
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class InvoiceProducer(
    private val sqsTemplate: SqsTemplate,
    @Value("\${aws.queue.producer.name}") val queueName: String
): ProducerPort {

    override fun sendMessage(message: InvoiceEvent) {
         try {
             sqsTemplate.send(queueName, message)
         } catch (ex: Exception) {
             logger.error("Erro ao enviar mensagem para a fila '$queueName'. Mensagem: $message, Erro: ${ex.message}", ex)
             throw ex
         }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(InvoiceProducer::class.java)
    }
}