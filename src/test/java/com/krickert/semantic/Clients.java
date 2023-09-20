package com.krickert.semantic;

import com.krickert.embeddings.SentenceEmbeddingsServiceGrpc;
import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.micronaut.grpc.server.GrpcServerChannel;
import jakarta.inject.Singleton;

@Factory
public class Clients {
    @Bean
    SentenceEmbeddingsServiceGrpc.SentenceEmbeddingsServiceBlockingStub blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME)
            ManagedChannel channel) {
        return SentenceEmbeddingsServiceGrpc.newBlockingStub(
                channel
        );
    }

    @Singleton
    @Bean
    SentenceEmbeddingsServiceGrpc.SentenceEmbeddingsServiceStub embeddingsStub(
            @GrpcChannel(GrpcServerChannel.NAME)
            ManagedChannel channel) {
        return SentenceEmbeddingsServiceGrpc.newStub(
                channel
        );
    }

}
