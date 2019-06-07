import java.util.EmptyStackException;
import java.util.Stack;

public class BET<E> {
    private BinaryNode root;

    public BET() {
    }

    private static String ops = "+-*/";
    private static int HIGH =  1;
    private static int LOW = 0;

    public BET(String expr, char mode) throws IllegalStateException {
        if (mode == 'p') {
            if (!buildFromPostFix(expr)) {
                System.out.println("Invalid Notation: " + expr);
                throw new IllegalStateException();
            }
        } else if (mode == 'i') {
            if (!buildFromInfix(expr)) {
                System.out.println("Invalid Notation: " + expr);
                throw new IllegalStateException();
            }
        } else {
            System.out.println("Invalid option");
            throw new IllegalStateException();
        }
    }

    private class BinaryNode {
        private String element;
        private BinaryNode parent;
        private BinaryNode left;
        private BinaryNode right;

        //Tells us what type of node this is
        private final static int OPERATOR = 0;
        private final static int OPERAND = 1;

        public BinaryNode(String element, BinaryNode parent, BinaryNode left, BinaryNode right) {
            this.element = element;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

        public String getElement() {
            return element;
        }

        public void setElement(String element) {
            this.element = element;
        }

        public BinaryNode getParent() {
            return parent;
        }

        public void setParent(BinaryNode parent) {
            this.parent = parent;
        }

        public BinaryNode getLeft() {
            return left;
        }

        public void setLeft(BinaryNode left) {
            this.left = left;
        }

        public BinaryNode getRight() {
            return right;
        }

        public void setRight(BinaryNode right) {
            this.right = right;
        }

    }

    public boolean buildFromPostFix(String postfix) {
        //Not sure if this is what is needed...
        if ( root != null ) {
            makeEmpty(root);
        }

        postfix = postfix.trim();
        String[] tokens = postfix.split(" ");
        Stack<BinaryNode> operands = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (ops.contains(tokens[i])) {
                try {
                    BinaryNode right = operands.pop();
                    BinaryNode left = operands.pop();
                    BinaryNode op = new BinaryNode(tokens[i], null, left, right);
                    right.setParent(op);
                    left.setParent(op);
                    operands.push(op);
                } catch (EmptyStackException e) {
                    return false;
                }

            } else {
                BinaryNode operand = new BinaryNode(tokens[i], null, null, null);
                operands.push(operand);
            }
        }

        BinaryNode newRoot = operands.pop();

        if (operands.isEmpty()) {
            root = newRoot;
            return true;
        } else {
            return false;
        }
    }

    private int getPrecedence(String op) {
        if (op.equals("*") || op.equals("/")) {
            return HIGH;
        } else {
            return LOW;
        }
    }

    //Using shunting yard algorithm
    public boolean buildFromInfix(String infix) {
        //Not sure if this is what is needed...
        if ( root != null ) {
            makeEmpty(root);
        }

        infix = infix.trim();
        String[] tokens = infix.split(" ");
        Stack<String> operators = new Stack<>();
        StringBuilder postFix = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            if (ops.contains(tokens[i])) {
                while ( !operators.isEmpty() && getPrecedence(operators.peek()) >= getPrecedence(tokens[i])
                && !operators.peek().equals("(")) {
                    postFix.append(operators.pop() + " ");
                }
                operators.push(tokens[i]);

            } else if (tokens[i].equals("(")) {
                operators.push(tokens[i]);
            } else if (tokens[i].equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    postFix.append(operators.pop() + " ");
                }
                if (operators.isEmpty() || !operators.pop().equals("(")) {
                    return false;
                }
            } else {
                postFix.append(tokens[i] + " ");
            }
        }

        while (!operators.isEmpty()) {
            if (operators.peek().equals("(")) {
                return false;
            }
            postFix.append(operators.pop() + " ");
        }

        return buildFromPostFix(postFix.toString());
    }

    public void printPostfixExpression() {
        printPostfixExpression(root);
        System.out.println();
    }

    public void printPostfixExpression(BinaryNode n) {
        if (n != null) {
            printPostfixExpression(n.getLeft());
            printPostfixExpression(n.getRight());
            System.out.print(n.getElement() + " ");
        }
    }

    public void printInfixExpression() {
        printInfixExpression(root);
        System.out.println();
    }

    public void printInfixExpression(BinaryNode n) {
        if (n != null) {
            if (ops.contains(n.getElement())) {
                System.out.print("( ");
            }
            printInfixExpression(n.getLeft());
            System.out.print(n.getElement() + " ");
            printInfixExpression(n.getRight());
            if (ops.contains(n.getElement())) {
                System.out.print(") ");
            }
        }
    }


    public int size() {
        return size(root);
    }

    public int size(BinaryNode t) {
        if (t != null) {
            int size = 0;
            size += size(t.getLeft());
            size += size(t.getRight());
            return 1 + size;
        } else {
            return 0;
        }
    }

    public int leafNodes(BinaryNode t) {
        if (t.getLeft() == null && t.getRight() == null) {
            return 1;
        }
        int leaves = 0;
        leaves += leafNodes(t.getLeft());
        leaves += leafNodes(t.getRight());
        return leaves;
    }

    public int leafNodes() {
        return leafNodes(root);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void makeEmpty(BinaryNode t) {
        t.setRight(null);
        t.setLeft(null);
        t.setElement(null);
        t.setParent(null);
    }


}
