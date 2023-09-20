package com.krickert.semantic;

import com.krickert.embeddings.SentenceEmbeddingsReply;
import com.krickert.embeddings.SentenceEmbeddingsRequest;
import com.krickert.embeddings.SentenceEmbeddingsServiceGrpc;
import io.grpc.stub.StreamObserver;
import io.micronaut.core.util.StringUtils;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@MicronautTest
class EmbedderGrpcTest {
    private static final Logger log = LoggerFactory.getLogger(EmbedderGrpcTest.class);



    @Inject
    SentenceEmbeddingsServiceGrpc.SentenceEmbeddingsServiceBlockingStub endpoint;

    @Inject
    SentenceEmbeddingsServiceGrpc.SentenceEmbeddingsServiceStub endpoint2;

    @Inject
    EmbeddedApplication<?> application;

    StreamObserver<SentenceEmbeddingsReply> streamObserver = new StreamObserver<>() {
        @Override
        public void onNext(SentenceEmbeddingsReply reply) {
            log.info("RESPONSE, returning embeddings: {}", reply.getVectorList());
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("Not implemented", throwable);
        }

        @Override
        public void onCompleted() {
            log.info("Finished");
        }

        // Override OnError ...
    };

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testServerEndpoint() {
        SentenceEmbeddingsRequest request = SentenceEmbeddingsRequest.newBuilder()
                        .setText("is galdolf a real wizard?").build();
        SentenceEmbeddingsReply reply = endpoint.send(request);
        Assertions.assertEquals(reply.getVectorList().size(), 384);
    }

    @Test
    void testAsyncEndpoint() {
        SentenceEmbeddingsRequest request = SentenceEmbeddingsRequest.newBuilder()
                .setText("why is harry potter popular??").build();
        endpoint2.send(request,streamObserver);

    }

}
