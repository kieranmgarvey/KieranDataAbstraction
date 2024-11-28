package csci2320;

import java.util.Arrays; // You will probably find Arrays.copyOf useful for this assignment.
import java.util.Iterator;

public abstract class BTVector<E> implements Iterable<E> {
  /**
   * Create a new vector with this element added to the end.
   * 
   * @param elem the new element
   * @return a new vector that is one bigger
   */
  abstract public BTVector<E> add(E elem);

  /**
   * Gets the value at a particular index.
   */
  abstract public E get(int index);

  /**
   * Returns a new vector where the value at the given location has been changed.
   * Note that
   * the original collection is not altered in any way.
   * 
   * @param index location to change
   * @param elem  value to set it to
   * @return a new vector with one value modified
   */
  abstract public BTVector<E> set(int index, E elem);

  /**
   * Returns the size of the vector.
   */
  abstract public int size();

  /**
   * Returns an iterator that runs through the elements in the vector. In the
   * leaf, this can
   * be your normal iterator through an array with an index. In the internal
   * nodes, the fast
   * approach keeps an index for a child and the iterator for that child. In the
   * next() it
   * calls next() on the child iterator. Then, if the child iterator doesn't have
   * a next after
   * that call, it moves the index forward and creates a new child iterator. It is
   * done when
   * the index reaches the end.
   */
  abstract public Iterator<E> iterator();

  /**
   * This protected method is helpful in add because you need to be able to tell
   * if the next add
   * would overflow your last child. Having this method allows you to know in
   * advance if you need
   * create a new child or, if your last available child is full, if you need to
   * create a new
   * parent.
   * 
   * @return a boolean telling if a node can hold more elements
   */
  abstract protected boolean isFull();

  /**
   * I found this protected method to be helpful. It has a node create a new
   * sibling that
   * works with the same nibble `this` which contains the one provided element.
   * This is
   * needed because when a parent needs a new child, it can ask the child to make
   * a sibling
   * so it is of the right type (leaf/internal).
   * 
   * @param elem the one element in the new sibling
   * @return a new vector node with one element in it.
   */
  abstract protected BTVector<E> makeSibling(E elem);

  /**
   * Our implementation groups bits into groups of 4. This constant is here so you
   * don't have
   * "magic numbers" scattered through your code.
   */
  private static int BITS_IN_ALPHABET = 4;

  /**
   * Creates an empty leaf to begin construction of a vector.
   */
  public static <E> BTVector<E> empty() {
    return new Leaf<E>();
  }

  /**
   * This class represents an internal node in the binary trie.
   */
  private static class Internal<E> extends BTVector<E> {
    // TODO: Add your data here.

    BTVector<E>[] children;
    int nibble;

    // TODO: Include appropriate constructors.
    // My implementation had three constructors. One took a single child. One took
    // an array of
    // children. The last took an array and a new child to add.
    // You can make a BTVector<E>[] by instantiating an array of the raw type and
    // casting it
    // this look ssomething like (BTVector<E>) new BTVector[size].
    public Internal(BTVector<E> child, int nibble) {
      children = (BTVector<E>[]) new BTVector[] { child };
      this.nibble = nibble;
    }

    public Internal(BTVector<E>[] child, int nibble) {
      children = Arrays.copyOf(child, child.length);
      this.nibble = nibble;
    }

    public Internal(BTVector<E>[] child, BTVector<E> newChild, int nibble) {
      children = Arrays.copyOf(child, child.length + 1);
      children[child.length] = newChild;
      this.nibble = nibble;
    }

    public Internal(BTVector<E> child1, BTVector<E> child2, int nibble) {
      children = (BTVector<E>[]) new BTVector[] { child1, child2 };
      this.nibble = nibble;
    }

    @Override
    public BTVector<E> add(E elem) {
      if (isFull()) {
        return new Internal<E>(this, makeSibling(elem), nibble + 1);
      } else if (!children[children.length - 1].isFull()) {
        BTVector<E>[] newChildren = Arrays.copyOf(children, children.length);
        newChildren[newChildren.length - 1] = children[children.length - 1].add(elem);
        return new Internal<E>(newChildren, nibble);
      } else {
        return new Internal<E>(children, children[0].makeSibling(elem), nibble);
      }
    }

    @Override
    public E get(int index) {
      return children[(index >> (nibble * BITS_IN_ALPHABET)) & 0xf].get(index);
    }

    @Override
    public BTVector<E> set(int index, E elem) {
      int i = (index >> (nibble * BITS_IN_ALPHABET)) & 0xf;
      Internal<E> reset = new Internal<E>(children, nibble);
      reset.children[i] = reset.children[i].set(index, elem);
      return reset;
    }

    @Override
    public int size() {
      return ((children.length - 1) << (nibble * BITS_IN_ALPHABET)) + (children[children.length - 1].size());
    }

    @Override
    public Iterator<E> iterator() {
      return new Iterator<E>() {
        int i;
        Iterator<E> iter = children[i].iterator();

        public boolean hasNext() {
          return i < children.length;

        }

        public E next() {
          E temp = iter.next();
          if (!iter.hasNext()) {
            i++;
            if (hasNext()) {
              iter = children[i].iterator();
            }
          }
          return temp;
        }
      };
    }

    @Override
    protected boolean isFull() {
      return children.length == 16 && children[children.length - 1].isFull();
    }

    @Override
    protected BTVector<E> makeSibling(E elem) {
      return new Internal<E>(children[0].makeSibling(elem), nibble);
    }
  }

  /**
   * This class represents a leaf in the binary trie.
   */
  private static class Leaf<E> extends BTVector<E> {
    E[] data;
    int nibble = 0;

    public Leaf() {
      data = (E[]) new Object[0];
    }

    public Leaf(E elem) {
      data = (E[]) new Object[] { elem };
    }

    public Leaf(E[] elemArray) {
      data = Arrays.copyOf(elemArray, elemArray.length);
    }

    public Leaf(E[] elemArray, E elem) {
      data = Arrays.copyOf(elemArray, elemArray.length + 1);
      data[elemArray.length] = elem;
    }

    @Override
    public BTVector<E> add(E elem) {
      if (isFull()) {
        return new Internal<E>(new Leaf<E>(data), makeSibling(elem), nibble + 1);
      }
      return new Leaf<E>(data, elem);
    }

    @Override
    public E get(int index) {
      return data[index & 0xf];
    }

    @Override
    public BTVector<E> set(int index, E elem) {
      Leaf<E> set2 = new Leaf<E>(data);
      set2.data[index & 0xf] = elem;
      return set2;
    }

    @Override
    public int size() {
      return data.length;
    }

    @Override
    public Iterator<E> iterator() {
      return new Iterator<E>() {
        int i;

        public boolean hasNext() {
          return i < data.length;
        }

        public E next() {
          E temp = data[i];
          i++;
          return temp;
        }
      };
    }

    @Override
    protected boolean isFull() {
      return size() == 16;
    }

    @Override
    protected BTVector<E> makeSibling(E elem) {
      return new Leaf<E>(elem);
    }
  }
}
