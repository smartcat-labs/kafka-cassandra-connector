package io.smartcat.connect.cassandra.api;

import java.util.Collection;

import com.datastax.driver.core.Statement;

/**
 * Builds a CQL statement out of object of type {@code <T>}.
 *
 * @param <T> Type of object to be used for building of a CQL statement.
 */
public interface StatementBuilder<T> {

    /**
     * Builds a collection of CQL statements based on <code>keyspace</code> and a collection of objects of type
     * {@code <T>}.
     *
     * @param keyspace Keyspace to be used.
     * @param objects Collection of objects to be used for building CQL statements.
     * @return Collection of CQL statements, or empty collection, never null.
     */
    Collection<Statement> build(String keyspace, Collection<T> objects);
}
