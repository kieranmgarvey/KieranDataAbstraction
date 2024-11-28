package csci2320;

public class ArrayQueue<E> implements Queue<E> {
  private int t = 0;
  private Object[] array;
  private Object holder;

  public ArrayQueue() {
    array = new Object[10];
  }

  @Override
  public void enqueue(E elem) {
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
  public E dequeue() {
    if (isEmpty()) {
      return null;
    }
    else {
      holder = (E) array[0];
      t--;
      Object[] copyArray = new Object[array.length * 2];
        for (int i = 0; i < array.length-1; i++) {
          copyArray[i] = array[i+1];
        }
      array = copyArray;
      return (E) holder;
    }
  }

  @Override
  public E peek() {
    if (isEmpty()) {
      return null;
    } else {
      return (E) array[0];
    }
  }

  @Override
  public boolean isEmpty() {
    if (t == 0) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isFull() {
    if (t == array.length) {
      return true;
    }
    return false;
  }
  
}
