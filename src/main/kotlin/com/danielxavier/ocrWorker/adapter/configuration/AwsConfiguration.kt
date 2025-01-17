package com.danielxavier.ocrWorker.adapter.configuration

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.textract.TextractClient

@Configuration
class AwsConfiguration {

    @Bean
    fun sqsClient(): SqsClient {
        return SqsClient.create()
    }

    @Bean(name = ["defaultSqsListenerContainerFactory"])
    fun sqsListenerContainerFactory(sqsAsyncClient: SqsAsyncClient): SqsMessageListenerContainerFactory<Any> {
        return SqsMessageListenerContainerFactory
            .builder<Any>()
            .sqsAsyncClient(sqsAsyncClient)
            .configure { it.acknowledgementMode(AcknowledgementMode.MANUAL) }
            .build()
    }

    @Bean
    fun textractClient(): TextractClient {
        return TextractClient.create()
    }

}