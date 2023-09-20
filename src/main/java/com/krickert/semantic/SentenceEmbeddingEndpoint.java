package com.krickert.semantic;

import com.krickert.Vectorizer;
import com.krickert.embeddings.SentenceEmbeddingsReply;
import com.krickert.embeddings.SentenceEmbeddingsRequest;
import com.krickert.embeddings.SentenceEmbeddingsServiceGrpc;
import io.grpc.stub.StreamObserver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;


@Singleton
public class SentenceEmbeddingEndpoint extends SentenceEmbeddingsServiceGrpc.SentenceEmbeddingsServiceImplBase {

    @Inject
    Vectorizer vectorizer;

    @Override
    public void send(SentenceEmbeddingsRequest req, StreamObserver<SentenceEmbeddingsReply> responseObserver) {
        SentenceEmbeddingsReply reply =  SentenceEmbeddingsReply.newBuilder()
                .addAllVector(vectorizer.getEmbeddings(req.getText())).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
