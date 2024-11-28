package csci2320;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This is the Map interface that you will implement for a few assignments in this
 * course.
 */
public interface Map<K, V> extends Iterable<Map.KeyValuePair<K, V>>, Function<K, V> {
  /**
   * This record type is included for the iterator so that you can iterator through
   * keys and values at the same time. It is also used as an argument for the higher-order
   * functions.
   */
  static record KeyValuePair<K, V>(K key, V value) {}

  /**
   * This is just a shortcut so you can make pairs with Map.kvp(key, value).
   * @param <K> key type
   * @param <V> value type 
   * @param key the key
   * @param value the value
   * @return a new KeyValuePair
   */
  static <K, V> KeyValuePair<K, V> kvp(K key, V value) {
    return new KeyValuePair<K,V>(key, value);
  }

  /**
   * Associates a value with a key. If the key is already present, it will replace the
   * value and return the old one. If the key is not already present, this pair is added and
   * Optional.empty is returned.
   * @param key the key in the pair
   * @param value the value associated with the key
   * @return an Optional of the previous value if present, otherwise Optional.empty.
   */
  Optional<V> put(K key, V value);

  /**
   * Get the value associated with this key. If none exists, returns Optional.empty.
   * @param key the key to be found
   * @return an Optional with the value if it is in the Map, otherwise Optional.empty.
   */
  Optional<V> get(K key);

  /**
   * Get the value associated with this key or return a default if it isn't found.
   * @param key the key to look for
   * @param defaultValue the value to return if the key is not present
   * @return either the value associated with the key or the provided default
   */
  V getOrElse(K key, V defaultValue);

  /**
   * Check if the map contains the provided key.
   * @param key the key to look for
   * @return true if it is present, otherwise false
   */
  boolean contains(K key);

  /**
   * Remove the provided key and return the associated value if it is present, otherwise
   * return Optional.empty.
   * @param key the key to be removed
   * @return either the value associated with that key or Optional.empty
   */
  Optional<V> remove(K key);

  /**
   * The number of elements currently stores in this Map
   * @return number of elements in the map
   */
  int size();

  /**
   * Returns an immutable set that is a view of the keys for this Map.
   * @return a set of the keys in this Map
   */
  Set<K> keySet();

  // **************************** High-Order Methods ********************************************
  // ******************************** Non-mutating **********************************************
  /**
   * Creates a new Map with the values produced by applying the provided function to each
   * element of the currrent sequence.
   * @param <V2> the return value type of the function and the value type of the new Map
   * @param f the function to apply to each element
   * @return a new collection with the result values
   */
  <V2> Map<K, V2> mapValues(Function<V, V2> f);

  /**
   * Creates a new Map that only contains the elements that satisfy the predicate.
   * @param predicate the predicate function to filter on
   * @return a new collection with only the elements for which the predicate was true
   */
  Map<K, V> filter(Function<Map.KeyValuePair<K, V>, Boolean> predicate);

  /**
   * Returns the first element in the order given by the iterator that satisfies the predicate, if one exists.
   * @param predicate the predicate of the key/value we are looking for
   * @return an optional wrapped around the matching key/value or an empty optional if nothing matches
   */
  Optional<Map.KeyValuePair<K, V>> find(Function<Map.KeyValuePair<K, V>, Boolean> predicate);

  /**
   * Combine the elements of the sequence it iterator order starting with the provided "zero" value.
   * @param <E2> the result type of the provided funcion and this operation
   * @param f the function to use to combine the values
   * @param zero the initial value to start with
   * @return the result of combining all the key/value with that function.
   */
  <E2> E2 fold(E2 zero, BiFunction<E2, Map.KeyValuePair<K, V>, E2> f);

  /**
   * Tells if there is a key/value pair in this Map that satisfies the given predicate.
   * @param predicate the function we want to satisfy
   * @return a boolean telling if an appropriate key/value was found
   */
  boolean exists(Function<Map.KeyValuePair<K, V>, Boolean> predicate);

  /**
   * Tells if all the key/value pairs in this map satisfy the given predicate.
   * @param predicate the function we want satisfied
   * @return a boolean telling is all the key/values satisfy the predicate
   */
  boolean forall(Function<Map.KeyValuePair<K, V>, Boolean> predicate);

}
