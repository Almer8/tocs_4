package bst;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class BinarySearchTree {
    public Node root;

    public Node treeSearch(Node node, Integer key, AtomicInteger bst_search_nodes_checked) {
        while (node != null && !key.equals(node.key)) {
            bst_search_nodes_checked.incrementAndGet();
            node = key < node.key ? node.left : node.right;
        }
        return node;
    }

    public Node minimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public Node maximum(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    public Node successor(Node node) {
        if(node.right != null) {
            return minimum(node.right);
        }
        for(Node n = node.parent; n.parent!= null; n = n.parent) {
            Node parent = n.parent;
            if(parent.left == n) {
                return parent;
            }
        }
        return null;
    }

    public Node predecessor(Node node) {
        if (node.left != null) {
            return maximum(node.left);
        }
        for (Node n = node.parent; n.parent != null; n = n.parent) {
            Node parent = n.parent;
            if (parent.right == n) {
                return parent;
            }
        }
        return null;
    }

    public Node insert(Integer key, AtomicInteger bst_insert_nodes_checked) {
        Node n = new Node(null,null,null,key);
        if (root == null) {
            root = n;
            return n;
        }

        Node x = root;
        Node y = null;
        while (x != null) {
            bst_insert_nodes_checked.incrementAndGet();
            y = x;
            x = (key < x.key) ? x.left : x.right;
        }
        n.parent = y;
        if(key < y.key) {
            y.left = n;
        } else{
            y.right = n;
        }
        return n;
    }

    public void delete(Node node, AtomicInteger bst_delete_nodes_checked) {
        if (node == null) return;
        Node parent = node.parent;
        Node child = null;

        if(node.left == null && node.right == null) {
            if (parent == null) {
                root = null;
            } else
            if(parent.left == node) {
                parent.left = null;
            } else{
                parent.right = null;
            }
            return;
        }

        if(node.left != null && node.right == null) {
            child = node.left;
            if(parent == null){
                root = child;
                child.parent = null;
                return;
            }

            if(node == parent.right){
                parent.right = child;
            } else {
                parent.left = child;
            }
            child.parent = parent;

        }
        else if(node.left == null && node.right != null) {
            child = node.right;
            if(parent == null){
                root = child;
                child.parent = null;
                return;
            }

            if(node == parent.right){
                parent.right = child;
            } else {
                parent.left = child;
            }
            child.parent = parent;
        }
        else {
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
            bst_delete_nodes_checked.incrementAndGet();
            delete(y, bst_delete_nodes_checked);
        }
    }
    public void inorderWalk(Node node) {
        if(node == null) return;
        inorderWalk(node.left);
        System.out.print(node.key + " ");
        inorderWalk(node.right);
    }

    public int height(Node node){
        if(node == null) return 0;
        int left = height(node.left);
        int right = height(node.right);
        return Math.max(left, right) + 1;
    }


}
