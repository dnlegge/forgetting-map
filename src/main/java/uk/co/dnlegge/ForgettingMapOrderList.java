package uk.co.dnlegge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForgettingMapOrderList<K> {
    private final List<K> order;

    public ForgettingMapOrderList() {
        //https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedList-java.util.List-
        this.order = Collections.synchronizedList(new ArrayList<>());
    }

    public void add(K key) {
        order.add(0, key);
    }

    public K removeAndReturnLast() {
        return order.remove(getLastIndex());
    }

    private int getLastIndex() {
        return getSize() - 1;
    }

    public synchronized void moveToFront(K key) {
        for (K thisKey : order) {
            if (key.equals(thisKey)) {
                moveToZeroPosition(key);
                //operation complete - need to break here to avoid concurrency error
                return;
            }
        }
    }

    private void moveToZeroPosition(K key) {
        remove(key);
        add(key);
    }

    private void remove(K key) {
        order.remove(key);
    }

    private int getSize() {
        return order.size();
    }
}
