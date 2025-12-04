package wavl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WAVLSearchTree {
    public Node root;
    public final Node NIL;

    public WAVLSearchTree(){
        NIL = new Node(null, null, null, null, 0);
        NIL.left = NIL;
        NIL.right = NIL;
        NIL.parent = NIL;
    }


    public Node treeSearch(Node node, Integer key, AtomicInteger checked) {
        while (node != NIL && !key.equals(node.key)) {
            checked.incrementAndGet();
            node = key < node.key ? node.left : node.right;
        }
        return node;
    }

    public Node minimum(Node node) {
        if (node == null || node == NIL) return NIL;
        while (node.left != NIL) node = node.left;
        return node;
    }

    public Node maximum(Node node) {
        if (node == null || node == NIL) return NIL;
        while (node.right != NIL) node = node.right;
        return node;
    }

    public Node successor(Node node) {
        if (node == null || node == NIL) return NIL;
        if (node.right != NIL) return minimum(node.right);
        for (Node n = node.parent; n != NIL; n = n.parent) {
            if (n.left == node) return n;
            node = n;
        }
        return NIL;
    }

    public Node predecessor(Node node) {
        if (node == null || node == NIL) return NIL;
        if (node.left != NIL) return maximum(node.left);
        for (Node n = node.parent; n != NIL; n = n.parent) {
            if (n.right == node) return n;
            node = n;
        }
        return NIL;
    }

    public void leftRotate(Node root, Node x) {
        Node y = x.right;
        if (y == NIL) return;

        x.right = y.left;
        if (y.left != NIL) y.left.parent = x;
        y.parent = x.parent;

        if (x.parent == NIL) this.root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;

        y.left = x;
        x.parent = y;
    }

    public void rightRotate(Node root, Node y) {
        Node x = y.left;
        if (x == NIL) return;

        y.left = x.right;
        if (x.right != NIL) x.right.parent = y;
        x.parent = y.parent;

        if (y.parent == NIL) this.root = x;
        else if (y == y.parent.left) y.parent.left = x;
        else y.parent.right = x;

        x.right = y;
        y.parent = x;
    }

    public void insertFixup(Node n, AtomicInteger wst_insert_rotates) {


        for (Node x = n; x != NIL; x = x.parent) {
            Node p = x.parent;
            if (p == NIL) {
                break;
            }

            Node b = (x == p.left) ? p.right : p.left;

            if (p.rank - x.rank == 1) return;

            if (p.rank - b.rank == 2) {
                boolean doubleRotation = false;

                if (x == p.left) {
                    if (x.rank - x.right.rank == 1) {
                        wst_insert_rotates.incrementAndGet();
                        leftRotate(root, x);
                        doubleRotation = true;
                    }
                    wst_insert_rotates.incrementAndGet();
                    rightRotate(root, p);
                } else {
                    if (x.rank - x.left.rank == 1) {
                        wst_insert_rotates.incrementAndGet();
                        rightRotate(root, x);
                        doubleRotation = true;
                    }
                    wst_insert_rotates.incrementAndGet();
                    leftRotate(root, p);
                }

                if (doubleRotation) {
                    p.rank--;
                    x.rank--;
                    Node newRoot = (p.parent == NIL ? root : p.parent);
                    newRoot.rank++;
                } else {
                    p.rank--;
                }
                return;
            } else {
                p.rank++;
            }
        }


    }

    public Node insert(Integer key, AtomicInteger wst_insert_nodes_checked, AtomicInteger wst_insert_rotates) {
        Node n = new Node(null, NIL, NIL, key, 1);
        n.left.parent = n;
        n.right.parent = n;

        if (root == null) {
            n.parent = NIL;
            root = n;
            return n;
        }

        Node x = root;
        Node y = null;
        while (x != NIL) {
            wst_insert_nodes_checked.incrementAndGet();
            y = x;
            x = (key < x.key) ? x.left : x.right;
        }
        n.parent = y;
        if(key < y.key) {
            y.left = n;
        } else{
            y.right = n;
        }
        insertFixup(n, wst_insert_rotates);
        return n;
    }

    public void deleteFixup(Node n, AtomicInteger wst_delete_rotates) {
        for (Node x = n; x.parent != NIL; x = x.parent) {
            Node p = x.parent;
            int diffPX = p.rank - x.rank;

            if (diffPX == 2) return;

            Node b = (x == p.left) ? p.right : p.left;
            int diffPB = p.rank - b.rank;

            if (diffPB == 2) {
                p.rank--;
                x = p;
            } else {
                int diffBL = b.rank - b.left.rank;
                int diffBR = b.rank - b.right.rank;

                if (diffBL == 2 && diffBR == 2) {
                    p.rank--;
                    b.rank--;
                    x = p;
                } else {
                    boolean doubleRotation = false;
                    if (x == p.left) {
                        if (diffBR == 1) {
                            leftRotate(root, b);
                            doubleRotation = true;
                        }
                        wst_delete_rotates.incrementAndGet();
                        rightRotate(root, p);
                    } else {
                        if (diffBL == 1) {
                            rightRotate(root, b);
                            doubleRotation = true;
                        }
                        wst_delete_rotates.incrementAndGet();
                        leftRotate(root, p);
                    }

                    if (doubleRotation) {
                        wst_delete_rotates.incrementAndGet();
                        p.rank--;
                        b.rank--;
                        x.rank++;
                    } else {
                        p.rank--;
                    }

                    return;
                }
            }
        }
    }



    public void delete(Node node, AtomicInteger wst_delete_nodes_checked, AtomicInteger wst_delete_rotates) {
        if (node == null || node == NIL) return;

        if (node.left != NIL && node.right != NIL) {
            Node y;
            if(maximum(root).equals(node)) {
                y = predecessor(node);
            } else if(minimum(root).equals(node)) {
                y = successor(node);
            }
            else {
                if(new Random().nextBoolean()) {
                    y = predecessor(node);
                } else {
                    y = successor(node);
                }
            }

            node.key = y.key;
            wst_delete_nodes_checked.incrementAndGet();
            delete(y, wst_delete_nodes_checked, wst_delete_rotates);
            return;
        }


        Node parent = node.parent;
        Node child = (node.left != NIL) ? node.left : node.right;


        if (parent == NIL) {
            root = child;
            if (child != NIL) {
                child.parent = NIL;
            }
            return;
        }

        if (node == parent.left) {
            parent.left = child;
        } else {
            parent.right = child;
        }

        if (child != NIL) {
            child.parent = parent;
        }

        int diffL = parent.rank - parent.left.rank;
        int diffR = parent.rank - parent.right.rank;

        if (node.left == NIL && node.right == NIL) {
            if (diffL == 2 && diffR == 2) {
                parent.rank--;
                deleteFixup(parent, wst_delete_rotates);
            }

        } else {

            if (diffL == 2 && diffR == 2) {
                parent.rank--;
                deleteFixup(parent, wst_delete_rotates);
            } else if (diffL == 3 || diffR == 3) {
                deleteFixup(child, wst_delete_rotates);
            }
        }
    }

    public void inorderWalk(Node node) {
        if(node == NIL) return;
        inorderWalk(node.left);
        System.out.print(String.format("%s(%s) ",node.key,node.rank));
        inorderWalk(node.right);
    }

    public int height(Node node) {
        return height(node, new HashSet<>());
    }

    private int height(Node node, Set<Node> visited) {
        if (node == null || node == NIL) return 0;
        if (visited.contains(node)) {
            System.err.println("Cycle detected at " + node.key);
            throw new RuntimeException("Cycle detected at " + node.key);
           // return 0;
        }
        visited.add(node);
        int left = height(node.left, visited);
        int right = height(node.right, visited);
        return Math.max(left, right) + 1;
    }




    ////////////////////




    public void printTreeSym(Node root) {
        int maxLevel = maxDepth(root);
        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }

    private void printNodeInternal(List<Node> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || isAllNIL(nodes)) return;

        int floor = maxLevel - level;
        int edgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, floor) - 1;
        int betweenSpaces = (int) Math.pow(2, floor + 1) - 1;

        printWhitespaces(firstSpaces);

        List<Node> newNodes = new ArrayList<>();
        for (Node node : nodes) {
            if (node != NIL) {
                System.out.print(node.key + "(" + node.rank + ")");
                newNodes.add(node.left);
                newNodes.add(node.right);
            } else {
                System.out.print(" ");
                newNodes.add(NIL);
                newNodes.add(NIL);
            }
            printWhitespaces(betweenSpaces);
        }
        System.out.println();

        for (int i = 1; i <= edgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                printWhitespaces(firstSpaces - i);
                Node node = nodes.get(j);
                if (node == NIL) {
                    printWhitespaces(edgeLines + edgeLines + i + 1);
                    continue;
                }

                if (node.left != NIL) System.out.print("/");
                else printWhitespaces(1);

                printWhitespaces(i + i - 1);

                if (node.right != NIL) System.out.print("\\");
                else printWhitespaces(1);

                printWhitespaces(edgeLines + edgeLines - i);
            }
            System.out.println();
        }

        printNodeInternal(newNodes, level + 1, maxLevel);
    }

    private void printWhitespaces(int count) {
        for (int i = 0; i < count; i++) System.out.print(" ");
    }

    private boolean isAllNIL(List<Node> nodes) {
        for (Node node : nodes) {
            if (node != NIL) return false;
        }
        return true;
    }

    private int maxDepth(Node node) {
        if (node == NIL) return 0;
        return Math.max(maxDepth(node.left), maxDepth(node.right)) + 1;
    }


}
