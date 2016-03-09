package uk.co.dnlegge.order;

/**
 * A data structure to record ForgettingOrder
 */
public interface ForgettingOrder<K> {

    /**
     * add a new element to the order by passing its key
     *
     * @param key
     */
    void add(K key);

    /**
     * remove the oldest-accessed element from the order, returning its key
     *
     * @return key
     */
    K removeAndReturnLast();

    /**
     * update an element to most-recently accessed via its key
     *
     * @param key
     */
    void moveToFront(K key);

    /**
     * Returns the instantaneous number of elements recorded in the order
     * (not sychronized)
     *
     * @return size
     */
    int getSize();

}
