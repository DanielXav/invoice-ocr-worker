package com.danielxavier.ocrWorker.adapter.`in`.controller

import com.danielxavier.ocrWorker.application.usecase.ProcessInvoiceUseCase
import com.danielxavier.ocrWorker.domain.InvoiceItem
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.textract.TextractClient
import software.amazon.awssdk.services.textract.model.BlockType
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse

@RestController
@RequestMapping("/textract")
class TextractController(
    private val textractClient: TextractClient,
    private val processInvoiceUseCase: ProcessInvoiceUseCase
) {

    @GetMapping("/{key}")
    fun textract(@PathVariable key: String): ResponseEntity<List<InvoiceItem>> {
        return try {
            val extractedInvoices = processInvoiceUseCase.processInvoice(key)

            if (extractedInvoices.isEmpty()) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.ok(extractedInvoices)
            }
        } catch (ex: Exception) {
            ResponseEntity.status(500).body(emptyList())
        }
    }

    fun extractLines(response: DetectDocumentTextResponse): List<String> {
        return response.blocks()
            .filter { it.blockType() == BlockType.LINE }
            .mapNotNull { it.text() }
    }

}