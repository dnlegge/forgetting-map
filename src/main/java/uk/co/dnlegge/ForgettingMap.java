package uk.co.dnlegge;

public interface ForgettingMap<K, V> {

    /**
     * Adds an entry (key-value pair) and records as most recently accessed
     **/
    void add(K key, V value);

    /**
     * Searches Map for given key returning single value, or null.
     * If a value is found, entry is recorded as most recently accessed
     * @return value
     **/
    V find(K key);

    /**
     * Gets current (instantaneous) size of map
     *
     * @return size
     */
    int getSize();

    /**
     * Gets maximum size of map, set at construction
     *
     * @return size
     */
    int getMaxSize();

}
