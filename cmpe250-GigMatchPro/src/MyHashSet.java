//my own implementation of hashset using hashmaps and linked lists
public class MyHashSet<E> {

    private final MyHashMap<E, Object> map;
    private static final Object DUMMY_VALUE = new Object();

    public MyHashSet() {
        this.map = new MyHashMap<>();
    }

    public void add(E element) {
        map.put(element, DUMMY_VALUE);
    }

    public boolean remove(E element) {
        return map.remove(element) == DUMMY_VALUE;
    }

    public boolean contains(E element) {
        return map.containsKey(element);
    }

    public int size() {
        return map.size();
    }
}