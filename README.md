# rcsb-sequence-coordinates

The RCSB PDB GraphQL service for amino/nucleic acid and identifier map among multiple sequence resources. 
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
- `collector`: Contains the methods to fetch alignments and annotations from a MongoDB. Data is always dispatched as streams.

## Application Configuration

Configuration happens through the usual rcsb config method (from the
[rcsb-util](https://github.com/rcsb/rcsb-util) package). Pass the configuration directory
with `-DrcsbConfigProfile=<URL>`. The rcsb-sequence-coordinates config file must be called 
`borrego.app.properties`.

The properties are:

| Property             | Action                                                                              | 
|----------------------|-------------------------------------------------------------------------------------|
| `aw.mongodb.db.name` | Set the DB name for the annotations warehouse DB.                                   |
| `aw.mongodb.uri`     | Set the MongoDB connection URI (including host, username and password) for the DWH. |


## GraphQL schema generation

The GraphQL schema builder is based on the libray:
* [graphql-spqr](https://github.com/leangen/graphql-spqr): creation of GraphQL schema from POJOs
  without annotations. graphql-spqr also provides annotations to implement wiring for the data.
  See class `GraphQLService`. The POJOs used are those in `org.rcsb.mojave.auto` and `org.rcsb.graphqlschema.response` packages.
  The json annotations in those POJOs are ignored by graphql-spqr, 
  for instance camel to snake case is done via a specific transformation implemented by us.  