package csci2320;

public class DisjointSet<E> {
  private DisjointSet<E> parent;
  private E element;
  private int rank;

  /**
   * Makes a new disjoint set that represents the provided element type.
   * 
   * @param <E>  The element type.
   * @param elem The representative element for the set.
   * @return A new disjoint set whose only element in the one provided.
   */
  public static <E> DisjointSet<E> makeSet(E elem) {
    return new DisjointSet<>(elem);
  }

  // TODO: Add any member data/constructors here.

  public DisjointSet(E elem) {
    this.element = elem;
    this.parent = this;
    this.rank = 0;
  }

  /**
   * Returns the representative element for the current set. This should be
   * the <code>elem</code> that the set was created with.
   * 
   * @return
   */
  public E getElement() {
    return element;
  }

  /**
   * Union <code>this</code> to <code>that</code>.
   * 
   * @param that the other set to union with the current one.
   */
  public void union(DisjointSet<E> that) {
    DisjointSet<E> thisRoot = this.findSet();
    DisjointSet<E> thatRoot = that.findSet();

    if (thisRoot != thatRoot) {
      if (thisRoot.rank < thatRoot.rank) {
        thisRoot.parent = thatRoot;
      } else {
        thatRoot.parent = thisRoot;
        if (thisRoot.rank == thatRoot.rank) {
          thisRoot.rank++;
        }
      }
    }
  }

  /**
   * Finds the representative set for the current set. Note that this is not
   * the element data. That is done with <code>getElement</code>.
   * 
   * @return the root of the tree this set is part of.
   */
  public DisjointSet<E> findSet() {
    if (this != parent) {
      parent = parent.findSet();
    }
    return parent;
  }
}
