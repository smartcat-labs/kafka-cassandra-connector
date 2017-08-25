# kafka-cassandra-connector

kafka-cassandra-connector is implemented using Kafka Connect API, it represents Cassandra Sink.

It exposes two interfaces, `SinkRecordTransformer` and `StatementBuilder`. `CassandraSinkTask` uses implementations of those two interfaces to transform `SinkRecord` into CQL statement which is then sent to Cassandra cluster. Loading of concrete implementations for those two interfaces are done dynamically (see `connect.cassandra.sink-record-transformer-class` and `connect.cassandra.statement-builder-class` properties in [src/example/resources/connector.properties](src/example/resources/connector.properties) file. With this approach, one can use generic connector JAR implementation and just provide different `SinkRecordTransformer` and `StatementBuilder` implementations to classpath for different behaviour.


