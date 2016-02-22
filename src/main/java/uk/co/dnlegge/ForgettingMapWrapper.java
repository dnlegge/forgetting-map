package uk.co.dnlegge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ForgettingMapWrapper<K, V> implements ForgettingMap<K, V> {

    private final int maxSize;
    private final Map<K, V> map;
    private final ForgettingOrderList<K> orderList;

    public ForgettingMapWrapper() {
        this(10);
    }

    public ForgettingMapWrapper(int maxSize) {
        this.maxSize = maxSize;
        this.map = new ConcurrentHashMap<>();
        this.orderList = new ForgettingOrderList<>();
    }

    @Override
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
        orderList.add(key);
    }

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
        return orderList.removeAndReturnLast();
    }

    @Override
    public V find(K key) {
        synchronized (this) {
            final V returnValue = map.get(key);
            if (returnValue != null) {
                orderList.moveToFront(key);
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
