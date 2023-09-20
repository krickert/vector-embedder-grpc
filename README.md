## Easily forgettable runnable code that does something cool

The purpose of this project is for demo purposes only.  You won't find production ready code here.  But you will find:

* Self contained java code that calculates embeddings from a pytorch sentence embedding in pure java
* A micronaut project that exposes those vectors as a grpc service and a non-working consul discovery service
* A demo of testcontainers that utilizes docker to download solr 9.3.0 and create a sample set of embeddings along with matching queries
* Good demo use of the SolrJ interface

---
## What does this do?
If you run the unit test:
* Downloads and runs a docker image for solr 9.3.0
* Creates a sample index configured to use vectors for embeddings on use of semantic search
* Adds sample documents to the index which includes vectors
* Creates a few sample queries on the solr server

So it's an end-to-end test that uses a LLM to run the search query.

---
- [Micronaut Maven Plugin documentation](https://micronaut-projects.github.io/micronaut-maven-plugin/latest/)
## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)


## Feature discovery-core documentation

- [Micronaut Discovery Core documentation](https://micronaut-projects.github.io/micronaut-discovery-client/latest/guide/)


## Feature maven-enforcer-plugin documentation

- [https://maven.apache.org/enforcer/maven-enforcer-plugin/](https://maven.apache.org/enforcer/maven-enforcer-plugin/)


