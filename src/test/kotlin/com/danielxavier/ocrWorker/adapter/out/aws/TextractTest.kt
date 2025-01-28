package com.danielxavier.ocrWorker.adapter.out.aws

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.services.textract.TextractClient
import software.amazon.awssdk.services.textract.model.BlockType
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse
import java.util.function.Consumer

class TextractTest {

    private lateinit var textractClient: TextractClient
    private lateinit var textract: Textract

    @BeforeEach
    fun setUp() {
        textractClient = mockk(relaxed = true)
        textract = Textract(textractClient, "test")
    }

    @Test
    fun `deve extrair o documento com sucesso`() = runBlocking {

        val mockResponse = mockk<DetectDocumentTextResponse>()

        coEvery {
            textractClient.detectDocumentText(any<Consumer<DetectDocumentTextRequest.Builder>>())
        } returns mockResponse

        coEvery { mockResponse.blocks() } returns listOf(
            mockk {
                every { blockType() } returns BlockType.LINE
                every { text() } returns "Exemplo de linha extraída"
            }
        )

        val result = textract.textract("teste")

        assert(result.isNotEmpty())
        assert(result.contains("Exemplo de linha extraída"))
    }

    @Test
    fun `deve gerar erro em caso de falha`(): Unit = runBlocking {

        coEvery {
            textractClient.detectDocumentText(any<Consumer<DetectDocumentTextRequest.Builder>>())
        } throws Exception()

        assertThrows<Exception> {
            textract.textract("teste")
        }
    }
}