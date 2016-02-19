package uk.co.dnlegge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgettingMapWrapper<K, V> implements ForgettingMap<K, V> {

    private final int maxSize;
    private final Map<K, V> map;
    private final List<K> forgettingOrder;

    public ForgettingMapWrapper() {
        this(10);
    }

    public ForgettingMapWrapper(int maxSize) {
        this.maxSize = maxSize;
        this.map = new HashMap<>();
        this.forgettingOrder = new ArrayList<>(maxSize);
    }

    public void add(K key, V value) {

        //let map impl handle duplicate case
        map.put(key, value);

        final int size = map.size();
        if (size > maxSize) {
            final K entryToForget = forgettingOrder.get(size - 2);
            map.remove(entryToForget);
            forgettingOrder.remove(size - 2);
        }

        forgettingOrder.add(0, key);
    }

    public V find(K key) {
        if (map.containsKey(key)) {
            for (K thisKey : forgettingOrder) {
                if (key.equals(thisKey)) {
                    forgettingOrder.remove(key);
                    forgettingOrder.add(0, key);
                    //operation complete - need to break here to avoid concurrency error
                    break;
                }
            }
        }

        return map.get(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public int maxSize() {
        return maxSize;
    }
}
