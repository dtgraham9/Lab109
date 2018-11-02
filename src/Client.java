
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * Gets a data file from user and proceeds to evaluate expressions inside and 
 * create LinkedBinaryTrees and display their preorder, in order, post order, and 
 * Euler's tour traversals.  
 * @author Graham
 * @version 
 */
public class Client {
        
    public static String[][] opsByPrecedence = {{"+","-"},{"*","/"}};
    public static String[][] opsBrackets = {{"(",")"}, {"[","]"},{"{","}"}};
    
    /**
     * Checks to see if token is an operator.
     * @param token
     * @return 
     */
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

    /**
     * Gets the Precedent value for the operator.  The higher the value the 
     * higher its precedence is 
     * @param token
     * @return 
     */
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
    
    /**
     * checks to see if token is left bracket
     * @param token
     * @return 
     */
    private static boolean isOpsLeftBracket(String token){
        for(int i = 0; i<opsBrackets.length; i++){
            if(token.equals(opsBrackets[i][0]))
                return true;
        }
        return false;
    }
    
    /**
     * Checks to see if token is a right bracket
     * @param token
     * @return 
     */
    private static boolean isOpsRightBracket(String token){
        for(int i = 0; i<opsBrackets.length; i++){
            if(token.equals(opsBrackets[i][1]))
                return true;
        }
        return false;
    }
    
    /**
     * Checks to see if it is a complete set of brackets
     * @param leftBracket
     * @param rightBracket
     * @return 
     */
    private static boolean compareBrackets(String leftBracket, String rightBracket){
        for(int i = 0; i<3; i++){
            if(leftBracket.equals(opsBrackets[i][0]) && rightBracket.equals(opsBrackets[i][1]))
                return true;
        }
        return false;
    }
    /**
     * Checks to see if token is a bracket
     * @param token
     * @return 
     */
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
    
    /**
     * The shunting yard algorithm.  Takes a String expression converts it into a queue
     * them proceeds to push brackets and numerical values and lower precedent operators 
     * awaiting for a closed bracket or high precedent operator to have elements of the
     * stack be pop into another queue.
     * @param expression
     * @return 
     */
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
    
    
    /**
     * Takes a LinkedQueue of Strings and converts them into LinkedBinaryTrees
     * where each element of tree is its own root of its own LinkedBinaryTree
     * @param queue
     * @return 
     */
    public static LinkedQueue<LinkedBinaryTree> makeTreeNodes(LinkedQueue<String> queue){
        LinkedQueue<LinkedBinaryTree> tree = new LinkedQueue();
        while(!queue.isEmpty()){
            LinkedBinaryTree node = new LinkedBinaryTree();
            node.addRoot(queue.dequeue());
            tree.enqueue(node);
        }
        return tree;
    }
    
    
    /**
     * Takes a LinkedQueue of LinkedBinaryTrees and pushs non operators onto a stack
     * that will have them pop and attach to an operator which then will be pushed
     * back onto the stack
     * @param queue
     * @return 
     */
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
    /**
     * Confirms if the user wants to exit after clicking cancel
     * @return 
     */
    public static boolean confirmExit(){
        int option = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit?","exit", JOptionPane.YES_NO_OPTION);
        return JOptionPane.YES_OPTION == option;
    }

    
    /**
     * Takes a scanner and will return it will it being able to read from a 
     * data file that does exist that was provided by a user
     * @param scan
     * @return 
     */
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
    
    /**
     * Takes the scanner scan that has location of file with data and 
     * extracts each token line and puts into a queue and returns it
     * @param scan
     * @return 
     */
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
    
    /**
     * Evaluates a post fix expression.  Takes a LinkedQueue and extracts elements
     * type casts them to double and performs correct operation
     * @param queue
     * @return 
     */
    public static double evaluateExpression(LinkedQueue queue){
        LinkedStack<Double> stack = new LinkedStack();
        while(!queue.isEmpty()){
            String token  = (String) queue.dequeue();
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
        return stack.pop();
    }
    
    /**
     * Gets a data file from user and proceeds to evaluate expressions inside and 
     * create LinkedBinaryTrees and display their preorder, in order, post order, and 
     * Euler's tour traversals.  
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scan = null;
        LinkedQueue<String> queueFile = storeInQueue(scan);
        while(!queueFile.isEmpty()){
            try{
                StringBuilder postOrderExpression = new StringBuilder("Post Order: ");
                StringBuilder preOrderExpression = new StringBuilder("Pre Order: ");
                StringBuilder inOrderExpression = new StringBuilder("In Order: ");
                StringBuilder expression = new StringBuilder("Expression: ");
                StringBuilder postFixExpression = new StringBuilder("Post Fix: ");
                String tokens = (String) queueFile.dequeue();
                expression.append(tokens);
                System.out.println(expression.toString());
                LinkedQueue<String> postFix = toPostFix(tokens);
                
                LinkedQueue<String> temp = new LinkedQueue();
                while(!postFix.isEmpty()){
                    postFixExpression.append(postFix.first()).append(" ");
                    temp.enqueue(postFix.dequeue());
                }
                
                postFix = temp;
                temp = null;
                
                LinkedQueue<LinkedBinaryTree> postFixTree = makeTreeNodes(postFix);
                LinkedBinaryTree myTree = constructTree(postFixTree);
                
                //Pre order
                Iterable<Position<String>> preOrder = myTree.preorder();
                for(Position<String> pO : preOrder){
                    preOrderExpression.append(pO.getElement()).append(" ");
                }
                //In order
                Iterable<Position<String>> inOrder = myTree.inorder();
                for(Position<String> iO : inOrder){
                    inOrderExpression.append(iO.getElement()).append(" ");
                }
                
                //post order    
                Iterable<Position<String>> postOrder =  myTree.postorder();
                for(Position<String> pO : postOrder){
                    postOrderExpression.append(pO.getElement()).append(" ");
                }
                
                
                System.out.println(postFixExpression.toString());
                System.out.println(preOrderExpression.toString());
                System.out.println(inOrderExpression.toString());
                System.out.println(postOrderExpression.toString());
                System.out.print("Euler's Tour: ");
                LinkedBinaryTree.parenthesize(myTree, myTree.root);
                System.out.println("\n");
            }
            catch (RuntimeException e){
                System.out.println("\033[0;31m" + e.toString() + "\n");
            }
            
        }

        
    }
    
}
