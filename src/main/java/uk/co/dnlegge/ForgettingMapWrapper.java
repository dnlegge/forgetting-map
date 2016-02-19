package uk.co.dnlegge;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ForgettingMapWrapper<K, V> implements ForgettingMap<K, V> {

    private final int maxSize;
    private final Map<K, V> map;
    private final List<K> forgettingOrder;

    public ForgettingMapWrapper() {
        this(10);
    }

    public ForgettingMapWrapper(int maxSize) {
        this.maxSize = maxSize;
        this.map = new ConcurrentHashMap<>();
        this.forgettingOrder = new CopyOnWriteArrayList<>();
    }

    public void add(K key, V value) {

        //let map impl handle duplicate case
        putKeyValuePairIntoMap(key, value);

        removeOldestAccessedElementIfOversized();

        addKeyAsLatestAccessedElement(key);
    }

    private void putKeyValuePairIntoMap(K key, V value) {
        map.put(key, value);
    }

    private void addKeyAsLatestAccessedElement(K key) {
        forgettingOrder.add(0, key);
    }

    private void removeOldestAccessedElementIfOversized() {
        if (getSize() > getMaxSize()) {
            final K entryToForget = getOldestAccessedElement();
            removeFromMap(entryToForget);
            removeOldestAccessedElement();
        }
    }

    private void removeFromMap(K entryToForget) {
        map.remove(entryToForget);
    }

    private K getOldestAccessedElement() {
        return forgettingOrder.get(getIndexOfLastElement());
    }

    private void removeOldestAccessedElement() {
        forgettingOrder.remove(getIndexOfLastElement());
    }

    private int getIndexOfLastElement() {
        return forgettingOrder.size() - 1;
    }

    public V find(K key) {
        if (map.containsKey(key)) {
            updateLatestAccessOrder(key);
        }

        return map.get(key);
    }

    private void updateLatestAccessOrder(K key) {
        for (K thisKey : forgettingOrder) {
            if (key.equals(thisKey)) {
                moveToZeroPosition(key);
                //operation complete - need to break here to avoid concurrency error
                return;
            }
        }
    }

    private void moveToZeroPosition(K key) {
        removeKeyFromCurrentOrderPosition(key);
        addKeyAsLatestAccessedElement(key);
    }

    private void removeKeyFromCurrentOrderPosition(K key) {
        forgettingOrder.remove(key);
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
