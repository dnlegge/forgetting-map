package uk.co.dnlegge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.dnlegge.order.ForgettingOrder;
import uk.co.dnlegge.order.ForgettingOrderList;

/**
 * An implementation of a ForgettingMap
 * This implementation uses a Map to store key-value pairs,
 * and a ForgettingOrder implementation to record access-order
 */
public class ForgettingMapWrapper<K, V> implements ForgettingMap<K, V> {

    private final int maxSize;
    private final Map<K, V> map;
    private final ForgettingOrder<K> order;

    /**
     * Default constructor - constructs with a maxSize of 10
     */
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
     * In this implementation, kv-pair is added to Map
     * If this makes map oversized then remove oldest-accessed kv-pair
     * Add new key to Ordering mechanism
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

    /**
     * Put key value pair directly in to internal Map
     *
     * @param key
     * @param value
     */
    private void putKeyValuePairIntoMap(K key, V value) {
        map.put(key, value);
    }

    /**
     * Add key to ForgettingOrder
     * @param key
     */
    private void addToOrderList(K key) {
        order.add(key);
    }

    /**
     * checks size does not exceed maximum and removes least-used record if it does
     */
    private void removeOldestAccessedElementIfOversized() {
        if (getMapSize() > getMaxSize()) {
            final K entryToForget = removeAndReturnOldestAccessedElement();
            removeFromMap(entryToForget);
        }
    }

    /**
     * Remove key-value pair directly from internal map using key
     * @param entryToForget
     */
    private void removeFromMap(K entryToForget) {
        map.remove(entryToForget);
    }

    /**
     * Remove oldest-accessed item from ForgettingOrder, returning it
     * @return removedItem
     */
    private K removeAndReturnOldestAccessedElement() {
        return order.removeAndReturnLast();
    }

    @Override
    /**
     * Synchonized so that cannot run if there is an add or getSize being called at the same time
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
    /**
     * Gets the size of the internal Map
     * Also confirms synchronization with ForgettingOrder by confirming sizes match
     */
    public int getSize() {
        synchronized (this) {
            if (order.getSize() != getMapSize()) {
                throw new RuntimeException("Size of Map and Order not in consistent state");
            }
            return getMapSize();
        }
    }

    /**
     * Private accessor using direct call to internal Map
     *
     * @return mapSize
     */
    private int getMapSize() {
        return map.size();
    }

    @Override
    /**
     * Gets maximum size of ForgettingMap set at construction (immutable)
     */
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    /**
     *
     */
    public void validate() {
        synchronized (this) {
            for (K key : map.keySet()) {
                if (!order.contains(key)) {
                    throw new RuntimeException("Validation failed: ForgettingMap in inconsistent state");
                }
            }
        }
    }

}
