package com.danielxavier.ocrWorker.application.ports.out

interface TextractPort {

    fun textract(key: String): List<String>
}