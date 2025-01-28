package com.danielxavier.ocrWorker.adapter.out.queue

import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceEvent
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InvoiceProducerTest {

    private lateinit var sqsTemplate: SqsTemplate
    private lateinit var producer: InvoiceProducer

    @BeforeEach
    fun setUp() {
        sqsTemplate = mockk()
        producer = InvoiceProducer(sqsTemplate, "queueName")
    }

    @Test
    fun `deve fazer o envio para o sqs com sucesso`() = runBlocking {
        val message = mockk<InvoiceEvent>()

        coEvery { sqsTemplate.send("queueName", message) } returns mockk()

        producer.sendMessage(message)

        coVerify(exactly = 1) { sqsTemplate.send("queueName", message) }
    }

    @Test
    fun `deve fazer o envio para o sqs com falha`() = runBlocking {
        val message = mockk<InvoiceEvent>()

        coEvery { sqsTemplate.send("queueName", message) } throws Exception()

        assertThrows<Exception> {
            producer.sendMessage(message)
        }

        coVerify(exactly = 1) { sqsTemplate.send("queueName", message) }
    }
}