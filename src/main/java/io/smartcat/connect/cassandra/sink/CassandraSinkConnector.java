package io.smartcat.connect.cassandra.sink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

/**
 * Cassandra Sink connector.
 */
public class CassandraSinkConnector extends SinkConnector {

    private static final String VERSION = "1";

    private Map<String, String> properties;

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public void start(final Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return CassandraSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(final int maximumTasks) {
        final List<Map<String, String>> configs = new ArrayList<>();
        IntStream.rangeClosed(1, maximumTasks).forEach((i) -> configs.add(properties));
        return configs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }
}
