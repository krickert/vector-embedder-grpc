package com.krickert.semantic;

import com.krickert.Vectorizer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class VectorizerTest {

    @Inject
    Vectorizer vectorizer;

    @Test
    void testVetorizerTestInjects() {
        Assertions.assertNotNull(vectorizer);
        Assertions.assertEquals(384, vectorizer.embeddings("Vectorize 4 lyfe").length);
    }
}
