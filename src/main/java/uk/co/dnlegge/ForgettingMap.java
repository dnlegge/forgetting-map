package uk.co.dnlegge;

public interface ForgettingMap<K, V> {

    /**
     * Adds an entry (key-value pair) and records as most recently accessed
     * @param key
     * @param value
     **/
    void add(K key, V value);

    /**
     * Searches Map for given key returning single value, or null.
     * If a value is found, entry is recorded as most recently accessed
     * @param key
     * @return value
     **/
    V find(K key);

    /**
     * Gets size of map
     * Synchronized so has guarantee of consistency
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

    /**
     * Runs self check on contents
     */
    void validate();

}
