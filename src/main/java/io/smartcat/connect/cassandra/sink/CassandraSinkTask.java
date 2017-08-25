package io.smartcat.connect.cassandra.sink;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

import io.smartcat.connect.cassandra.CassandraConfiguration;
import io.smartcat.connect.cassandra.CassandraConnection;
import io.smartcat.connect.cassandra.api.SinkRecordTransformer;
import io.smartcat.connect.cassandra.api.StatementBuilder;

/**
 * Cassadra Sink task.
 */
public class CassandraSinkTask extends SinkTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraSinkTask.class);
    private static final String VERSION = "1";
    private static final String CONFIG_PREFX = "connect.cassandra.";
    private static final String SINK_RECORD_TRANSFORMER_CLASS = CONFIG_PREFX + "sink-record-transformer-class";
    private static final String STATEMENT_BUILDER_CLASS = CONFIG_PREFX + "statement-builder-class";

    private String keyspace;
    private CassandraConnection cassandraConnection;
    private SinkRecordTransformer<Object> sinkRecordTransformer;
    private StatementBuilder<Object> statementBuilder;

    @Override
    public void start(final Map<String, String> properties) {
        CassandraConfiguration config = new CassandraConfiguration(properties);
        this.keyspace = config.getKeyspace();
        this.cassandraConnection = new CassandraConnection(config);
        this.sinkRecordTransformer = getInstanceOfClass(SINK_RECORD_TRANSFORMER_CLASS, properties);
        this.statementBuilder = getInstanceOfClass(STATEMENT_BUILDER_CLASS, properties);
    }

    @Override
    public void put(final Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            LOGGER.debug("No records read from Kafka.");
            return;
        }
        LOGGER.debug("Processing " + records.size() + " records from Kafka:");
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(records.stream().map(record -> record.value() + "\n").collect(Collectors.joining()));
        }
        Session session = cassandraConnection.getSession();
        Collection<Object> objects = sinkRecordTransformer.transform(records);
        Collection<Statement> statements = statementBuilder.build(keyspace, objects);
        statements.forEach(statement -> session.executeAsync(statement));
    }

    @Override
    public void stop() {
        cassandraConnection.destroy();
    }

    @Override
    public String version() {
        return VERSION;
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstanceOfClass(String property, Map<String, String> properties) {
        String className = properties.get(property);
        if (className == null || className.isEmpty()) {
            throw new RuntimeException("Property '" + property + "' cannot be null nor empty.");
        }
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Class<?> clazz = classLoader.loadClass(className);
            return (T) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
