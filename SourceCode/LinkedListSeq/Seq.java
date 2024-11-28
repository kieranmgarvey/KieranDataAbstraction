package csci2320;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Seq<E> extends Iterable<E> {
  // Basic Methods
  /**
   * Returns the value at the given index. Throws an IndexOutOfBoundsExcepetion if it is out of range.
   * @param index the index to get
   * @return the value at that index.
   */
  E get(int index);

  /**
   * Sets the value at a particular index. Throws an IndexOutOfBoundsExcepetion if it is out of range.
   * @param index the index to set
   * @param elem the value to set it to
   */
  void set(int index, E elem);

  /**
   * Add a new element to the end of the sequence. Throws an IndexOutOfBoundsExcepetion if it is out of range.
   * @param elem
   */
  void add(E elem);

  /**
   * Insert a new element at a particular location. Throws an IndexOutOfBoundsExcepetion if it is out of range.
   * @param index the index the new element is inserted at
   * @param elem the element to insert
   */
  void insert(int index, E elem);

  /**
   * Remove the element at a given index and return the value returned. Throws an IndexOutOfBoundsExcepetion 
   * if it is out of range.
   * @param index the index to remove
   * @return the element that was removed
   */
  E remove(int index);

  /**
   * How many elements are in the sequence.
   * @return the number of elements in the sequence
   */
  int size();

  // **************************** High-Order Methods ********************************************
  // ******************************** Non-mutating **********************************************
  /**
   * Creates a new sequence with the values produced by applying the provided function to each
   * element of the currrent sequence.
   * @param <E2> the return type of the function and the content type of the new sequence
   * @param f the function to apply to each element
   * @return a new collection with the result values
   */
  <E2> Seq<E2> map(Function<E, E2> f);

  /**
   * Creates a new sequence that only contains the elements that satisfy the predicate.
   * @param predicate the predicate function to filter on
   * @return a new collection with only the elements for which the predicate was true
   */
  Seq<E> filter(Function<E, Boolean> predicate);

  /**
   * Creates a new sequence that contains the elements from the front of this collection
   * that satisfy the predicate. It stops at the first element that doesn't satisfy the predicate.
   * @param predicate the predicate indicating what to take
   * @return a new sequence with elemments from the front of this sequence
   */
  Seq<E> takeWhile(Function<E, Boolean> predicate);

  /**
   * Creates a new sequence that don't contain the elements from the front of this collection
   * that satisfy the predicate. It starts taking elements at the first element that doesn't satisfy the predicate.
   * @param predicate the predicate indicating what to drop
   * @return a new sequence without elemments from the front of this sequence
   */
  Seq<E> dropWhile(Function<E, Boolean> predicate);

  /**
   * Returns the first element that satisfies the predicate, if one exists.
   * @param predicate the predicate of the value we are looking for
   * @return an optional wrapped around the matching value or an empty optional if nothing matches
   */
  Optional<E> find(Function<E, Boolean> predicate);

  /**
   * Combine the elements of the sequence going from left to right starting with the provided "zero" value.
   * @param <A> the result type of the provided funcion and this operation
   * @param f the function to use to combine the values
   * @param zero the initial value to start with
   * @return the result of combining all the value with that function.
   */
  <A> A foldLeft(A zero, BiFunction<A, E, A> f);

  /**
   * Combine the elements of the sequence going from right to left starting with the provided "zero" value.
   * @param <A> the result type of the provided funcion and this operation
   * @param f the function to use to combine the values
   * @param zero the initial value to start with
   * @return the result of combining all the value with that function.
   */
  <A> A foldRight(BiFunction<E, A, A> f, A zero);

  // ********************************* Mutating *************************************************
  /**
   * Modify all the elements in this sequence to be the output of the provided function.
   * @param f the function used to transform the values
   */
  void mapped(Function<E, E> f);

  /**
   * Modify the current collection to only include values that satisfy the predicate.
   * @param predicate the predicate telling which values to keep
   */
  void filtered(Function<E, Boolean> predicate);

  /**
   * Modify the current collection to only have the elements at the beginning that satisfy the
   * predicate. Don't keep anything after the first element that fails the predicate.
   * @param predicate the predicate indicating which elements from the beginning to keep
   */
  void keepWhile(Function<E, Boolean> predicate);

  /**
   * Modify the current collection to remove all the elements from the beginning that satisfy
   * the predicate. Stop removing elements with the first one that doesn't satisfy the predicate.
   * @param predicate the predicate indicating which elements to remove.
   */
  void removeWhile(Function<E, Boolean> predicate);
}
