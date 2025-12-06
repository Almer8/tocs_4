import bst.BinarySearchTree;
import wavl.WAVLSearchTree;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        AtomicInteger dummy = new AtomicInteger();
        WAVLSearchTree wst = new WAVLSearchTree();
        wst.insert(30, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(40, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(50, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(24, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(8, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(58, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(48, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(26, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(11, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.insert(13, dummy, dummy);
        wst.printTreeSym(wst.root);
        wst.delete(wst.treeSearch(wst.root,11 , dummy), dummy, dummy);
        System.out.println("Delete 11");
        wst.printTreeSym(wst.root);
        wst.delete(wst.treeSearch(wst.root,8,  dummy), dummy, dummy);
        System.out.println("Delete 8");
        wst.printTreeSym(wst.root);
        wst.delete(wst.treeSearch(wst.root,26, dummy), dummy, dummy);
        System.out.println("Delete 26");
        wst.printTreeSym(wst.root);
        wst.inorderWalk(wst.root);

        long total_bst_height = 0;
        long total_wavl_height = 0;

        long total_bst_search = 0;
        long total_bst_search_nodes = 0;
        long total_bst_insert = 0;
        long total_bst_insert_nodes = 0;
        long total_bst_delete = 0;
        long total_bst_delete_nodes = 0;

        long total_wst_search = 0;
        long total_wst_search_nodes = 0;
        long total_wst_insert = 0;
        long total_wst_insert_nodes = 0;
        long total_wst_insert_rotates = 0;
        long total_wst_delete = 0;
        long total_wst_delete_nodes = 0;
        long total_wst_delete_rotates = 0;


        int iterations = 10;
        Random random = new Random();
        for (int iter = 0; iter < iterations; iter++){

            BinarySearchTree bst = new BinarySearchTree();
            wst = new WAVLSearchTree();
            dummy = new AtomicInteger();

            ArrayList<Integer> bstKeys = new ArrayList<>();
            ArrayList<Integer> wstKeys = new ArrayList<>();

            AtomicInteger bst_search_nodes_checked = new AtomicInteger(0);
            AtomicInteger bst_insert_nodes_checked = new AtomicInteger(0);
            AtomicInteger bst_delete_nodes_checked = new AtomicInteger(0);

            AtomicInteger wst_search_nodes_checked = new AtomicInteger(0);
            AtomicInteger wst_insert_nodes_checked = new AtomicInteger(0);
            AtomicInteger wst_insert_rotates = new AtomicInteger(0);
            AtomicInteger wst_delete_nodes_checked = new AtomicInteger(0);
            AtomicInteger wst_delete_rotates = new AtomicInteger(0);


            for(int i = 0; i < 1000000; i++) {
                int randomInt = random.nextInt();
                bst.insert(randomInt, dummy);
                wst.insert(randomInt, dummy, dummy);

                bstKeys.add(randomInt);
                wstKeys.add(randomInt);
            }


            total_bst_height += bst.height(bst.root);
            total_wavl_height += wst.height(wst.root);

            for(int i = 0; i < 200000; i++) {
                int randomInt = random.nextInt(3);

                switch(randomInt) {
                    case 0:
                        total_bst_search++;
                        bst.treeSearch(bst.root,random.nextInt(), bst_search_nodes_checked);
                        break;
                    case 1:
                        total_bst_insert++;
                        int key = random.nextInt();
                        bst.insert(key, bst_insert_nodes_checked);
                        bstKeys.add(key);
                        break;
                    case 2:
                        total_bst_delete++;
                        if (!bstKeys.isEmpty()) {
                            int indexToDelete = random.nextInt(bstKeys.size());
                            int keyToDelete = bstKeys.get(indexToDelete);

                            bst.delete(bst.treeSearch(bst.root, keyToDelete, dummy), bst_delete_nodes_checked);

                            int lastIndex = bstKeys.size() - 1;
                            bstKeys.set(indexToDelete, bstKeys.get(lastIndex));
                            bstKeys.remove(lastIndex);
                        }
                        break;
                }
            }

            for(int i = 0; i < 200000; i++) {
                int randomInt = random.nextInt(3);

                switch(randomInt) {
                    case 0:
                        total_wst_search++;
                        wst.treeSearch(wst.root,random.nextInt(), wst_search_nodes_checked);
                        break;
                    case 1:
                        total_wst_insert++;
                        int key = random.nextInt();
                        wst.insert(key, wst_insert_nodes_checked, wst_insert_rotates);
                        wstKeys.add(key);
                        break;
                    case 2:
                        if(!wstKeys.isEmpty()) {
                            total_wst_delete++;
                            int indexToDelete = random.nextInt(wstKeys.size());
                            int keyToDelete = wstKeys.get(indexToDelete);

                            wst.delete(wst.treeSearch(wst.root,keyToDelete, dummy), wst_delete_nodes_checked, wst_delete_rotates);
                            int lastIndex = wstKeys.size() - 1;
                            wstKeys.set(indexToDelete, wstKeys.get(lastIndex));
                            wstKeys.remove(lastIndex);
                        }
                }
            }

            wst.checkBalance(wst.root);

            total_bst_search_nodes += bst_search_nodes_checked.get();
            total_bst_insert_nodes += bst_insert_nodes_checked.get();
            total_bst_delete_nodes += bst_delete_nodes_checked.get();

            total_wst_search_nodes += wst_search_nodes_checked.get();
            total_wst_insert_nodes += wst_insert_nodes_checked.get();
            total_wst_insert_rotates += wst_insert_rotates.get();
            total_wst_delete_nodes += wst_delete_nodes_checked.get();
            total_wst_delete_rotates += wst_delete_rotates.get();

        }

        System.out.println();
        System.out.printf("Average BST height: %s\n", total_bst_height / iterations);
        System.out.printf("Average BST search nodes checked: %.3f\n",
                (double)total_bst_search_nodes / total_bst_search);
        System.out.printf("Average BST insert nodes checked: %.3f\n",
                (double)total_bst_insert_nodes / total_bst_insert);
        System.out.printf("Average BST delete nodes checked: %.3f\n",
                (double)total_bst_delete_nodes / total_bst_delete);

        System.out.printf("Average WAVL height: %s\n", total_wavl_height / iterations);
        System.out.printf("Average WAVL search nodes checked: %.3f\n",
                (double)total_wst_search_nodes / total_wst_search);
        System.out.printf("Average WAVL insert nodes checked: %.3f\n",
                (double)total_wst_insert_nodes / total_wst_insert);
        System.out.printf("Average WAVL insert rotates: %.3f\n",
                (double)total_wst_insert_rotates / total_wst_insert);
        System.out.printf("Average WAVL delete nodes checked: %.3f\n",
                (double)total_wst_delete_nodes / total_wst_delete);
        System.out.printf("Average WAVL delete rotates: %.3f\n",
                (double)total_wst_delete_rotates / total_wst_delete);


    }
}
