package com.danielxavier.ocrWorker.application.usecase

import com.danielxavier.ocrWorker.application.ports.out.TextractPort
import com.danielxavier.ocrWorker.domain.InvoiceItem
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProcessInvoiceUseCase(
    private val textractPort: TextractPort
) {

    fun processInvoice(key: String): List<InvoiceItem> {
        val file = textractPort.textract(key)
        return extractInvoiceItem(file)
    }

    fun extractInvoiceItem(file: List<String>): List<InvoiceItem> {
        val transactions = mutableListOf<InvoiceItem>()
        val processedValues = mutableSetOf<Double>()

        val valuePattern = """\d{1,3}(\.\d{3})*(,\d{2})?""".toRegex()
        var currentEstablishment: String? = null
        var skipNextValue = false

        val ignorePatterns = listOf(
            "Lançamentos no cartão \\(final \\d{4}\\)".toRegex(),
            "Lançamentos produtos e serviços".toRegex(),
            "(?i)Pagamento Efetuado".toRegex()
        )

        file.forEach { line ->
            when {
                ignorePatterns.any { it.matches(line) } -> {
                    currentEstablishment = null
                    skipNextValue = true
                }
                valuePattern.matches(line) -> {
                    if (skipNextValue) {
                        skipNextValue = false
                    } else if (currentEstablishment != null) {
                        val value = line.replace(".", "").replace(",", ".").toDouble()

                        if (value !in processedValues) {
                            transactions.add(
                                InvoiceItem(
                                    establishment = currentEstablishment!!,
                                    value = value
                                )
                            )
                            processedValues.add(value)
                        }

                        currentEstablishment = null
                    }
                }
                else -> {
                    currentEstablishment = line
                }
            }
        }
        return transactions
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ProcessInvoiceUseCase::class.java.name)
    }
}