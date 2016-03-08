package uk.co.dnlegge.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of a ForgettingOrder
 * This implementation uses a concurrency-protected ArrayList
 * and a List to record access-order
 * <p>
 * Most recently accessed is at zero-index
 */
public class ForgettingOrderList<K> implements ForgettingOrder<K> {
    private final List<K> order;

    public ForgettingOrderList() {
        //see https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedList-java.util.List-
        this.order = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void add(K key) {
        order.add(0, key);
    }

    @Override
    public K removeAndReturnLast() {
        return order.remove(getLastIndex());
    }

    @Override
    public void moveToFront(K key) {
        remove(key);
        add(key);
    }

    @Override
    public int getSize() {
        return order.size();
    }

    private void remove(K key) {
        order.remove(key);
    }

    private int getLastIndex() {
        return getSize() - 1;
    }

}
