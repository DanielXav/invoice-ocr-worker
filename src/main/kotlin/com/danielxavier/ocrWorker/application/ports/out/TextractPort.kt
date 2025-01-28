package com.danielxavier.ocrWorker.application.ports.out

interface TextractPort {

    suspend fun textract(key: String): List<String>

}