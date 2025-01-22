package com.danielxavier.ocrWorker.adapter.`in`.controller

import com.danielxavier.ocrWorker.application.usecase.ProcessInvoiceUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/textract")
class TextractController(
    private val processInvoiceUseCase: ProcessInvoiceUseCase
) {

    @GetMapping("/{key}")
    fun textract(@PathVariable key: String): ResponseEntity<String> {

        //val extractedInvoices = processInvoiceUseCase.processInvoice(key)

        return ResponseEntity.ok("extractedInvoices")

    }

}