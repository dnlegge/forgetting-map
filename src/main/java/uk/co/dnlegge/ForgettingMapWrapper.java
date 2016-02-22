package uk.co.dnlegge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ForgettingMapWrapper<K, V> implements ForgettingMap<K, V> {

    private final int maxSize;
    private final Map<K, V> map;
    private final ForgettingMapOrderList<K> orderList;

    public ForgettingMapWrapper() {
        this(10);
    }

    public ForgettingMapWrapper(int maxSize) {
        this.maxSize = maxSize;
        this.map = new ConcurrentHashMap<>();
        this.orderList = new ForgettingMapOrderList<>();
    }

    @Override
    public void add(K key, V value) {
        //let map impl handle duplicate case
        putKeyValuePairIntoMap(key, value);

        removeOldestAccessedElementIfOversized();

        addToOrderList(key);
    }

    private void putKeyValuePairIntoMap(K key, V value) {
        map.put(key, value);
    }

    private void addToOrderList(K key) {
        orderList.add(key);
    }

    private void removeOldestAccessedElementIfOversized() {
        if (getSize() > getMaxSize()) {
            final K entryToForget = removeOldestAccessedElement();
            removeFromMap(entryToForget);
        }
    }

    private void removeFromMap(K entryToForget) {
        map.remove(entryToForget);
    }

    private K removeOldestAccessedElement() {
        return orderList.removeAndReturnLast();
    }

    @Override
    public V find(K key) {
        final V returnValue = map.get(key);
        if (returnValue != null) {
            orderList.moveToFront(key);
        }

        return returnValue;
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
