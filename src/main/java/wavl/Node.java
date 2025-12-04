package wavl;

public class Node {
    public Node parent, left, right;
    public Integer key, rank;

    public Node(Node parent, Node left, Node right, Integer key, Integer rank) {
        this.parent = parent;
        this.left = left;
        this.right = right;
        this.key = key;
        this.rank = rank;
    }

}
