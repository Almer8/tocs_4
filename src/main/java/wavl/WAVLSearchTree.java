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
        root = NIL;
    }

    private int rank(Node n) {
        return (n == NIL) ? 0 : n.rank;
    }

    private void demote(Node n) {
        if (n != NIL) {
            n.rank--;
        }
    }

    private void promote(Node n) {
        if (n != NIL) {
            n.rank++;
        }
    }

    public Node treeSearch(Node node, Integer key, AtomicInteger checked) {
        while (node != NIL && !key.equals(node.key)) {
            checked.incrementAndGet();
            node = key < node.key ? node.left : node.right;
        }
        return node;
    }

    public Node minimum(Node node) {
        if (node == NIL) return NIL;
        while (node.left != NIL) node = node.left;
        return node;
    }

    public Node maximum(Node node) {
        if (node == NIL) return NIL;
        while (node.right != NIL) node = node.right;
        return node;
    }

    public Node successor(Node node) {
        if (node == NIL) return NIL;
        if (node.right != NIL) return minimum(node.right);
        for (Node n = node.parent; n != NIL; n = n.parent) {
            if (n.left == node) return n;
            node = n;
        }
        return NIL;
    }

    public Node predecessor(Node node) {
        if (node == NIL) return NIL;
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

                demote(p);
                if (doubleRotation) {
                    demote(x);
                    Node newRoot = (p.parent == NIL ? root : p.parent);
                    promote(newRoot);
                }
                return;
            } else {
                promote(p);
            }
        }


    }

    public Node insert(Integer key, AtomicInteger wst_insert_nodes_checked, AtomicInteger wst_insert_rotates) {
        Node n = new Node(null, NIL, NIL, key, 1);

        Node x = root;
        Node y = NIL;

        while (x != NIL) {
            wst_insert_nodes_checked.incrementAndGet();
            y = x;
            x = (key < x.key) ? x.left : x.right;
        }

        n.parent = y;

        if (y == NIL) {
            root = n;
        } else if (key < y.key) {
            y.left = n;
        } else {
            y.right = n;
        }
        insertFixup(n, wst_insert_rotates);
        return n;
    }

    private void deleteFixup(Node x, Node p, AtomicInteger wst_delete_rotates) {
        while (p != NIL) {
            int diff = rank(p) - rank(x);

            Node b = (x == p.left) ? p.right : p.left;
            int diffPB = rank(p) - rank(b);

            if (diff == 2) {
                boolean isLeafViolation = diffPB == 2 && rank(p) == 2;

                boolean isSiblingBroken = (diffPB > 2);

                if (!isLeafViolation && !isSiblingBroken) {
                    return;
                }
            }

            if (diffPB == 2) {
                demote(p);
                x = p;
                p = p.parent;
                continue;
            }

            int diffBL = rank(b) - rank(b.left);
            int diffBR = rank(b) - rank(b.right);

            if (diffBL == 2 && diffBR == 2) {
                demote(p);
                demote(b);
                x = p;
                p = p.parent;
                continue;
            }

            if (x == p.left) {
                wst_delete_rotates.incrementAndGet();
                if (diffBR == 1) {
                    leftRotate(root, p);

                    if (p.left == NIL && p.right == NIL) p.rank = 1;
                    else demote(p);

                    promote(b);
                } else {
                    rightRotate(root, b);
                    wst_delete_rotates.incrementAndGet();
                    leftRotate(root, p);

                    Node newRoot = p.parent;
                    promote(newRoot);
                    promote(newRoot);
                    demote(p);
                    demote(p);
                    demote(b);
                }
            } else {
                wst_delete_rotates.incrementAndGet();
                if (diffBL == 1) {
                    rightRotate(root, p);

                    if (p.left == NIL && p.right == NIL) p.rank = 1;
                    else demote(p);

                    promote(b);
                } else {
                    leftRotate(root, b);
                    wst_delete_rotates.incrementAndGet();
                    rightRotate(root, p);

                    Node newRoot = p.parent;
                    promote(newRoot);
                    promote(newRoot);
                    demote(p);
                    demote(p);
                    demote(b);
                }
            }
            return;
        }
    }



    public void delete(Node node, AtomicInteger checks, AtomicInteger rotates) {
        if (node == NIL) return;

        if (node.left != NIL && node.right != NIL) {
            Node deleteNode = new Random().nextBoolean() ? successor(node): predecessor(node);
            node.key = deleteNode.key;
            checks.incrementAndGet();
            delete(deleteNode, checks, rotates);
            return;
        }
        Node child = (node.left != NIL) ? node.left : node.right;
        Node parent = node.parent;

        if (child != NIL) {
            child.parent = parent;
        }

        if (parent == NIL) {
            root = child;
            return;
        } else {
            if (node == parent.left) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        }

        int diff = rank(parent) - rank(child);

        if (node.rank == 1 || diff == 3) {
            deleteFixup(child, parent, rotates);
        }
    }

    public void inorderWalk(Node node) {
        if(node == NIL) return;
        inorderWalk(node.left);
        System.out.print(String.format("%s(%s) ",node.key,node.rank));
        inorderWalk(node.right);
    }

    public int height(Node node) {
        if (node == NIL) return 0;
        int left = height(node.left);
        int right = height(node.right);
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

    public void checkBalance(Node node){
        if(node == NIL) return;
        checkBalance(node.left);
        checkBalance(node.right);
        if(node.rank - node.left.rank > 2 || node.rank - node.left.rank == 0){
            System.out.println("Balance diff is " + (node.rank - node.left.rank));
        }
        if(node.rank - node.right.rank > 2 || node.rank - node.right.rank == 0){
            System.out.println("Balance diff is " + (node.rank - node.right.rank));
        }
    }


}
