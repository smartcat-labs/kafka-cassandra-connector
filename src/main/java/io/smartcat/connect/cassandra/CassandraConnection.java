package io.smartcat.connect.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;

/**
 * Represents connection to Cassandra cluster.
 */
public class CassandraConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraConnection.class);

    private final CassandraConfiguration configuration;
    private Cluster cluster;
    private Session session;

    /**
     * Constructs Cassandra connection based on Cassandra configuration.
     *
     * @param configuration Cassandra configuration to be used for this connection.
     */
    public CassandraConnection(final CassandraConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Acquires connection and session to Cassandra cluster based on provided configuration if necessary and returns it.
     * Subsequent invokes will use same session object, in order to create new session object one should invoke
     * {@link #destroy()} first and than invoke this method again.
     *
     * @return Session to Cassandra cluster.
     */
    public Session getSession() {
        if (session == null) {
            connect();
        }
        return session;
    }

    /**
     * Destroys connection, disconnecting from Cassandra cluster.
     */
    public void destroy() {
        if (!session.isClosed()) {
            LOGGER.info("Closing session to Cassandra cluster.");
            session.close();
            session = null;
        }
        if (!cluster.isClosed()) {
            LOGGER.info("Closing connection to Cassandra cluster.");
            cluster.close();
            cluster = null;
        }
    }

    private void connect() {
        LOGGER.info("Connecting to " + configuration.getContactPointsWithPorts() + " nodes of Cassandra cluster.");
        this.cluster = createCluster();
        LOGGER.info("Acquiring session to Cassandra cluster.");
        this.session = cluster.connect();
    }

    private Cluster createCluster() {
        Cluster.Builder builder = Cluster.builder().addContactPointsWithPorts(configuration.getContactPointsWithPorts())
                .withQueryOptions(new QueryOptions().setConsistencyLevel(configuration.getConsistencyLevel()));

        if (configuration.areCredentialsSet()) {
            builder = builder.withCredentials(configuration.getUsername(), configuration.getPassword());
        }

        return builder.build();
    }
}
