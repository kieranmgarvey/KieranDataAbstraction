package csci2320;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.Stack;

public class AVLMap<K extends Comparable<K>, V> implements Map<K, V> {
  // If you use this, you don't have to do null checks on nodes when you
  // get the height. It is static so it can be called from the Node as well.
  private static <K extends Comparable<K>, V> int height(Node<K, V> n) {
    if (n == null)
      return 0;
    return n.height;
  }

  // Put your Node class and private data up here.

  private Node<K, V> root;
  private int numElems;

  private static class Node<K, V> {
    int height;
    K key;
    Node<K, V> left;
    Node<K, V> right;
    Node<K, V> parent;
    V value;

    Node(K key, V value) {
      this.key = key;
      this.value = value;
      this.left = null;
      this.right = null;
      this.parent = null;
      this.height = 1;
    }

  }

  public void AVLMap() {
    root = null;
    numElems = 0;
  }

  // I'm giving you some helper method to make testing easier.
  @SuppressWarnings("unchecked")
  public static <K extends Comparable<K>, V> AVLMap<K, V> of(Map.KeyValuePair<K, V>... elems) {
    AVLMap<K, V> ret = new AVLMap<>();
    for (var kvp : elems)
      ret.put(kvp.key(), kvp.value());
    return ret;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null || !(that instanceof AVLMap))
      return false;
    AVLMap<?, ?> thatSeq = (AVLMap<?, ?>) that;
    if (thatSeq.size() != size())
      return false;
    for (Iterator<?> iter1 = thatSeq.iterator(), iter2 = this.iterator(); iter1.hasNext();)
      if (!iter1.next().equals(iter2.next()))
        return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("BSTMap(");
    boolean first = true;
    for (var kvp : this) {
      if (!first) {
        sb.append(", " + kvp.key() + "->" + kvp.value());
      } else {
        sb.append(kvp.key() + "->" + kvp.value());
        first = false;
      }
    }
    sb.append(")");
    return sb.toString();
  }

  /**
   * Return the height of the tree. This is just for testing purposes, but my
   * tests call it.
   * 
   * @return The height of the tree.
   */
  public int treeHeight() {
    return height(root);
  }

  @Override
  public Iterator<KeyValuePair<K, V>> iterator() {
    return new BSTIterator(root);
  }

  private class BSTIterator implements Iterator<KeyValuePair<K, V>> {
    private Stack<Node<K, V>> stack;

    public BSTIterator(Node<K, V> root) {
      stack = new Stack<>();
      pushLeft(root);
    }

    private void pushLeft(Node<K, V> n) {
      while (n != null) {
        stack.push(n);
        n = n.left;
      }
    }

    public boolean hasNext() {
      return !stack.isEmpty();
    }

    public KeyValuePair<K, V> next() {
      if (!hasNext()) {
        return null;
      }

      Node<K, V> current = stack.pop();
      pushLeft(current.right);
      return new KeyValuePair<>(current.key, current.value);
    }
  }

  @Override
  public Optional<V> put(K key, V value) {
    Node<K, V> newNode = new Node<K, V>(key, value);
    newNode.key = key;
    newNode.value = value;
    if (root == null) {
      root = newNode;
      numElems++;
      return Optional.empty();
    }
    Node<K, V> rover = root;
    while (true) {
      if (key.compareTo(rover.key) < 0) {
        if (rover.left == null) {
          rover.left = newNode;
          newNode.parent = rover;
          numElems++;
          break;
        } else {
          rover = rover.left;
        }
      } else if (key.compareTo(rover.key) > 0) {
        if (rover.right == null) {
          rover.right = newNode;
          newNode.parent = rover;
          numElems++;
          break;
        } else {
          rover = rover.right;
        }
      } else {
        V temp = rover.value;
        rover.value = value;
        return Optional.of(temp);
      }
    }
    updateHeights(newNode);
    rebalance(newNode);
    return Optional.empty();
  }

  @Override
  public Optional<V> get(K key) {
    if (size() == 0) {
      return Optional.empty();
    }

    int compared;
    Node<K, V> current = root;

    while (current != null) {
      compared = key.compareTo(current.key);
      if (compared == 0) {
        return Optional.of(current.value);
      } else if (compared < 0) {
        current = current.left;
      } else {
        current = current.right;
      }
    }

    return Optional.empty();
  }

  @Override
  public V getOrElse(K key, V defaultValue) {
    if (size() == 0) {
      return defaultValue;
    }

    int compared;
    Node<K, V> current = root;

    while (current != null) {
      compared = key.compareTo(current.key);
      if (compared == 0) {
        return current.value;
      } else if (compared < 0) {
        current = current.left;
      } else {
        current = current.right;
      }
    }

    return defaultValue;
  }

  @Override
  public boolean contains(K key) {
    if (size() == 0) {
      return false;
    }

    int compared;
    Node<K, V> current = root;

    while (current != null) {
      compared = key.compareTo(current.key);
      if (compared == 0) {
        return true;
      } else if (compared < 0) {
        current = current.left;
      } else {
        current = current.right;
      }
    }

    return false;
  }

  private Node<K, V> treeMin(Node<K, V> current) {
    while (current.left != null)
      current = current.left;
    return current;
  }

  private V transplant(Node<K, V> FirstNode, Node<K, V> SecondNode) {
    if (FirstNode.parent == null)
      root = SecondNode;
    else if (FirstNode == FirstNode.parent.left)
      FirstNode.parent.left = SecondNode;
    else
      FirstNode.parent.right = SecondNode;
    if (SecondNode != null)
      SecondNode.parent = FirstNode.parent;
    return FirstNode.value;
  }

  private Optional<Node<K, V>> getNode(K key) {
    Node<K, V> rover = root;
    while (rover != null) {
      if (key.compareTo(rover.key) < 0) {
        rover = rover.left;
      } else if (key.compareTo(rover.key) > 0) {
        rover = rover.right;
      } else {
        return Optional.of(rover);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<V> remove(K key) {
    Optional<Node<K, V>> opNode = getNode(key);
    if (opNode.isEmpty()) {
      return Optional.empty();
    }
    Node<K, V> node = opNode.get();
    if (node == null) {
      return Optional.empty();
    }
    Optional<V> result = Optional.of(node.value);
    if (node.left == null && node.right == null) {
      if (node.parent == null) {
        root = null;
      } else if (node.parent.left == node) {
        node.parent.left = null;
      } else {
        node.parent.right = null;
      }
      node = node.parent;
    } else if (node.left == null) {
      node.right.parent = node.parent;
      if (node.parent == null) {
        root = node.right;
      } else if (node.parent.left == node) {
        node.parent.left = node.right;
      } else {
        node.parent.right = node.right;
      }
      node = node.parent;
    } else if (node.right == null) {
      node.left.parent = node.parent;
      if (node.parent == null) {
        root = node.left;
      } else if (node.parent.left == node) {
        node.parent.left = node.left;
      } else {
        node.parent.right = node.left;
      }
      node = node.parent;
    } else {
      Node<K, V> successor = getSuccessor(node);
      node.key = successor.key;
      node.value = successor.value;
      node = successor.parent;
      if (successor.parent.left == successor) {
        successor.parent.left = successor.right;
      } else {
        successor.parent.right = successor.right;
      }
      if (successor.right != null) {
        successor.right.parent = successor.parent;
      }
    }
    updateHeights(node);
    rebalance(node);
    numElems--;
    return result;
  }

  private void updateHeights(Node<K, V> node) {
    while (node != null) {
      node.height = Math.max(height(node.left), height(node.right)) + 1;
      node = node.parent;
    }
  }

  private Node<K, V> rotateLeft(Node<K, V> node) {
    Node<K, V> pivot = node.right;
    pivot.parent = node.parent;
    node.right = pivot.left;
    if (node.right != null) {
      node.right.parent = node;
    }
    pivot.left = node;
    node.parent = pivot;
    if (pivot.parent == null) {
      root = pivot;
    } else if (pivot.parent.left == node) {
      pivot.parent.left = pivot;
    } else {
      pivot.parent.right = pivot;
    }
    updateHeights(node);
    updateHeights(pivot);
    return pivot;
  }

  private Node<K, V> rotateRight(Node<K, V> node) {
    Node<K, V> pivot = node.left;
    pivot.parent = node.parent;
    node.left = pivot.right;
    if (node.left != null) {
      node.left.parent = node;
    }
    pivot.right = node;
    node.parent = pivot;
    if (pivot.parent == null) {
      root = pivot;
    } else if (pivot.parent.left == node) {
      pivot.parent.left = pivot;
    } else {
      pivot.parent.right = pivot;
    }
    updateHeights(node);
    updateHeights(pivot);
    return pivot;
  }

  private void rebalance(Node<K, V> node) {
    while (node != null) {
      int balance = height(node.left) - height(node.right);
      if (balance > 1) {
        if (height(node.left.left) >= height(node.left.right)) {
          node = rotateRight(node);
        } else {
          node.left = rotateLeft(node.left);
          node = rotateRight(node);
        }
      } else if (balance < -1) {
        if (height(node.right.right) >= height(node.right.left)) {
          node = rotateLeft(node);
        } else {
          node.right = rotateRight(node.right);
          node = rotateLeft(node);
        }
      }
      node = node.parent;
    }
  }

  @Override
  public int size() {
    return numElems;
  }

  private Node<K, V> getSuccessor(Node<K, V> node) {
    Node<K, V> rover = node.right;
    while (rover.left != null) {
      rover = rover.left;
    }
    return rover;
  }

  @Override
  public Set<K> keySet() {
    return new Set<K>() {

      @Override
      public Iterator<K> iterator() {
        return new Iterator<K>() {
          Iterator<KeyValuePair<K, V>> iter = AVLMap.this.iterator();

          @Override
          public boolean hasNext() {
            return iter.hasNext();
          }

          @Override
          public K next() {
            return iter.next().key();
          }
        };
      }

      @Override
      public boolean contains(K elem) {
        return AVLMap.this.contains(elem);
      }

      @Override
      public int size() {
        return AVLMap.this.numElems;
      }
    };
  }

  @Override
  public <V2> AVLMap<K, V2> mapValues(Function<V, V2> f) {
    AVLMap<K, V2> newBSTMap = new AVLMap<>();
    for (KeyValuePair<K, V> kvp : this) {
      newBSTMap.put(kvp.key(), f.apply(kvp.value()));
    }
    return newBSTMap;
  }

  @Override
  public Map<K, V> filter(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    AVLMap<K, V> filteredMap = new AVLMap<>();
    for (KeyValuePair<K, V> kvp : this) {
      if (predicate.apply(kvp)) {
        filteredMap.put(kvp.key(), kvp.value());
      }
    }
    return filteredMap;
  }

  @Override
  public Optional<Map.KeyValuePair<K, V>> find(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    Iterator<KeyValuePair<K, V>> iter = iterator();
    while (iter.hasNext()) {
      KeyValuePair<K, V> temp = iter.next();
      if (predicate.apply(temp))
        return Optional.of(temp);
    }
    return Optional.empty();
  }

  @Override
  public <E2> E2 fold(E2 zero, BiFunction<E2, Map.KeyValuePair<K, V>, E2> f) {
    Iterator<KeyValuePair<K, V>> iter = iterator();
    while (iter.hasNext()) {
      KeyValuePair<K, V> temp = iter.next();
      zero = f.apply(zero, temp);
    }
    return zero;
  }

  @Override
  public boolean exists(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    Iterator<KeyValuePair<K, V>> iter = iterator();
    while (iter.hasNext()) {
      KeyValuePair<K, V> temp = iter.next();
      if (predicate.apply(temp))
        return true;
    }
    return false;
  }

  @Override
  public boolean forall(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    Iterator<KeyValuePair<K, V>> iter = iterator();
    while (iter.hasNext()) {
      KeyValuePair<K, V> temp = iter.next();
      if (!predicate.apply(temp))
        return false;
    }
    return true;
  }

  // ----------------------------These are potential helper functions for
  // debugging ---------------------
  // You can modify these if you want. I don't call them in any way. They don't
  // have to be used if you
  // don't need them. I used them in debugging my own implementation.

  // public void inorderPrint() {
  // inorderPrintRecur(root);
  // }

  // private void inorderPrintRecur(Node<K, V> n) {
  // if (n != null) {
  // inorderPrintRecur(n.left);
  // System.out.println(n.key+" -> "+n.value+" ");
  // inorderPrintRecur(n.right);
  // }
  // }

  // public void preorderPrintKeys() {
  // preorderPrintKeysRecur(root);
  // System.out.println();
  // }

  // public void preorderPrintKeysRecur(Node<K, V> n) {
  // if (n != null) {
  // System.out.print("(" + n.key);
  // preorderPrintKeysRecur(n.left);
  // preorderPrintKeysRecur(n.right);
  // System.out.print(")");
  // }
  // }

  // public boolean isConsistent() {
  // return isConsistentRecur(root);
  // }

  // private boolean isConsistentRecur(Node<K, V> n) {
  // if (n == null) return true;
  // if (n.parent != null && n.parent.left != n && n.parent.right != n) {
  // System.out.println("Not a child of parent at "+n.key);
  // return false;
  // }
  // if (n.left != null && n.left.key.compareTo(n.key) >= 0) {
  // System.out.println("Left child not smaller at "+n.key);
  // return false;
  // }
  // if (n.right != null && n.right.key.compareTo(n.key) <= 0) {
  // System.out.println("Right child not larger at "+n.key);
  // return false;
  // }
  // return isConsistentRecur(n.left) && isConsistentRecur(n.right);
  // }

}
