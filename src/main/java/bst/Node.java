package bst;

public class Node {
    public Node parent, left, right;
    public Integer key;

    public Node(Node parent, Node left, Node right, Integer key) {
        this.parent = parent;
        this.left = left;
        this.right = right;
        this.key = key;
    }
}
