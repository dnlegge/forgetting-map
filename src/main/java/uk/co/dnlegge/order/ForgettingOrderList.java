package uk.co.dnlegge.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of a ForgettingOrder
 * This implementation uses a concurrency-protected ArrayList to record access-order
 * <p>
 * Most recently accessed is at zero-index
 */
public class ForgettingOrderList<K> implements ForgettingOrder<K> {
    private final List<K> order;

    /**
     * On creation a new ArrayList is instantiated and wrapped by a Collections.synchornizedList
     */
    public ForgettingOrderList() {
        //see https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedList-java.util.List-
        this.order = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    /**
     * Elements always get added at index zero to record them as most recently accessed
     * Make sure element being added is not duplicate
     */
    public void add(K key) {
        if (order.contains(key)) {
            moveToFront(key);
        } else {
            order.add(0, key);
        }
    }

    @Override
    /***
     * Remove the last (highest number) element in the array
     */
    public K removeAndReturnLast() {
        return order.remove(getLastIndex());
    }

    @Override
    /**
     * Move the given element to index 0 by removing and readding
     */
    public void moveToFront(K key) {
        remove(key);
        add(key);
    }

    @Override
    /**
     * Get the instantaneous size (not synchronized)
     */
    public int getSize() {
        return order.size();
    }

    /**
     * Remove the given key from the list
     * Pleasingly can be done by direct access and not requiring iteration
     *
     * @param key
     */
    private void remove(K key) {
        order.remove(key);
    }

    /**
     * Returns the highest index number in the list
     * @return indexOfLastItem
     */
    private int getLastIndex() {
        return getSize() - 1;
    }

}
