package csci2320;

public class ArrayStack<E> implements Stack<E> {
  private int t = 0;
  private Object[] array;
  private Object holder;

  public ArrayStack() {
    array = new Object[10];
  }

  @Override
  public void push(E elem) {
    if (isFull() == true) {
      Object[] copyArray = new Object[array.length * 2];
      for (int i = 0; i < array.length; i++) {
        copyArray[i] = array[i];
      }
      array = copyArray;
    }
    array[t] = elem;
    t++;
  }

  @Override
  public E pop() {
    if (isEmpty()) {
      return null;
    } else {
      t--;
      return (E) array[t];
    }
  }

  @Override
  public E peek() {
    if (isEmpty()) {
      return null;
    } else {
      return (E) array[t - 1];
    }
  }

  @Override
  public boolean isEmpty() {
    return (t == 0);
  }

  public boolean isFull() {
    return (t == array.length);
  }

}
