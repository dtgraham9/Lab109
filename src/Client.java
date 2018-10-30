
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Graham
 */
public class Client {
        
    public static String[][] opsByPrecedence = {{"+","-"},{"*","/"}};
    
    private static boolean isOp(String s) {
        for (int i = 0; i < opsByPrecedence.length; i++) {
            for (int j = 0; j < opsByPrecedence[i].length; j++) {
                if (s.equals(opsByPrecedence[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    
    private static int getPrecedence(String op) {
        for (int i = 0; i < opsByPrecedence.length; i++) {
            for (int j = 0; j < opsByPrecedence[i].length; j++) {
                if (op.equals(opsByPrecedence[i][j])) {
                    return i;
                }
            }
        }
        throw new RuntimeException("Invalid op specified (" + op + ")");
    }
    
    public static String toPostFix(String expression) {
        
        String[] tokens = expression.split(" ");
        LinkedStack ops = new LinkedStack();
        
        String postFixStr = "";
        
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("(")) {
                ops.push(tokens[i]);
            }
            else if (tokens[i].equals(")")) {
                while (! ops.top().equals("(")) {
                    postFixStr += ops.pop() + " ";
                }
                ops.pop(); //pop the remaining "(" and throw it away
            }
            else if (! isOp(tokens[i])) {
                postFixStr += tokens[i] + " ";
            }
            else { // tokens[i] is an operator...
                
                boolean tokenProcessed = false; // we might have some work to do first before
                                                // we can push this token...
                
                while ( ! tokenProcessed ) {
                    if (ops.isEmpty() || ops.top().equals("(")) {  
                        ops.push(tokens[i]);
                        tokenProcessed = true;
                    }
                    else {
                        String topOp = (String) ops.top();
                        
                        if ((getPrecedence(tokens[i]) > getPrecedence(topOp)) ||
                            ((getPrecedence(tokens[i]) == getPrecedence(topOp))                                    )) {
                            ops.push(tokens[i]);
                            tokenProcessed = true;
                        }
                        else {
                            postFixStr += ops.pop() + " ";
                        } 
                    } 
                } 
            } 
        } //end for loop (all tokens now are in postFixStr or the ops stack now)
        
        // we finish by moving elements from the stack to postFixStr...
        while (! ops.isEmpty()) {
            postFixStr += ops.pop() + " ";
        }
        
        return postFixStr;
    } 

    public static Scanner filePath(Scanner scan){
        String path = "src\\data.txt";
        File myFile;
        String filePath = new File(path).getAbsolutePath();
        try{
            myFile = new File(filePath);
            scan = new Scanner(myFile);
        }
        catch(FileNotFoundException e){
            
        }
        return scan;
    }
    
    public static LinkedQueue storeInQueue(Scanner scan){
        
        LinkedQueue queueFile = new LinkedQueue();
        scan = filePath(scan);
        
        while(scan.hasNextLine()){
            queueFile.enqueue(scan.nextLine());
        }
        return queueFile;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scan = null;
        LinkedQueue queueFile = storeInQueue(scan);
        System.out.println(toPostFix((String) queueFile.dequeue()));
        System.out.println(toPostFix("( ( 5 + 2 ) * 3 ) - 1"));
        
    }
    
}
