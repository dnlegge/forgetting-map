package uk.co.dnlegge;

public interface ForgettingMap<K, V> {

    int getSize();

    int getMaxSize();

    void add(K key, V value);

    V find(K key);

}
