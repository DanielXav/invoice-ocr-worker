package com.danielxavier.ocrWorker.adapter.`in`.queue

import com.danielxavier.ocrWorker.adapter.`in`.queue.event.InvoiceEntryEvent
import com.danielxavier.ocrWorker.application.usecase.ProcessInvoiceUseCase
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InvoiceEntryListenerTest {

    private lateinit var processInvoiceUseCase: ProcessInvoiceUseCase
    private lateinit var invoiceEntryListener: InvoiceEntryListener

    @BeforeEach
    fun setUp() {
        processInvoiceUseCase = mockk()
        invoiceEntryListener = InvoiceEntryListener(processInvoiceUseCase)
    }

    @Test
    fun `deve consumir uma mensagem e fazer acknowledgement`() = runBlocking {
        val event = mockk<InvoiceEntryEvent>(relaxed = true)
        val ack = mockk<Acknowledgement>(relaxed = true)

        every { event.key } returns "key"

        coEvery { processInvoiceUseCase.processInvoice(any()) } just Runs

        invoiceEntryListener.invoiceEntry(event, ack).join()

        coVerify(exactly = 1) { processInvoiceUseCase.processInvoice("key") }
        coVerify(exactly = 1) { ack.acknowledge() }
    }

    @Test
    fun `deve gerar erro quando falhar`() = runBlocking {
        val event = mockk<InvoiceEntryEvent>(relaxed = true)
        val ack = mockk<Acknowledgement>(relaxed = true)

        every { event.key } returns "key"

        coEvery { processInvoiceUseCase.processInvoice(any()) } throws RuntimeException()

        invoiceEntryListener.invoiceEntry(event, ack).join()

        coVerify(exactly = 1) { processInvoiceUseCase.processInvoice("key") }
        coVerify(exactly = 0) { ack.acknowledge() }
    }

}