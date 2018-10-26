
/**
 * Data Structures & Algorithms 6th Edition
 * Goodrick, Tamassia, Goldwasser
 * Code Fragement 7.7
 */

 import java.util.Iterator;
 import java.util.ArrayList;
 import java.util.List;
 
public interface Position<E> {
    /**
     * Returns the element stored at this position.
     * 
     * @return the stored element
     * @thorws IllegalStateExceptoin if position no longer valid
     */
    E getElement( ) throws IllegalStateException;
}
