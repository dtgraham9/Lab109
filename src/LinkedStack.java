/**
* A collection of objects that are inserted and removed according to the last-in
* first-out principle. Although similar in purpose, this interface differs from
* java.util.Stack.
*
* @author Michael T. Goodrich
* @author Roberto Tamassia
* @author Michael H. Goldwasser
*/
public class LinkedStack<E> implements Stack<E> {
    private SinglyLinkedList<E> list = new SinglyLinkedList<>( ); // an empty list
    
    public LinkedStack( ) {
    } // new stack relies on the initially empty list
    
    public int size( ) { 
        return list.size( ); 
    }
    
    public boolean isEmpty( ) { 
        return list.isEmpty( ); 
    }
    
    public void push(E element) { 
        list.addFirst(element); 
    }
    
    public E top( ) { 
        return list.first( ); 
    } 
    
    public E pop( ) { 
        return list.removeFirst( ); 
    }
}