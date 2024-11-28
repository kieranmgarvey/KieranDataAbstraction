package csci2320;

public interface Stack<E> {
  void push(E elem);
  E pop();
  E peek();
  boolean isEmpty();  
}
