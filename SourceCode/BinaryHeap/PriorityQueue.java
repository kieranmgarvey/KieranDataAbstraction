package csci2320;

public interface PriorityQueue<E> {
  /**
   * Adds an element.
   * @param elem the element to add
   */
  void enqueue(E elem);

  /**
   * Removes the highest priority element and returns it.
   */
  E dequeue();

  /**
   * Returns the highest priority element.
   */
  E peek();

  /**
   * Tells if the priority queue is empty.
   */
  boolean isEmpty();

  /**
   * Return the number of element in the priority queue.
   */
  int size();
}
