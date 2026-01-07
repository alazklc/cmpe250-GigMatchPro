import java.util.ArrayList;
import java.util.LinkedList;

//my own implementation of hash map
//all the functions does the job on his name
public class MyHashMap<K, V> {

    private static class HashNode<K, V> {
        K key;
        V value;
        public HashNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private ArrayList<LinkedList<HashNode<K, V>>> buckets;
    private int capacity;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.75;

    public MyHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public MyHashMap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.size = 0;
        this.buckets = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            buckets.add(new LinkedList<>());
        }
    }

    public int size() {
        return size;
    }

    private int getBucketIndex(K key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % capacity;
    }

    public V get(K key) {
        int index = getBucketIndex(key);
        LinkedList<HashNode<K, V>> bucket = buckets.get(index);

        for (HashNode<K, V> node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public void put(K key, V value) {
        int index = getBucketIndex(key);
        LinkedList<HashNode<K, V>> bucket = buckets.get(index);

        for (HashNode<K, V> node : bucket) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }

        bucket.add(new HashNode<>(key, value));
        size++;


        //ı resize to optimize my code.
        if ((double) size / capacity > MAX_LOAD_FACTOR) {
            rehash();
        }
    }

    public V remove(K key) {
        int index = getBucketIndex(key);
        LinkedList<HashNode<K, V>> bucket = buckets.get(index);

        HashNode<K, V> toRemove = null;
        for (HashNode<K, V> node : bucket) {
            if (node.key.equals(key)) {
                toRemove = node;
                break;
            }
        }

        if (toRemove != null) {
            bucket.remove(toRemove);
            size--;
            return toRemove.value;
        }

        return null;
    }

    private void rehash() {
        ArrayList<LinkedList<HashNode<K, V>>> oldBuckets = buckets;

        this.capacity *= 2;
        this.size = 0;
        this.buckets = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            buckets.add(new LinkedList<>());
        }

        for (LinkedList<HashNode<K, V>> oldBucket : oldBuckets) {
            for (HashNode<K, V> node : oldBucket) {
                put(node.key, node.value);
            }
        }
    }

    public ArrayList<V> getAllValues() {
        ArrayList<V> allValues = new ArrayList<>(size);
        for (LinkedList<HashNode<K, V>> bucket : buckets) {
            for (HashNode<K, V> node : bucket) {
                allValues.add(node.value);
            }
        }
        return allValues;
    }
}