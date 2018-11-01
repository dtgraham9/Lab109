
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
    public static String[][] opsBrackets = {{"(",")"}, {"[","]"},{"{","}"}};
    
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
        for(int i = 0; i < opsBrackets.length; i++ ){
            if(isOpsLeftBracket(op)){
                return 0;
            }
        }
        return -1;
    }
    
    private static boolean isOpsLeftBracket(String token){
        for(int i = 0; i<opsBrackets.length; i++){
            if(token.equals(opsBrackets[i][0]))
                return true;
        }
        return false;
    }
    
    private static boolean isOpsRightBracket(String token){
        for(int i = 0; i<opsBrackets.length; i++){
            if(token.equals(opsBrackets[i][1]))
                return true;
        }
        return false;
    }
    
    private static boolean compareBrackets(String leftBracket, String rightBracket){
        for(int i = 0; i<3; i++){
            if(leftBracket.equals(opsBrackets[i][0]) && rightBracket.equals(opsBrackets[i][1]))
                return true;
        }
        return false;
    }
    private static boolean isBracket(String token) {
        for (int i = 0; i < opsBrackets.length; i++) {
            for (int j = 0; j < opsBrackets[i].length; j++) {
                if (token.equals(opsBrackets[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    public static LinkedQueue<String> toPostFix(String expression) {
        
        Scanner scan = new Scanner(expression);
        LinkedStack<String> ops = new LinkedStack();
        LinkedQueue<String> postFix = new LinkedQueue();
        LinkedStack<String> brackets = new LinkedStack();
        int operandCounter = 0; 
        int operatorCounter = 0; 
        boolean trackTokenType = false; //false for operand true for operator
        while (scan.hasNext()) {
            String token = scan.next();
            boolean[] isTokenBracket = {false,false};
            if(isOp(token)){
                trackTokenType = true;
                operatorCounter++;
            }
                
            else if(isBracket(token)){}//prevent tokenType from changing
            
            else {
               trackTokenType = false;
               operandCounter++;
            }
            isTokenBracket[0] = isOpsLeftBracket(token);
            isTokenBracket[1]= isOpsRightBracket(token);
            if (isTokenBracket[0]) {
                ops.push(token);
                brackets.push(token);
            }
            else if (isTokenBracket[1]) {
                boolean bracketsSolved = false;
                while (! (bracketsSolved || ops.isEmpty())) {
                    if(isOpsLeftBracket(ops.top())){
                        if(compareBrackets(ops.top(),token)){
                            ops.pop();
                            brackets.pop();
                            bracketsSolved = true;
                        }
                        else{
                            throw new RuntimeException("Invalid brackets "
                                    + "specified ( \'" + ops.top() + "\' , \'" + token+ "\' )");
                        }
                    }
                    else{
                      postFix.enqueue(ops.pop());  
                    }
                    
                }
            }
            else if (! isOp(token)) {
                postFix.enqueue(token);
            }
            else { // token is an operator...
                
                boolean tokenProcessed = false; 
                                                
                
                while ( ! tokenProcessed ) {
                    if (ops.isEmpty() || ops.top().equals("(")) {  
                        ops.push(token);
                        tokenProcessed = true;
                    }
                    else {
                        String topOp = (String) ops.top();
                        
                        if ((getPrecedence(token) > getPrecedence(topOp)) ||
                            ((getPrecedence(token) == getPrecedence(topOp)))) {
                            ops.push(token);
                            tokenProcessed = true;
                        }
                        else {
                            postFix.enqueue(ops.pop());
                        } 
                    } 
                } 
            } 
        } //end loop (all tokens are now in postFix or the ops stack now)
        
        // move elements from the stack to postFix
        while (! ops.isEmpty()) {
            postFix.enqueue(ops.pop());
        }
        if(!brackets.isEmpty())
            throw new RuntimeException("Brackets incomplete");
        else if(trackTokenType)
            throw new RuntimeException("Expression doesn't end with operand");
        else if((operandCounter-operatorCounter) != 1)
            throw new RuntimeException("Expression doesn't have corrent amount "
                    + "operators or operands");
        return postFix;
    } 
    
    public static LinkedQueue<LinkedBinaryTree> makeTreeNodes(LinkedQueue<String> queue){
        LinkedQueue<LinkedBinaryTree> tree = new LinkedQueue();
        while(!queue.isEmpty()){
            LinkedBinaryTree node = new LinkedBinaryTree();
            node.addRoot(queue.dequeue());
            tree.enqueue(node);
        }
        return tree;
    }
    
    public static LinkedBinaryTree constructTree(LinkedQueue<LinkedBinaryTree> queue){
        LinkedStack<LinkedBinaryTree> treeBuilder = new LinkedStack();
        while(!queue.isEmpty()){
            LinkedBinaryTree testNode =  queue.dequeue();
            if(isOp((String) testNode.root().getElement())){
                LinkedBinaryTree rightBranch = treeBuilder.pop();
                LinkedBinaryTree leftBranch = treeBuilder.pop();
                testNode.attach(testNode.root, leftBranch, rightBranch);
                treeBuilder.push(testNode);
            }
            else{
                treeBuilder.push(testNode);
            }
        }
        return treeBuilder.pop();
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
        LinkedQueue<String> queueFile = storeInQueue(scan);
        while(!queueFile.isEmpty()){
            try{
            LinkedQueue<String> postFix = toPostFix((String) queueFile.dequeue());
            LinkedQueue<LinkedBinaryTree> postFixTree = makeTreeNodes(postFix);
            LinkedBinaryTree myTree = constructTree(postFixTree);
            Iterable<Position<String>> p =  myTree.postorder();
            for(Position<String> s : p){
                System.out.print(s.getElement()+ " ");
            }
            System.out.println("");
            }
            catch (RuntimeException e){
                e.printStackTrace();
            }
        }

        
    }
    
}
