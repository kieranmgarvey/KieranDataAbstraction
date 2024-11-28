package csci2320;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DLinkedSeq<E> implements Seq<E> {

  // Declare your node class for a double linked list here.
  private static class Node<E> {
    E data;
    Node<E> prev;
    Node<E> next;

    Node(E data, Node<E> prev, Node<E> next) {
      this.data = data;
      this.prev = prev;
      this.next = next;
    }
  }

  // Put your private data here

  private int size;
  private Node<E> headSent;

  public DLinkedSeq() {
    size = 0;
    headSent = new Node<>(null, null, null);
    headSent.next = headSent;
    headSent.prev = headSent;
  }

  // I'm giving you some helper method to make testing easier.
  @SuppressWarnings("unchecked")
  public static <E> DLinkedSeq<E> of(E... elems) {
    DLinkedSeq<E> ret = new DLinkedSeq<>();
    for (E e : elems)
      ret.add(e);
    return ret;
  }

  public static DLinkedSeq<Integer> ofInt(int... elems) {
    DLinkedSeq<Integer> ret = new DLinkedSeq<>();
    for (Integer e : elems)
      ret.add(e);
    return ret;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null || !(that instanceof DLinkedSeq))
      return false;
    DLinkedSeq<?> thatSeq = (DLinkedSeq<?>) that;
    if (thatSeq.size() != size())
      return false;
    for (Iterator<?> iter1 = thatSeq.iterator(), iter2 = this.iterator(); iter1.hasNext();)
      if (!iter1.next().equals(iter2.next()))
        return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("DLinkedSeq(");
    boolean first = true;
    for (E e : this) {
      if (!first) {
        sb.append(", " + e);
      } else {
        sb.append(e.toString());
        first = false;
      }
    }
    sb.append(")");
    return sb.toString();
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      Node<E> rover = headSent.next;

      @Override
      public boolean hasNext() {
        return rover != headSent;
      }

      @Override
      public E next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        E tmp = rover.data;
        rover = rover.next;
        return tmp;
      }
    };
  }

  @Override
  public E get(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index out of bounds");
    }

    Node<E> rover = headSent.next;
    for (int i = 0; i < index; i++) {
      rover = rover.next;
    }

    return rover.data;
  }

  @Override
  public void set(int index, E elem) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index out of bounds");
    }
    Node<E> rover = headSent.next;
    for (int i = 0; i < index; i++) {
      rover = rover.next;
    }
    rover.data = elem;

  }

  @Override
  public void add(E elem) {
    Node<E> rover = headSent.prev;
    Node<E> Nodenew = new Node<E>(elem, rover, headSent);
    rover.next = Nodenew;
    headSent.prev = Nodenew;
    size++;
  }

  @Override
  public void insert(int index, E elem) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index out of bounds");
    }
    Node<E> rover = headSent.next;
    for (int i = 0; i < index - 1; i++) {
      rover = rover.next;
    }
    Node<E> insertNode = new Node<>(elem, rover, rover.next);
    rover.next.prev = insertNode;
    rover.next = insertNode;
    size++;
  }

  @Override
  public E remove(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index out of bounds");
    }
    Node<E> rover = headSent.next;
    for (int i = 0; i < index; i++) {
      rover = rover.next;
    }
    E temp = rover.data;
    rover.prev.next = rover.next;
    rover.next.prev = rover.prev;
    size--;
    return temp;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public <E2> DLinkedSeq<E2> map(Function<E, E2> f) {
    DLinkedSeq<E2> DLink2 = new DLinkedSeq<>();
    for (E i : this) {
      DLink2.add(f.apply(i));
    }
    return DLink2;
  }

  @Override
  public DLinkedSeq<E> filter(Function<E, Boolean> predicate) {
    DLinkedSeq<E> DLink2 = new DLinkedSeq<>();
    for (E i : this) {
      if (predicate.apply(i)) {
        DLink2.add(i);
      }
    }
    return DLink2;
  }

  @Override
  public DLinkedSeq<E> takeWhile(Function<E, Boolean> predicate) {
    DLinkedSeq<E> DLink2 = new DLinkedSeq<>();
    for (E i : this) {
      if (predicate.apply(i)) {
        DLink2.add(i);
      } else {
        break;
      }
    }
    return DLink2;
  }

  @Override
  public DLinkedSeq<E> dropWhile(Function<E, Boolean> predicate) {
    DLinkedSeq<E> DLink2 = new DLinkedSeq<>();
    boolean foundFalse = false;
    for (E i : this) {
      if (!foundFalse && predicate.apply(i)) {
        continue;
      }
      foundFalse = true;
      DLink2.add(i);
    }
    return DLink2;
  }

  @Override
  public Optional<E> find(Function<E, Boolean> predicate) {
    for (E i : this) {
      if (predicate.apply(i)) {
        return Optional.of(i);
      }
    }
    return Optional.empty();
  }

  @Override
  public <A> A foldLeft(A zero, BiFunction<A, E, A> f) {
    A allLeft = zero;
    for (E element : this) {
      allLeft = f.apply(allLeft, element);
    }
    return allLeft;
  }

  @Override
  public <A> A foldRight(BiFunction<E, A, A> f, A zero) {
    Node<E> rover = headSent.prev;
    A allRight = zero;
    while (rover != headSent) {
      allRight = f.apply(rover.data, allRight);
      rover = rover.prev;
    }
    return allRight;
  }

  @Override
  public void mapped(Function<E, E> f) {
    Node<E> rover = headSent.next;
    while (rover != headSent) {
      rover.data = f.apply(rover.data);
      rover = rover.next;
    }
  }

  @Override
  public void filtered(Function<E, Boolean> predicate) {
    Node<E> rover = headSent.next;
    while (rover != headSent) {
      if (!predicate.apply(rover.data)) {
        rover.prev.next = rover.next;
        rover.next.prev = rover.prev;
        size--;
      }
      rover = rover.next;
    }
  }

  @Override
  public void keepWhile(Function<E, Boolean> predicate) {

  }

  @Override
  public void removeWhile(Function<E, Boolean> predicate) {

  }

}
