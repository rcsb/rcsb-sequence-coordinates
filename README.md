# rcsb-sequence-coordinates

The RCSB PDB GraphQL service for amino/nucleic acid and identifier mapping among multiple sequence resources. 
The service integrates pairwise sequence alignments including NCBI (genome and proteins), UniProt and PDB. 
In addition, sequence annotations can be mapped among the different sequence references.
The service exposes blocking and reactive operations.

## Project Architecture

The project is divided in 3 modules:
- `org.rcsb.graphqlschema`: Contains the interface and type definitions for the service endpoints queries and responses.
At project build time GrapQL schema is built from these definitions (`org.rcsb.graphqlschema.schema.SchemaGenerator`).
- `org.rcsb.rcsbsequencecoordinates`: Defines the Spring Boot application based on GraphQL, reactive streams and RSocket.
  - `configuration`: The GraphQL service uses a custom wiring factor `GraphqlWiringFactory`. 
  Data objects are defined as documents (`org.bson.Document`). 
  The wiring factory defines the right way of accessing the GraphQL schema fields in data objects.
  - `controller`: Defines the different GraphQL entry points for alignments and annotations requests. 
  Some of the GrapQL schema fields are mapped to specific entry points using the `@SchemaMapping` annotation. 
  - `collectors`: Contains the methods to fetch alignments and annotations from a MongoDB. Data is always dispatched as streams.

## Application Configuration

Configuration happens through Spring's configs mechanisms. In brief: properties or yaml files can be used, any configuration 
can also be provided via CLI parameter, configurations are composable. See [more info here](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.files).

The properties that are required are:

| Property                         | Action                                                                   | 
|----------------------------------|--------------------------------------------------------------------------|
| `spring.data.mongodb.host`       | The MongoDB host                                                         |
| `spring.data.mongodb.port`       | The MongoDB port                                                         |
| `spring.data.mongodb.database`   | The MongoDB database name where the annotations data lives.              |
| `seqcoords.mongo-db-uri-scheme`  | The MongoDB URI scheme (MongoDB connection string), default is "mongodb" |

The MongoDB username and password must be passed via environment variables. These 2 together with the above will compose the MongoDB URI:
- `MONGO_USER`
- `MONGO_PWD`

## GraphQL schema generation

The GraphQL schema builder is based on the libray:
* [graphql-spqr](https://github.com/leangen/graphql-spqr): creation of GraphQL schema from POJOs
  without annotations. graphql-spqr also provides annotations to implement wiring for the data.
  See class `GraphQLService`. The POJOs used are those in `org.rcsb.mojave.auto` and `org.rcsb.graphqlschema.response` packages.
  The json annotations in those POJOs are ignored by graphql-spqr, 
  for instance camel to snake case is done via a specific transformation implemented by us.  

Contributing
---
All contributions are welcome. Please, make a pull request or open an issue.

License
---

The MIT License

    Copyright (c) 2024 - now, RCSB PDB and contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.