package uk.co.dnlegge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.dnlegge.order.ForgettingOrder;
import uk.co.dnlegge.order.ForgettingOrderList;

/**
 * An implementation of a ForgettingMap
 * This implementation uses a Map to store key-value pairs,
 * and a List to record access-order
 */
public class ForgettingMapWrapper<K, V> implements ForgettingMap<K, V> {


    private final int maxSize;
    private final Map<K, V> map;
    private final ForgettingOrder<K> order;

    public ForgettingMapWrapper() {
        this(10);
    }

    /**
     * Instantiates a ForgettingMap with a specified maximum size
     *
     * @param maxSize
     */
    public ForgettingMapWrapper(int maxSize) {
        this.maxSize = maxSize;
        this.map = new ConcurrentHashMap<>();
        this.order = new ForgettingOrderList<>();
    }

    @Override
    /**
     * Synchonised so that cannot run if there is an find being called at the same time
     */
    public void add(K key, V value) {
        // I'm wary of synchronizing at such a high level because of the risk of deadlock,
        // as you should synchronize for as short a time as possible
        // but add and find can't be allowed to be called at the same time
        // particularly as the 'read' function still affects the state
        // use a lock of 'this' to protect across both accesses
        synchronized (this) {
            //let map impl handle duplicate case
            putKeyValuePairIntoMap(key, value);

            removeOldestAccessedElementIfOversized();

            addToOrderList(key);
        }
    }

    private void putKeyValuePairIntoMap(K key, V value) {
        map.put(key, value);
    }

    private void addToOrderList(K key) {
        order.add(key);
    }

    /**
     * checks size does not exceed maximum and removes least-used record if it does
     */
    private void removeOldestAccessedElementIfOversized() {
        if (getSize() > getMaxSize()) {
            final K entryToForget = removeAndReturnOldestAccessedElement();
            removeFromMap(entryToForget);
        }
    }

    private void removeFromMap(K entryToForget) {
        map.remove(entryToForget);
    }

    private K removeAndReturnOldestAccessedElement() {
        return order.removeAndReturnLast();
    }

    @Override
    /**
     * Synchonised so that cannot run if there is an add being called at the same time
     */
    public V find(K key) {
        synchronized (this) {
            final V returnValue = map.get(key);
            if (returnValue != null) {
                order.moveToFront(key);
            }

            return returnValue;
        }
    }

    @Override
    public int getSize() {
        return map.size();
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

}
