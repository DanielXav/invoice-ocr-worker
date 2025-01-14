package com.danielxavier.ocrWorker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OcrWorkerApplication

fun main(args: Array<String>) {
	runApplication<OcrWorkerApplication>(*args)
}
