package csci2320;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.function.BiPredicate;

public class KLargest {
  /**
   * This method finds the k largest elements elements in a list.
   * 
   * @param <E>       the element type in the list
   * @param elems     the list of elements
   * @param k         how many elements to return
   * @param isSmaller a predicate that says if the first argument is smaller than
   *                  the second
   * @return a Set of the k largest values
   */
  public static <E> Set<E> findKLargest(List<E> elems, int k, BiPredicate<E, E> isSmaller) {
    BinaryHeap<E> heap = new BinaryHeap<>(isSmaller);
    for (int i = 0; i < k; i++) {
      heap.enqueue(elems.get(i));
    }
    for (int i = k; i < elems.size(); i++) {
      if (!isSmaller.test(elems.get(i), heap.peek())) {
        heap.dequeue();
        heap.enqueue(elems.get(i));
      }
    }
    Set<E> result = new HashSet<>();
    while (!heap.isEmpty()) {
      result.add(heap.dequeue());
    }

    return result;
  }
}
