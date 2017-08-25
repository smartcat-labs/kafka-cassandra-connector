package io.smartcat.connect.cassandra;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.datastax.driver.core.ConsistencyLevel;

/**
 * Contains configuration for connecting to Cassandra cluster.
 */
public class CassandraConfiguration {

    private static final String CONFIG_PREFX = "connect.cassandra.";
    private static final String CONTACT_POINTS = CONFIG_PREFX + "contact-points";
    private static final String KEYSPACE = CONFIG_PREFX + "keyspace";
    private static final String CONSISTENCY_LEVEL = CONFIG_PREFX + "consistency-level";
    private static final String USERNAME = CONFIG_PREFX + "username";
    private static final String PASSWORD = CONFIG_PREFX + "password";

    private final List<InetSocketAddress> contactPointsWithPorts;
    private final String keyspace;
    private final ConsistencyLevel consistencyLevel;
    private final String username;
    private final String password;

    /**
     * Constructs Cassandra configuration out of properties.
     *
     * @param properties Properties which will be used to construct Cassandra configuration.
     */
    public CassandraConfiguration(final Map<String, String> properties) {
        this.contactPointsWithPorts = getContectPointsWithPorts(properties);
        this.keyspace = getKeyspace(properties);
        this.consistencyLevel = getConsistencyLevel(properties);
        this.username = properties.get(USERNAME);
        this.password = properties.get(PASSWORD);
    }

    /**
     * Returns list of contact points with corresponding ports.
     *
     * @return List of contact points with corresponding ports, never empty list nor null.
     */
    public List<InetSocketAddress> getContactPointsWithPorts() {
        return contactPointsWithPorts;
    }

    /**
     * Returns keyspace to be used.
     *
     * @return Keyspace to be used.
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * Returns consistency level to be used.
     *
     * @return Consistency level to be used.
     */
    public ConsistencyLevel getConsistencyLevel() {
        return consistencyLevel;
    }

    /**
     * Returns username to be used if authentication is needed.
     *
     * @return Username to be used if authentication is needed.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns password to be used if authentication is needed.
     *
     * @return Password to be used if authentication is needed.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Indicates whether credentials are set.
     *
     * @return True if credentials are set, otherwise false.
     */
    public boolean areCredentialsSet() {
        return username != null && password != null;
    }

    private List<InetSocketAddress> getContectPointsWithPorts(final Map<String, String> properties) {
        String contactPoints = (String) properties.get(CONTACT_POINTS);
        if (contactPoints == null || properties.isEmpty()) {
            throw new RuntimeException("Property '" + CONTACT_POINTS + "' cannot be null nor empty.");
        }
        List<InetSocketAddress> result = new ArrayList<>();
        for (String connectionPoint : contactPoints.split(",")) {
            String[] hostnameAndPort = connectionPoint.trim().split(":");
            InetSocketAddress address = new InetSocketAddress(hostnameAndPort[0], Integer.parseInt(hostnameAndPort[1]));
            result.add(address);
        }
        if (result.isEmpty()) {
            throw new RuntimeException("Property '" + CONTACT_POINTS
                    + "' evaluated to zero contact points. Initial raw value is: " + contactPoints);
        }
        return Collections.unmodifiableList(result);
    }

    private String getKeyspace(final Map<String, String> properties) {
        String keyspace = properties.get(KEYSPACE);
        if (keyspace == null || keyspace.isEmpty()) {
            throw new RuntimeException("Property '" + KEYSPACE + "' cannot be null nor empty.");
        }
        return keyspace;
    }

    private ConsistencyLevel getConsistencyLevel(final Map<String, String> properties) {
        String consistencyLevel = properties.get(CONSISTENCY_LEVEL);
        return consistencyLevel == null ? ConsistencyLevel.ONE : ConsistencyLevel.valueOf(consistencyLevel);
    }
}
