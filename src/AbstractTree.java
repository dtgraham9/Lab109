/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 import java.util.Iterator;
 import java.util.ArrayList;
 import java.util.List;
/**
 *
 * @author Thompson
 */
/** An abstract base class providing some functionality of the Tree interface. */
public abstract class AbstractTree<E> implements Tree<E> {
    public boolean isInternal(Position<E> p) { return numChildren(p) > 0; } 
    public boolean isExternal(Position<E> p) { return numChildren(p) == 0; }
    public boolean isRoot(Position<E> p) { return p == root( ); }
    public boolean isEmpty( ) { return size( ) == 0; }
    
    /** Returns the number of levels separating Position p from the root. */
    public int depth(Position<E> p) { 
        if (isRoot(p))
            return 0;
        else
            return 1 + depth(parent(p));
    }
    
    /** Returns the height of the tree. */
    private int heightBad( ) { // works, but quadratic worst-case time
    int h = 0;
    for (Position<E> p : positions( ))
        if (isExternal(p)) // only consider leaf positions
            h = Math.max(h, depth(p));
        return h;
    }
    
    /** Returns the height of the subtree rooted at Position p. */
    public int height(Position<E> p) {  
        int h = 0; // base case if p is external
        for (Position<E> c :  children(p))
            h = Math.max(h, 1 + height(c));
        return h;
    }
}
