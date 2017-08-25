package io.smartcat.connect.cassandra.api;

import java.util.Collection;

import org.apache.kafka.connect.sink.SinkRecord;

/**
 * Transforms a {@link SinkRecord} object into an object of type {@code <T>}.
 *
 * @param <T> Type of object {@link SinkRecord} will be transformed into.
 */
public interface SinkRecordTransformer<T> {

    /**
     * Transforms a collection of {@link SinkRecord} objects into a collection of objects of type {@code <T>}.
     *
     * @param records Collection of records to transform.
     * @return Collection of transformed objects, or empty collection, never null.
     */
    Collection<T> transform(Collection<SinkRecord> records);
}
