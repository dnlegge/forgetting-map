package uk.co.dnlegge;

public interface ForgettingMap<K, V> {

    int size();

    int maxSize();

    void add(K key, V value);

    V find(K key);

}
