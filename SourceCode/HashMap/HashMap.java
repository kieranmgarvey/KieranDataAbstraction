package csci2320;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HashMap<K, V> implements Map<K, V> {

  private static class Node<K, V> {
    K key;
    V value;
    Node<K, V> next;

    Node(K key, V value, Node<K, V> next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }
  }

  @SuppressWarnings("unchecked")
  private Node<K, V>[] table = new Node[10];
  private int numElems = 0;

  // I'm giving you some helper method to make testing easier.
  @SuppressWarnings("unchecked")
  public static <K, V> HashMap<K, V> of(Map.KeyValuePair<K, V>... elems) {
    HashMap<K, V> ret = new HashMap<>();
    for (var kvp : elems)
      ret.put(kvp.key(), kvp.value());
    return ret;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null || !(that instanceof HashMap))
      return false;
    HashMap<?, ?> thatSeq = (HashMap<?, ?>) that;
    if (thatSeq.size() != size())
      return false;
    for (Iterator<?> iter1 = thatSeq.iterator(), iter2 = this.iterator(); iter1.hasNext();)
      if (!iter1.next().equals(iter2.next()))
        return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("HashMap(");
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

  // adding this method to make generating hashcodes easier
  public int hasher(K key) {
    return Math.abs(key.hashCode() % table.length);
  }

  @Override
  public Iterator<KeyValuePair<K, V>> iterator() {
    return new Iterator<KeyValuePair<K, V>>() {
      int currentIndex = -1;
      Node<K, V> currentNode = null;

      @Override
      public boolean hasNext() {
        if (currentNode != null && currentNode.next != null) {
          return true;
        }
        for (int i = currentIndex + 1; i < table.length; i++) {
          if (table[i] != null) {
            return true;
          }
        }
        return false;
      }

      @Override
      public KeyValuePair<K, V> next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        if (currentNode == null || currentNode.next == null) {
          while (++currentIndex < table.length && table[currentIndex] == null)
            ;
          if (currentIndex < table.length) {
            currentNode = table[currentIndex];
          } else {
            throw new NoSuchElementException();
          }
        } else {
          currentNode = currentNode.next;
        }
        return new KeyValuePair<>(currentNode.key, currentNode.value);
      }
    };
  }

  @Override
  public Optional<V> put(K key, V value) {
    int index = hasher(key);
    Node<K, V> currentNode = table[index];
    if (currentNode == null) {
      table[index] = new Node<>(key, value, null);
      numElems++;
      return Optional.empty();
    }
    while (currentNode != null) {
      if (currentNode.key.equals(key)) {
        V oldValue = currentNode.value;
        currentNode.value = value;
        return Optional.of(oldValue);
      }
      if (currentNode.next == null) {
        break;
      }
      currentNode = currentNode.next;
    }
    currentNode.next = new Node<>(key, value, null);
    numElems++;
    return Optional.empty();
  }

  @Override
  public Optional<V> get(K key) {
    int index = hasher(key);
    Node<K, V> currentNode = table[index];
    while (currentNode != null) {
      if (currentNode.key.equals(key)) {
        return Optional.of(currentNode.value);
      }
      currentNode = currentNode.next;
    }
    return Optional.empty();
  }

  @Override
  public V getOrElse(K key, V defaultValue) {
    Optional<V> value = get(key);
    return value.orElse(defaultValue);
  }

  @Override
  public boolean contains(K key) {
    int index = hasher(key);
    Node<K, V> currentNode = table[index];
    while (currentNode != null) {
      if (currentNode.key.equals(key)) {
        return true;
      }
      currentNode = currentNode.next;
    }
    return false;
  }

  @Override
  public Optional<V> remove(K key) {
    int index = hasher(key);
    Node<K, V> current = table[index];
    if (current != null && current.key.equals(key)) {
      table[index] = current.next;
      numElems--;
      return Optional.of(current.value);
    }
    while (current != null && current.next != null) {
      if (current.next.key.equals(key)) {
        Node<K, V> removed = current.next;
        current.next = removed.next;
        numElems--;
        return Optional.of(removed.value);
      }
      current = current.next;
    }
    return Optional.empty();
  }

  @Override
  public int size() {
    return numElems;
  }

  @Override
  public Set<K> keySet() {
    return new Set<K>() {
      @Override
      public boolean contains(K elem) {
        return HashMap.this.contains((K) elem);
      }

      @Override
      public int size() {
        return HashMap.this.size();
      }

      @Override
      public Iterator<K> iterator() {
        return new Iterator<K>() {
          int currentIndex = -1;
          Node<K, V> currentNode = null;

          @Override
          public boolean hasNext() {
            if (currentNode != null && currentNode.next != null) {
              return true;
            }
            for (int i = currentIndex + 1; i < table.length; i++) {
              if (table[i] != null) {
                return true;
              }
            }
            return false;
          }

          @Override
          public K next() {
            if (!hasNext()) {
              throw new NoSuchElementException();
            }
            if (currentNode == null || currentNode.next == null) {
              while (++currentIndex < table.length && table[currentIndex] == null)
                ;
              if (currentIndex < table.length) {
                currentNode = table[currentIndex];
              } else {
                throw new NoSuchElementException();
              }
            } else {
              currentNode = currentNode.next;
            }
            return currentNode.key;
          }
        };
      }
    };
  }

  @Override
  public <V2> HashMap<K, V2> mapValues(Function<V, V2> f) {
    HashMap<K, V2> result = new HashMap<>();
    for (var kvp : this) {
      result.put(kvp.key(), f.apply(kvp.value()));
    }
    return result;
  }

  @Override
  public Map<K, V> filter(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    HashMap<K, V> result = new HashMap<>();
    for (var kvp : this) {
      if (predicate.apply(kvp)) {
        result.put(kvp.key(), kvp.value());
      }
    }
    return result;
  }

  @Override
  public Optional<Map.KeyValuePair<K, V>> find(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (var kvp : this) {
      if (predicate.apply(kvp)) {
        return Optional.of(kvp);
      }
    }
    return Optional.empty();
  }

  @Override
  public <E2> E2 fold(E2 zero, BiFunction<E2, Map.KeyValuePair<K, V>, E2> f) {
    E2 combined = zero;
    for (var kvp : this) {
      combined = f.apply(combined, kvp);
    }
    return combined;
  }

  @Override
  public boolean exists(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (var kvp : this) {
      if (predicate.apply(kvp)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean forall(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (var kvp : this) {
      if (!predicate.apply(kvp)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Lets this object work as a function from K to V. Gets the value if the key
   * exists.
   * Throws an exception if it doesn't.
   * 
   * @param key the key to look up in the collection
   * @return the associated value
   */
  @Override
  public V apply(K key) {
    Optional<V> value = get(key);
    if (value.isPresent()) {
      return value.get();
    } else {
      throw new NoSuchElementException();
    }
  }
}
