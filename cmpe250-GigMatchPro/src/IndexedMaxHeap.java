import java.util.ArrayList;

public class IndexedMaxHeap {

    private ArrayList<Freelancer> heap;
    //I REMOVED: private MyHashMap<String, Integer> positionMap;

    public IndexedMaxHeap() {
        this.heap = new ArrayList<>();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    private boolean isHigherPriority(int i, int j) {
        return ServiceHelper.isHigherPriority(heap.get(i), heap.get(j));
    }

    private void swap(int i, int j) {
        Freelancer f1 = heap.get(i);
        Freelancer f2 = heap.get(j);

        heap.set(i, f2);
        heap.set(j, f1);

        // OPTIMIZATION: Update indices directly in the object
        f1.heapIndex = j;
        f2.heapIndex = i;
    }

    //my perculate up method for heap operations
    private void siftUp(int i) {
        int parent = (i - 1) / 2;
        while (i > 0 && isHigherPriority(i, parent)) {
            swap(i, parent);
            i = parent;
            parent = (i - 1) / 2;
        }
    }

    //my perculate down method for heap operations
    private void siftDown(int i) {
        int size = heap.size();
        int maxIndex = i;

        while (true) {
            int leftChild = 2 * i + 1;
            int rightChild = 2 * i + 2;

            if (leftChild < size && isHigherPriority(leftChild, maxIndex)) {
                maxIndex = leftChild;
            }

            if (rightChild < size && isHigherPriority(rightChild, maxIndex)) {
                maxIndex = rightChild;
            }

            if (maxIndex == i) {
                break;
            }

            swap(i, maxIndex);
            i = maxIndex;
        }
    }

    //insert function
    public void insert(Freelancer f) {
        // If already in heap, update it
        if (f.heapIndex != -1) {
            update(f);
            return;
        }

        heap.add(f);
        int newIndex = heap.size() - 1;
        f.heapIndex = newIndex;

        siftUp(newIndex);
    }

    //taking the max
    public Freelancer extractMax() {
        if (isEmpty()) return null;

        Freelancer max = heap.get(0);
        int lastIndex = heap.size() - 1;

        swap(0, lastIndex);

        heap.remove(lastIndex);
        max.heapIndex = -1; // Mark as removed

        if (!isEmpty()) {
            siftDown(0);
        }

        return max;
    }

    /*
     * Removes a specific freelancer from the heap in O(log n) time.
     * Uses the stored 'heapIndex' in the Freelancer object to locate the node instantly,
     * swaps it with the last element, removes the last element, and sifts up/down to restore heap property.
     */
    public void remove(Freelancer f) {
        if (f.heapIndex == -1) {
            return; // Not in this heap
        }

        int i = f.heapIndex;
        int lastIndex = heap.size() - 1;

        if (i != lastIndex) {
            swap(i, lastIndex);

            heap.remove(lastIndex);
            f.heapIndex = -1;

            //I swapped an element from the bottom to position i.
            //It might need to go up OR down.
            siftUp(i);
            siftDown(i);
        } else {
            //Removing the last element is easy
            heap.remove(lastIndex);
            f.heapIndex = -1;
        }
    }

    public void update(Freelancer f) {
        if (f.heapIndex == -1) {
            return; //Not in heap, can't update
        }
        int i = f.heapIndex;
        siftUp(i);
        siftDown(i);
    }
}