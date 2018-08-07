/**
 * BTree Class for CS321
 *
 * @author Keener
 * @author Luke Grice
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class BTree<T extends Comparable<? super T> & Serializable> implements Serializable {

    public transient Node root;
    private final int t;                            //minimum degree
    private final int maxKeys;

    public BTree(int t) {
        this.t = t;
        root = null;

        int maxChildren = 2 * t;
        this.maxKeys = maxChildren - 1;
    }

    public static <K extends Comparable<? super K> & Serializable> Key<K> search(ZipFile zipFile, K key) throws IOException, ClassNotFoundException {
        ObjectInputStream objectStream;
//        objectStream = new ObjectInputStream(zipFile.getInputStream(zipFile.getEntry("tree")));
//	    BTree<K> tree = (BTree<K>)objectStream.readObject();
        objectStream = new ObjectInputStream(zipFile.getInputStream(zipFile.getEntry("n")));
        //noinspection unchecked
        BTree<K>.Node rootNode = (BTree<K>.Node) objectStream.readObject();
//	    return zipSearch(zipFile, key, tree.root, "");
        return zipSearch(zipFile, key, rootNode, "");
    }

    private static <K extends Comparable<? super K> & Serializable> Key<K> zipSearch(ZipFile zipFile, K key, BTree<K>.Node parent, String prefix) throws ClassNotFoundException, IOException {
        int keyIndex = Collections.binarySearch(parent.keys, new Key<>(key, 1));
        if (keyIndex >= 0) {
            return parent.keys.get(keyIndex);
        } else {
            int searchIndex = -1 - keyIndex;
            try {
                ObjectInputStream objectStream = new ObjectInputStream(zipFile.getInputStream(zipFile.getEntry(prefix + searchIndex + "/n")));  // Note this is not a newline character
                BTree.Node n = (BTree.Node) objectStream.readObject();
                //noinspection unchecked
                return zipSearch(zipFile, key, n, prefix + searchIndex + '/');
            } catch (NullPointerException e) {
                // this probably happened because zipFile.getEntry returns null if there is no entry for the input string, which is the case when we do not find our key
                if (e.getMessage().equals("entry")) // This is always the message for a missing entry, but this check might not be necessary
                {
                    return new Key<>(key, 0);
                } else {
                    throw e;
                }
            }
        }
    }

    public void write(OutputStream out) throws IOException {
        BTree.write(out, this);
    }

    public static void write(OutputStream out, BTree tree) throws IOException {
        ZipOutputStream zipStream = new ZipOutputStream(out);
        zipStream.setLevel(9);    // Set compression to highest level, may be unnecessary
        zipStream.putNextEntry(new ZipEntry("tree"));
        ObjectOutputStream objectStream = new ObjectOutputStream(zipStream);
        objectStream.writeObject(tree);
        zipStream.closeEntry();

        zipNodes(zipStream, tree.root, "");
        zipStream.close();
    }

    private static void zipNodes(ZipOutputStream zipStream, BTree.Node parent, String prefix) throws IOException {
        ZipEntry entry = new ZipEntry(prefix + "n");    // File entry for parent node
        zipStream.putNextEntry(entry);
        ObjectOutputStream objectStream = new ObjectOutputStream(zipStream);
        objectStream.writeObject(parent);
        zipStream.closeEntry();

        for (int i = 0; i < parent.children.size(); i++) {
            zipNodes(zipStream, (BTree.Node) parent.children.get(i), prefix + i + '/');
        }
    }

    /*
     * Inserts a key into the Btree
     *
     * k - sequence containing a long of binary bases
     */
    public void insert(T k) {
//		System.out.println("INSERT: " + k.getBases());
        // special case - empty tree
        if (root == null) {
            root = new Node(k);
        } else //non-empty trees
        //if root is full
        {
            if (root.keys.size() == maxKeys) {
                //creates a new node to make as root
                Node newRoot = new Node(false);
                //adds the current root as a child
                newRoot.children.add(root);
                //splits the full root
                newRoot.splitChild(0, root);
                //determines child position for new sequence
                int i = 0;
                // TODO verify this and other .compareTos sort in the right order
                if (newRoot.keys.get(0).key.compareTo(k) < 0) {
                    i++;
                }
                //adds sequence to specified child
                newRoot.children.get(i).insertNonFull(k);
                //re-assigns root
                root = newRoot;
            } else {
                //if root is not full, determine proper position of sequence
                root.insertNonFull(k);
            }
        }

    }

    /*
     * Returns the root node
     */
    public Node getRoot() {
        return root;
    }

    public static class Key<E extends Comparable<? super E>> implements Comparable<Key<E>>, Serializable {

        public E key;
        public long count;

        public Key(E key, long count) {
            this.key = key;
            this.count = count;
        }

        @Override
        public int compareTo(Key<E> eKey) {
            return key.compareTo(eKey.key);
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeLong(count);
            out.writeObject(key);
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            count = in.readLong();
            key = (E) in.readObject();
        }
    }

    /**
     * Inner class for nodes
     */
    //class is currently public - making it private made some testing difficult. Not sure if there's a workaround
    public class Node implements Serializable {

        //TODO add readObject and writeObject methods to more carefully serialize node data.
        public ArrayList<Key<T>> keys;
        public transient ArrayList<Node> children;
        boolean isLeaf;

        /**
         * Constructor
         */
        public Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            keys = new ArrayList<Key<T>>();
            children = new ArrayList<Node>();
        }

        /**
         * Constructor for a new root node
         *
         * @param root initial key
         */
        public Node(T root) {
            this(true);
            keys.add(new Key<>(root, 1));
        }

        /**
         * Method to insert a given key into this node. Should only be called if
         * a node is not full
         */
        public void insertNonFull(T k) {
            //get the right-most key index
            int i = keys.size() - 1;
            //if leaf, determine the correct position of the new key
            if (isLeaf) {
                while (i >= 0 && keys.get(i).key.compareTo(k) > 0) {
                    i--;
                }

                if (i >= 0 && keys.get(i).key.compareTo(k) == 0) {
                    keys.get(i).count++;
                } else {
                    //insert key at location
                    keys.add(i + 1, new Key<>(k, 1));
                }
            } else {
                //if not leaf, find child that will get new key
                while (i >= 0 && keys.get(i).key.compareTo(k) < 0) {
                	i--; 
                }                
                
                if (i >= 0 && keys.get(i).key.compareTo(k) == 0) {
                    keys.get(i).count++;
                } else {
                    //check if that child is full
                    if (children.get(i + 1).keys.size() == maxKeys) {
                        //if so, split the child
                        splitChild(i + 1, children.get(i + 1));
                        //after split, check which new branch is going to have new key
                        if (keys.get(i + 1).key.compareTo(k) < 0) {
                            i++;
                        }
                    }
                    children.get(i + 1).insertNonFull(k);
                }
            }
        }


        /*
         * Splits a full node, using i as the split index
         */
        public void splitChild(int i, Node fullNode) {
            //make a new node to contain some of fullnodes keys
            Node newNode = new Node(fullNode.isLeaf);
            //copy last t-1 keys of fullnode into the newnode
            for (int j = 0; j < t - 1; j++) {
                newNode.keys.add(fullNode.keys.get(j + t));
            }
            //copy the last t children of fullnode into newnode
            if (!fullNode.isLeaf) {
                for (int j = 0; j < t; j++) {
                    newNode.children.add(fullNode.children.get(j + t));
                }
                //remove old nodes that have now been transfered
                for (int o = 0; o < t; o++) {
                    fullNode.children.remove(fullNode.children.size() - 1);
                }
            }

            //link new child to node
            this.children.add(i + 1, newNode);
            //add the middle key of full node into the new node
            this.keys.add(i, fullNode.keys.get(t - 1));
            // remove old keys that have now been split
            for (int o = fullNode.keys.size() - 1; o >= t - 1; o--) {
                fullNode.keys.remove(o);
            }
        }

        /*
         * Should search the btree when called on the node.
         *
         * Currently untested.
         */
        public Node search(T k) {
            int i = 0;
            while (i < keys.size() && k.compareTo(keys.get(i).key) > 0) {
                i++;
            }
            if (keys.get(i).key.compareTo(k) == 0) {
                return this;
            }
            if (isLeaf) {
                return null;
            }
            return children.get(i).search(k);
        }

        public void traverse() {
            int i;
            System.out.println("tr start");
            for (i = 0; i < keys.size(); i++) {
                if (!isLeaf) {
                    children.get(i).traverse();
                }
            }
            if (!isLeaf) {
                children.get(i).traverse();
            }
        }

        public ArrayList<T> getKeys() {
            ArrayList<T> keys = new ArrayList<T>();
            for (Key<T> key : this.keys) {
                // For now, just return a key repeatedly if there are multiple instances of it
                for (int i = 0; i < key.count; i++) {
                    keys.add(key.key);
                }
            }
            return keys;
        }
    }
    
    
//  D U M P   T R E E    T O    F I L E 
    /**
     * Uses the inOrderTraversal method to walk through a tree and output the
     * frequency and string value to a new txt file.
     *
     * @param node - The root of the tree
     */
    public void dumpTree(Node node, int seqLength) {
        try {
            PrintWriter writer = new PrintWriter("dump.txt", "UTF-8");
            inOrderTraversal(node, writer, seqLength );
            
            writer.close();
        } catch (Exception e) {
            System.out.println("There was an issue: " + e);
            e.getStackTrace();
        }
    }
    
    /**
     * Recursively traverses the BTree from the root printing out the contents
     * of the nodes through an in-order traversal
     *
     * @param node - pass in a node within the tree (root)
     * @param writer
     */
    public void inOrderTraversal(Node node, PrintWriter writer, int seqLength) {
        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.isLeaf) {
                inOrderTraversal(node.children.get(i), writer, seqLength);
            }
            Key keyObj = node.keys.get(i);
            Sequence seq = (Sequence) keyObj.key;            
//            strArr.add(keyObj.count + " " + BTreeUtil.convertLongToString(seq.sequence, seqLength));
            writer.println(keyObj.count + " " + BTreeUtil.convertLongToString(seq.sequence, seqLength));
        }
    }
}
