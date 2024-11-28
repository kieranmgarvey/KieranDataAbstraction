package csci2320;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class TrieSet implements Iterable<String> {

  private class Node {
    Map<Character, Node> children;
    boolean keyNode;

    public Node() {
      this.children = new HashMap<>();
      this.keyNode = false;
    }
  }

  private Node root;

  public TrieSet() {
    this.root = new Node();
  }

  /**
   * Adds a string to the set.
   */
  public void add(String str) {
    Node current = root;
    for (char c : str.toCharArray()) {
      if (!current.children.containsKey(c)) {
        current.children.put(c, new Node());
      }
      current = current.children.get(c);
    }
    current.keyNode = true;
  }

  /**
   * Removes a string from the set.
   * 
   * @return true if it was removed. false if it wasn't found.
   */
  public boolean remove(String str) {
    Node current = root;
    Stack<Node> stack = new Stack<>();
    for (char c : str.toCharArray()) {
      if (!current.children.containsKey(c)) {
        return false;
      }
      stack.push(current);
      current = current.children.get(c);
    }
    if (current.keyNode) {
      current.keyNode = false;
      while (!stack.isEmpty() && current.children.isEmpty() && !current.keyNode) {
        Node parent = stack.pop();
        parent.children.remove(str.charAt(str.length() - 1));
        current = parent;
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Tells if a value is in the set.
   */
  public boolean contains(String str) {
    Node current = root;
    for (char c : str.toCharArray()) {
      if (!current.children.containsKey(c)) {
        return false;
      }
      current = current.children.get(c);
    }
    return current.keyNode;
  }

  // I'm giving you this iterator because you didn't really want to write one
  // yourself.
  private class TrieIterator implements Iterator<String> {
    private StringBuilder prefix = new StringBuilder();
    private Stack<Iterator<Map.Entry<Character, Node>>> stack = new Stack<>();

    public TrieIterator() {
      pushFirst(root);
    }

    private void pushFirst(Node n) {
      var iter = n.children.entrySet().iterator();
      stack.push(iter);
      if (!n.keyNode) {
        var entry = iter.next();
        prefix.append(entry.getKey().charValue());
        pushFirst(entry.getValue());
      }
    }

    private void advance() {
      if (stack.peek().hasNext()) {
        var iter = stack.peek();
        var entry = iter.next();
        prefix.append(entry.getKey().charValue());
        pushFirst(entry.getValue());
      } else {
        stack.pop();
        if (!stack.isEmpty()) {
          prefix.deleteCharAt(prefix.length() - 1);
          advance();
        }
      }
    }

    @Override
    public boolean hasNext() {
      return !stack.isEmpty();
    }

    @Override
    public String next() {
      var ret = prefix.toString();
      advance();
      return ret;
    }
  }

  /**
   * Returns an iterator that goes through this set.
   */
  @Override
  public Iterator<String> iterator() {
    return new TrieIterator();
  }

  /**
   * Returns the longest prefix of the given string that is in the set.
   */
  public String longestPrefix(String str) {
    Node current = root;
    StringBuilder prefix = new StringBuilder();
    for (char c : str.toCharArray()) {
      if (current.children.containsKey(c)) {
        prefix.append(c);
        current = current.children.get(c);
        if (current.keyNode) {
          prefix.setLength(0);
        }
      } else {
        break;
      }
    }
    return prefix.toString();
  }

  /**
   * Returns a list of all suffixes of the given string for strings in the set.
   * Only return the
   * suffix, not the complete word. So if "valid" is in the set and the string is
   * "val" then
   * "id" should be in the returned list. If the prefix is valid, the return set
   * should include
   * the empty string.
   */

  public Set<String> validSuffixes(String str) {
    Node current = root;
    Set<String> suffixes = new HashSet<>();
    StringBuilder prefix = new StringBuilder();
    for (char c : str.toCharArray()) {
      if (current.children.containsKey(c)) {
        current = current.children.get(c);
        prefix.append(c);
      } else {
        return suffixes;
      }
    }
    if (current.keyNode) {
      suffixes.add("");
    }
    allSuffixes(current, prefix, suffixes);
    return suffixes;
  }

  private void allSuffixes(Node node, StringBuilder suffix, Set<String> suffixes) {
    if (node.keyNode) {
      suffixes.add(suffix.toString());
    }
    for (Map.Entry<Character, Node> entry : node.children.entrySet()) {
      char c = entry.getKey();
      Node nextNode = entry.getValue();
      suffix.append(c);
      allSuffixes(nextNode, suffix, suffixes);
      suffix.deleteCharAt(suffix.length() - 1);
    }
    if (node == root) {
      suffixes.add("");
    }
  }

}
