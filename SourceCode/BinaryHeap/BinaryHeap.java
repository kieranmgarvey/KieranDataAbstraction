package csci2320;

import java.util.function.BiPredicate;

public class BinaryHeap<E> implements PriorityQueue<E> {
  private Object[] heapArray;
  private int size;
  private BiPredicate<E, E> higherPriority;

  /**
   * Constructs a binary heap with a predicate that tells if the first argument is
   * higher priority than the second argument.
   * 
   * @param higherPriority the priority predicate
   */

  public BinaryHeap(BiPredicate<E, E> higherPriority) {
    this.higherPriority = higherPriority;
    this.heapArray = new Object[10];
    this.size = 0;
  }

  private void swap(int i, int j) {
    Object temp = heapArray[i];
    heapArray[i] = heapArray[j];
    heapArray[j] = temp;
  }

  private int parent(int i) {
    return (i - 1) / 2;
  }

  @Override
  public void enqueue(E elem) {
    if (size == heapArray.length) {
      Object[] copyArray = new Object[heapArray.length * 2];
      for (int i = 0; i < heapArray.length; i++) {
        copyArray[i] = heapArray[i];
      }
      heapArray = copyArray;
    }

    int i = size;
    heapArray[i] = elem;
    size++;

    if (i != 0) {
      while (i > 0) {
        int parentIndex = parent(i);
        if (higherPriority.test((E) heapArray[i], (E) heapArray[parentIndex])) {
          swap(i, parentIndex);
          i = parentIndex;
        } else {
          break;
        }
      }
    }
  }

  @Override
  public E dequeue() {
    int c = 0;
    if (isEmpty()) {
      throw new UnsupportedOperationException("Heap is empty");
    }

    E removedElement = (E) heapArray[0];
    heapArray[0] = heapArray[size - 1];
    heapArray[size - 1] = null;
    size--;

    int i = 0;
    while (c == 0) {
      int leftChild = 2 * i + 1;
      int rightChild = 2 * i + 2;
      int largest = i;

      if (leftChild < size) {
        if (higherPriority.test((E) heapArray[leftChild], (E) heapArray[largest])) {
          largest = leftChild;
        }
      }

      if (rightChild < size) {
        if (higherPriority.test((E) heapArray[rightChild], (E) heapArray[largest])) {
          largest = rightChild;
        }
      }
      if (largest != i) {
        swap(i, largest);
        i = largest;
      } else {
        c++;
      }
    }
    return removedElement;
  }

  @SuppressWarnings("unchecked")
  @Override
  public E peek() {
    if (isEmpty()) {
      throw new UnsupportedOperationException("Your heap is empty");
    }
    return (E) heapArray[0];
  }

  @Override
  public boolean isEmpty() {
    if (size == 0)
      return true;
    return false;
  }

  @Override
  public int size() {
    return size;
  }

}
