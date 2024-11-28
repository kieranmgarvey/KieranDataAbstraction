package csci2320;

/**
 * This interface represents a basic immutable set. It is being added to this assignment to
 * provide a set view to the keys of a Map.
 */
public interface Set<E> extends Iterable<E>{
  boolean contains(E elem);
  int size();
}
