package com.danielxavier.ocrWorker.application.usecase

import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceEvent
import com.danielxavier.ocrWorker.adapter.out.queue.Event.InvoiceItemEvent
import com.danielxavier.ocrWorker.application.ports.out.ProducerPort
import com.danielxavier.ocrWorker.application.ports.out.TextractPort
import com.danielxavier.ocrWorker.domain.InvoiceItem
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProcessInvoiceUseCaseTest {
     private lateinit var textractPort: TextractPort
     private lateinit var producerPort: ProducerPort
     private lateinit var processInvoiceUseCase: ProcessInvoiceUseCase

    @BeforeEach
    fun setUp() {
        textractPort = mockk<TextractPort>()
        producerPort = mockk<ProducerPort>()
        processInvoiceUseCase = ProcessInvoiceUseCase(textractPort, producerPort)
    }

    @Test
    fun `deve processar a fatura com sucesso`() = runBlocking {
        val key = "invoice-key"
        val file = listOf(
            "Subtotal deste cartão R$ 1.234,56",
            "Estabelecimento 1",
            "100,00",
            "Estabelecimento 2",
            "200,00"
        )
        val expectedItems = listOf(
            InvoiceItemEvent(establishment = "Estabelecimento 1", value = 100.00),
            InvoiceItemEvent(establishment = "Estabelecimento 2", value = 200.00)
        )
        val expectedInvoiceValue = 1234.56

        coEvery { textractPort.textract(key) } returns file
        coEvery { producerPort.sendMessage(any()) } just Runs

        val capturedInvoiceEvent = slot<InvoiceEvent>()

        processInvoiceUseCase.processInvoice(key)

        coVerify(exactly = 1) { textractPort.textract(key) }
        coVerify(exactly = 1) { producerPort.sendMessage(capture(capturedInvoiceEvent)) }

        val sentInvoiceEvent = capturedInvoiceEvent.captured
        assertEquals(expectedInvoiceValue, sentInvoiceEvent.value)
        assertEquals(expectedItems, sentInvoiceEvent.items)
        assertNotNull(sentInvoiceEvent.id)
        assertNotNull(sentInvoiceEvent.date)
    }

    @Test
    fun `deve lançar exceção quando o textract falhar`() = runBlocking {
        val key = "invoice-key"
        coEvery { textractPort.textract(key) } throws Exception()

        assertThrows<Exception> {
            processInvoiceUseCase.processInvoice(key)
        }

        coVerify(exactly = 1) { textractPort.textract(key) }
        coVerify(exactly = 0) { producerPort.sendMessage(any()) }
    }

    @Test
    fun `deve lançar exceção quando o envio para a fila falhar`() = runBlocking {
        val key = "invoice-key"
        val file = listOf(
            "Subtotal deste cartão R$ 1.234,56",
            "Estabelecimento 1",
            "100,00",
            "Estabelecimento 2",
            "200,00"
        )

        coEvery { textractPort.textract(key) } returns file
        coEvery { producerPort.sendMessage(any()) } throws RuntimeException("Erro no envio para a fila")

        assertThrows<RuntimeException>("Erro no envio para a fila") {
            runBlocking {
                processInvoiceUseCase.processInvoice(key)
            }
        }

        coVerify(exactly = 1) { textractPort.textract(key) }
        coVerify(exactly = 1) { producerPort.sendMessage(any()) }
    }
}