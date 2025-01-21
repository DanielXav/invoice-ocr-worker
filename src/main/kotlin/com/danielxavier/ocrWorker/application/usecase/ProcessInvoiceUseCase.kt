package com.danielxavier.ocrWorker.application.usecase

import com.danielxavier.ocrWorker.application.ports.out.ProducerPort
import com.danielxavier.ocrWorker.application.ports.out.TextractPort
import com.danielxavier.ocrWorker.domain.Invoice
import com.danielxavier.ocrWorker.domain.InvoiceItem
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class ProcessInvoiceUseCase(
    private val textractPort: TextractPort,
    private val producerPort: ProducerPort
) {

    fun processInvoice(key: String) {

        logger.info("Iniciando o processo de extração dos itens da fatura.")

        val file = textractPort.textract(key)

        val invoiceValue: Double? = findInvoiceValue(file)

        val items = extractInvoiceItem(file).also {
            logger.info("Itens da fatura extraidos com sucesso.")
        }

        producerPort.sendMessage(
            Invoice(
                id = UUID.randomUUID().toString(),
                value = invoiceValue,
                date = LocalDateTime.now(),
                items = items
            )
        ).also {
            logger.info("Mensagem enviada com sucesso para a fila.")
        }
    }

    fun findInvoiceValue(file: List<String>): Double? {
        var invoiceValue: Double? = null

        val subtotalPattern = """Subtotal deste cartão R\$ (\d{1,3}(\.\d{3})*,\d{2})""".toRegex()
        val totalPattern = """Total dos lançamentos atuais""".toRegex()
        val valuePattern = """\d{1,3}(\.\d{3})*,\d{2}""".toRegex()

        file.forEach { line ->
            subtotalPattern.find(line)?.groupValues?.get(1)?.let {
                invoiceValue = it.replace(".", "").replace(",", ".").toDouble()
            }
            if (totalPattern.matches(line)) {
                val nextLineIndex = file.indexOf(line) + 1
                if (nextLineIndex < file.size) {
                    val nextLine = file[nextLineIndex]
                    if (valuePattern.matches(nextLine)) {
                        invoiceValue = nextLine.replace(".", "").replace(",", ".").toDouble()
                    }
                }
            }
        }
        return invoiceValue
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
            "(?i)Pagamento Efetuado".toRegex(),
            "Total dos lançamentos atuais".toRegex()
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