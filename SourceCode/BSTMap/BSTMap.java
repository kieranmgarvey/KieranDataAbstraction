package csci2320;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.Stack;

public class BSTMap<K extends Comparable<K>, V> implements Map<K, V> {
  // Put your Node class and private data up here.

  private Node<K, V> root;

  private static class Node<K, V> {
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
    }
  }

  // I'm giving you some helper method to make testing easier.
  @SuppressWarnings("unchecked")
  public static <K extends Comparable<K>, V> BSTMap<K, V> of(Map.KeyValuePair<K, V>... elems) {
    BSTMap<K, V> ret = new BSTMap<>();
    for (var kvp : elems)
      ret.put(kvp.key(), kvp.value());
    return ret;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null || !(that instanceof BSTMap))
      return false;
    BSTMap<?, ?> thatSeq = (BSTMap<?, ?>) that;
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

  @Override
  public Iterator<KeyValuePair<K, V>> iterator() {
    return new BSTIterator(root);
  }

  private class BSTIterator implements Iterator<KeyValuePair<K, V>> {
    private Node<K, V> nextNode;

    public BSTIterator(Node<K, V> root) {
      nextNode = root;
      goToLeftmost();
    }

    private void goToLeftmost() {
      while (nextNode != null && nextNode.left != null) {
        nextNode = nextNode.left;
      }
    }

    public boolean hasNext() {
      return nextNode != null;
    }

    public KeyValuePair<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Node<K, V> nodeToReturn = nextNode;
      if (nextNode.right != null) {
        nextNode = nextNode.right;
        goToLeftmost();
      } else {
        while (nextNode.parent != null && nextNode == nextNode.parent.right) {
          nextNode = nextNode.parent;
        }
        nextNode = nextNode.parent;
      }
      return new KeyValuePair<>(nodeToReturn.key, nodeToReturn.value);
    }
  }

  @Override
  public Optional<V> put(K key, V value) {
    if (size() == 0) {
      root = new Node<>(key, value);
      return Optional.empty();
    }

    Node<K, V> current = root;
    Node<K, V> parent = null;
    int compared;

    while (current != null) {
      compared = key.compareTo(current.key);
      if (compared == 0) {
        V oldValue = current.value;
        current.value = value;
        return Optional.of(oldValue);
      } else if (compared < 0) {
        parent = current;
        current = current.left;
      } else {
        parent = current;
        current = current.right;
      }
    }

    if (key.compareTo(parent.key) < 0) {
      parent.left = new Node<>(key, value);
    } else {
      parent.right = new Node<>(key, value);
    }

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

  @Override
  public Optional<V> remove(K key) {
    if (size() == 0) {
      return Optional.empty();
    }
    int compared;
    Node<K, V> current = root;
    while (current != null) {
      compared = key.compareTo(current.key);
      if (compared == 0) {
        return Optional.of(treeDelete(current));
      } else if (compared < 0) {
        current = current.left;
      } else {
        current = current.right;
      }
    }
    return Optional.empty();
  }

  private V treeDelete(Node<K, V> FirstNode) {
    V valueDeleted = FirstNode.value;
    if (FirstNode.left == null)
      transplant(FirstNode, FirstNode.right);
    else if (FirstNode.right == null)
      transplant(FirstNode, FirstNode.left);
    else {
      Node<K, V> replacementNode = treeMin(FirstNode.right);
      if (replacementNode != FirstNode.right) {
        transplant(replacementNode, replacementNode.right);
        replacementNode.right = FirstNode.right;
      }
      transplant(FirstNode, replacementNode);
      replacementNode.left = FirstNode.left;
    }
    return valueDeleted;
  }

  @Override
  public int size() {
    return size(root);
  }

  private int size(Node<K, V> node) {
    if (node == null) {
      return 0;
    } else {
      int leftSize = size(node.left);
      int rightSize = size(node.right);
      return 1 + leftSize + rightSize;
    }
  }

  @Override
  public Set<K> keySet() {
    return new Set<K>() {
      @Override
      public Iterator<K> iterator() {
        return new Iterator<K>() {
          Iterator<KeyValuePair<K, V>> iterator = BSTMap.this.iterator();

          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public K next() {
            return iterator.next().key();
          }
        };
      }

      @Override
      public int size() {
        return BSTMap.this.size();
      }

      @Override
      public boolean contains(K elem) {
        Node<K, V> current = root;
        while (current != null) {
          int compared = elem.compareTo(current.key);
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
    };

    // Implementation note. This can be done with an anonymous inner class.
    // You can refer to the BSTMap it is in with `BSTMap.this`. So you
    // can call things like `BSTMap.this.contains`.
  }

  @Override
  public <V2> BSTMap<K, V2> mapValues(Function<V, V2> f) {
    BSTMap<K, V2> newBSTMap = new BSTMap<>();
    for (KeyValuePair<K, V> kvp : this) {
      newBSTMap.put(kvp.key(), f.apply(kvp.value()));
    }
    return newBSTMap;
  }

  @Override
  public Map<K, V> filter(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    BSTMap<K, V> filteredMap = new BSTMap<>();
    for (KeyValuePair<K, V> kvp : this) {
      if (predicate.apply(kvp)) {
        filteredMap.put(kvp.key(), kvp.value());
      }
    }
    return filteredMap;
  }

  @Override
  public Optional<Map.KeyValuePair<K, V>> find(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (KeyValuePair<K, V> kvp : this) {
      if (predicate.apply(kvp)) {
        return Optional.of(kvp);
      }
    }
    return Optional.empty();
  }

  @Override
  public <E2> E2 fold(E2 zero, BiFunction<E2, Map.KeyValuePair<K, V>, E2> f) {
    E2 combined = zero;
    for (KeyValuePair<K, V> kvp : this) {
      combined = f.apply(combined, kvp);
    }
    return combined;
  }

  @Override
  public boolean exists(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (KeyValuePair<K, V> kvp : this) {
      if (predicate.apply(kvp)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean forall(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (KeyValuePair<K, V> kvp : this) {
      if (!predicate.apply(kvp)) {
        return false;
      }
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
