package uk.co.dnlegge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForgettingOrderList<K> {
    private final List<K> order;

    public ForgettingOrderList() {
        //see https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedList-java.util.List-
        this.order = Collections.synchronizedList(new ArrayList<>());
    }

    public void add(K key) {
        order.add(0, key);
    }

    public K removeAndReturnLast() {
        return order.remove(getLastIndex());
    }

    public void moveToFront(K key) {
        for (K thisKey : order) {
            if (key.equals(thisKey)) {
                remove(key);
                add(key);
                //operation complete - need to return here to avoid concurrency error
                return;
            }
        }
    }

    private void remove(K key) {
        order.remove(key);
    }

    private int getSize() {
        return order.size();
    }

    private int getLastIndex() {
        return getSize() - 1;
    }

}
