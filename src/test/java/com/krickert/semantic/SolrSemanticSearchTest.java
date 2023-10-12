package com.krickert.semantic;

import com.krickert.Vectorizer;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.SolrContainer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@MicronautTest
public class SolrSemanticSearchTest {
    private String SOLR_IMAGE = "solr:9.3.0";
    private SolrContainer container = createContainer();

    @Inject
    Vectorizer vectorizer;

    String[] docs =
            {
                    "Watermelons are red.",
                    "I like watermelons for breakfast.",
                    "Saffron is a wonderful spice for soup.",
                    "Hollywood writers are demanding higher wages",
                    "The fruit is large, green outside and red inside with seeds.",
                    "Skyscrapers are taller than most buildings",
                    "Construction workers did not cross the picket line",
                    "A bowling score of 300 is a terrible score",
                    "bat, ball, mitt, diamond, park"
            };



    private SolrContainer createContainer() {
        // Create the solr container.
        SolrContainer container = new SolrContainer(SOLR_IMAGE);
        // Start the container. This step might take some time...
        container.start();

        return container;
    }


    private SolrClient createSolrClient() {
        return new Http2SolrClient.Builder("http://" + container.getHost() + ":" + container.getSolrPort() + "/solr").build();
    }

    @Test
    void testPing() throws SolrServerException, IOException {
        SolrClient client = createSolrClient();
        client.ping("dummy");
    }

    @Test
    void testSemanticSearch() throws SolrServerException, IOException {
        SolrClient client = createSolrClient();
        uploadConfigSet(client);
        //create the collection
        client.request(CollectionAdminRequest.createCollection("fruit-docs", "semantic_simple",1, 1));
        //now we have a valid semantic search collection loaded... let's add some docs!
        addVectorizedDocs(client);
        // Keyword query demo
        keywordQueryDemo(client);
        // semantic search!
        performSemanticQuery(client, "watermelon");
        performSemanticQuery(client, "universal studios");
        performSemanticQuery(client, "architecture");
        performSemanticQuery(client, "strike");
        performSemanticQuery(client, "baseball");


        // Close the SolrClient instance.
        client.close();

    }

    private void performSemanticQuery(SolrClient client, String semanticQuery) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        // Set the query string.
        query.setQuery("*:*");
        Collection<Float> embeddings =
                createEmbeddings(vectorizer.embeddings(semanticQuery));
        String fq = "{!knn f=vector topK=3}"+ embeddings;
        String[] fq1 = {fq};
        query.setFilterQueries(fq1);
        query.setShowDebugInfo(true);
        // Execute the query.
        QueryResponse response = client.query("fruit-docs", query);
        // Get the results.
        List<SolrDocument> results = response.getResults();
        printResults(results, semanticQuery);
    }

    private void addVectorizedDocs(SolrClient client) throws SolrServerException, IOException {
        for(int i = 0; i < docs.length; i++) {
            addDoc(docs[i], i, client);
        }
        client.commit("fruit-docs");
    }

    private static void keywordQueryDemo(SolrClient client) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        // Set the query string.
        query.setQuery("text:watermelons");
        // Execute the query.
        QueryResponse response = client.query("fruit-docs", query);
        // Get the results.
        List<SolrDocument> results = response.getResults();

        // Print the results.
        printResults(results, "watermelons (keyword search) ");
    }

    private static void printResults(List<SolrDocument> results, String query) {
        System.out.println("**** " + query + "****");
        for (SolrDocument document : results) {
            System.out.println("\t" + document.getFieldValue("id"));
            System.out.println("\t" + document.getFieldValue("text"));
        }
    }

    private void addDoc(String doc, int i, SolrClient client) throws SolrServerException, IOException {
        SolrInputDocument solrDoc = new SolrInputDocument();
        solrDoc.addField("id", i);
        solrDoc.addField("text", docs[i]);

        solrDoc.addField("vector", createEmbeddings(vectorizer.embeddings(docs[i])));
        client.add("fruit-docs", solrDoc);
    }

    private Collection<Float> createEmbeddings(float[] embeddings) {
        // Create an empty array list of floats.
        List<Float> returnVal = new ArrayList<>(embeddings.length);
        // Iterate over the primitive float array and add each element to the array list of floats.
        for (float f : embeddings) {
            returnVal.add(f);
        }
        return returnVal;
    }

    private static void uploadConfigSet(SolrClient client) throws SolrServerException, IOException {
        ConfigSetAdminRequest.Upload request = new ConfigSetAdminRequest.Upload();
        request.setConfigSetName("semantic_simple");

        ResourceResolver resolver = new ResourceResolver();
        File resource = new File(resolver.getResource("classpath:semantic_example.zip").get().getFile());

        request.setUploadFile(resource, "zip" );


        // Execute the request
        ConfigSetAdminResponse response = request.process(client);


        // Check the response status
        if (response.getStatus() == 0) {
            System.out.println("Configset uploaded successfully!");
        } else {
            System.out.println("Error uploading configset: " + response);
        }
    }

}
