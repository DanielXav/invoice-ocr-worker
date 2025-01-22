package com.danielxavier.ocrWorker.adapter.out.aws

import com.danielxavier.ocrWorker.application.ports.out.TextractPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.textract.TextractClient
import software.amazon.awssdk.services.textract.model.BlockType
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse

@Component
class Textract(
    private val textractClient: TextractClient,
    @Value("\${aws.bucket.name}") private val bucketName: String
): TextractPort {

    override fun textract(key: String): List<String> {
        return try {
            logger.info("Buscando documento no bucket '$bucketName' com chave: $key")
            val response = textractClient.detectDocumentText() {
                it.document { doc ->
                    doc.s3Object { s3 ->
                        s3.bucket(bucketName)
                        s3.name(key)
                    }
                }
            }
            response.extractLines()
        } catch (ex: Exception) {
            logger.error("Ocorreu um erro ao processar documento com textract. ${ex.message}", ex)
            throw ex
        }
    }

    fun DetectDocumentTextResponse.extractLines(): List<String> {
        return this.blocks()
            .filter { it.blockType() == BlockType.LINE }
            .mapNotNull { it.text() }
    }


    companion object {
        private val logger = LoggerFactory.getLogger(Textract::class.java.name)
    }
}