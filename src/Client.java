
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

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
    
    private static boolean isOp(String token) {
        for (int i = 0; i < opsByPrecedence.length; i++) {
            for (int j = 0; j < opsByPrecedence[i].length; j++) {
                if (token.equals(opsByPrecedence[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    
    private static int getPrecedence(String token) {
        for (int i = 0; i < opsByPrecedence.length; i++) {
            for (int j = 0; j < opsByPrecedence[i].length; j++) {
                if (token.equals(opsByPrecedence[i][j])) {
                    return i;
                }
            }
        }
        for(int i = 0; i < opsBrackets.length; i++ ){
            if(isOpsLeftBracket(token)){
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
    
    public static boolean confirmExit(){
        int option = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit?","exit", JOptionPane.YES_NO_OPTION);
        return JOptionPane.YES_OPTION == option;
    }

    public static Scanner filePath(Scanner scan) {
        boolean debug = false;
        if (debug) {
            String path = "src\\data.txt";
            File myFile;
            String filePath = new File(path).getAbsolutePath();
            try {
                myFile = new File(filePath);
                scan = new Scanner(myFile);
            } catch (FileNotFoundException e) {

            }
            return scan;
        } else {
            boolean statusCheck = false;
            while (!(statusCheck)) {
                String prompt = "Enter in String Path to Data";
                String path = JOptionPane.showInputDialog(null, prompt);
                if (null == path) {
                    statusCheck = confirmExit();
                    if (statusCheck) {
                        break;
                    } else {
                        continue;
                    }
                }
                File file;
                try {
                    file = new File(path);
                    scan = new Scanner(file);
                    statusCheck = true;

                } catch (FileNotFoundException e) {
                    System.out.println("Invalid path: " + path);
                    JOptionPane.showMessageDialog(null, "Not a valid file location, please enter valid path");
                }
            }
        }
        return scan;
    }
    
    public static LinkedQueue storeInQueue(Scanner scan){
        
        LinkedQueue queueFile = new LinkedQueue();
        scan = filePath(scan);
        
        try {
            while (scan.hasNextLine()) {
                queueFile.enqueue(scan.nextLine());
            }
        }
        catch (NullPointerException e){
            
        }
        return queueFile;
    }
    
    public static double evaluateExpression(LinkedQueue queue){
        LinkedQueue<String> temp = new LinkedQueue();
        LinkedStack<Double> stack = new LinkedStack();
        while(!queue.isEmpty()){
            String token  = (String) queue.dequeue();
            temp.enqueue(token);
            if(isOp(token)){
                Double product;
                Double rightOperand = stack.pop();
                Double leftOperand = stack.pop();
                switch((String) token){
                    case "*":
                        product = leftOperand * rightOperand ;
                        stack.push(product);
                        break;
                    case "/":
                        product = leftOperand /rightOperand ;
                        stack.push(product);
                        break;
                    case "+":
                        product = leftOperand + rightOperand;
                        stack.push(product);
                        break;
                    case "-":
                        product = leftOperand - rightOperand;
                        stack.push(product);
                        break;
                        
                }
            }
            else {
                stack.push((Double.parseDouble(token)));
            }
        }
        queue = temp;
        return stack.pop();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scan = null;
        LinkedQueue<String> queueFile = storeInQueue(scan);
        while(!queueFile.isEmpty()){
            try{
                String expression = (String) queueFile.dequeue();
                System.out.println("Expression: " + expression);
                LinkedQueue<String> postFix = toPostFix(expression);
                
                LinkedQueue<LinkedBinaryTree> postFixTree = makeTreeNodes(postFix);
                LinkedBinaryTree myTree = constructTree(postFixTree);
                
                System.out.print("Pre Order: ");
                Iterable<Position<String>> preOrder = myTree.preorder();
                for(Position<String> pO : preOrder){
                    System.out.print(pO.getElement() + " ");
                }
                
                System.out.print("\nIn Order: ");
                Iterable<Position<String>> inOrder = myTree.inorder();
                for(Position<String> iO : inOrder){
                    System.out.print(iO.getElement() + " ");
                }
                
                System.out.print("\nPost Order: ");
                StringBuilder postFixExpression = new StringBuilder();
                LinkedQueue<String> postFixQueue = new LinkedQueue();
                Iterable<Position<String>> postOrder =  myTree.postorder();
                for(Position<String> pO : postOrder){
                    postFixExpression.append(pO.getElement()).append(" ");
                    postFixQueue.enqueue(pO.getElement());
                    System.out.print(pO.getElement()+ " ");
                }
                
                System.out.print("\nEuler's Tour: ");
                LinkedBinaryTree.parenthesize(myTree, myTree.root);
                
                System.out.println("\nPost Fix Expression: " + postFixExpression.toString());
                System.out.println("Evaluated Expression: " + evaluateExpression(postFixQueue));
                
                System.out.println("\n");
            }
            catch (RuntimeException e){
                System.out.println("\033[0;31m" + e.toString() + "\n");
            }
            
        }

        
    }
    
}
