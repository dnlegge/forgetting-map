package uk.co.dnlegge;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ForgettingMapOrderList<K> {
    private final List<K> order;

    public ForgettingMapOrderList() {
        this.order = new CopyOnWriteArrayList<>();
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

    public void moveToFirst(K key) {
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
