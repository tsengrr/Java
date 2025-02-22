import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Indexer implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Trie> textTries;

    public Indexer() {
        this.textTries = new ArrayList<>();
    }

    public void addTrie(Trie trie) {
        textTries.add(trie);
    }

    public Trie getTrie(int index){
        if (index < 0 || index >= textTries.size()){
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return textTries.get(index);
    }

    public int getSize() {
        return textTries.size();
    }

    public int getTrieSize(int index) {
        if (index < 0 || index >= textTries.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return textTries.get(index).getSize();
    }
}

class TrieNode implements Serializable {
    private static final long serialVersionUID = 1L;
    Map<Character, TrieNode> children;
    int cntEndOfWord;
    int prefixCount;

    TrieNode() {
        children = new ConcurrentHashMap<>();
        cntEndOfWord = 0;
        //prefixCount = 0;
    }
}

class Trie implements Serializable {
    private static final long serialVersionUID = 1L;
    TrieNode root;

    Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            //node.prefixCount++;
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.cntEndOfWord++;
    }

    public int cntHappen(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return 0;
            }
        }
        return node.cntEndOfWord;
    }
    
    public int getSize() {
        return getSize(root);
    }

    private int getSize(TrieNode node) {
        if (node == null) {
            return 0;
        }

        int size = node.cntEndOfWord;
        for (TrieNode child : node.children.values()) {
            size += getSize(child);
        }
        return size;
    }

    
}

class CustomBinaryExample {
    public static void serialize(Indexer indexer, String fileName) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {
            dos.writeInt(indexer.getSize());
            for (int i = 0; i < indexer.getSize(); i++) {
                serializeTrie(dos, indexer.getTrie(i));
            }
        }
    }

    private static void serializeTrie(DataOutputStream dos, Trie trie) throws IOException {
        serializeTrieNode(dos, trie.root);
    }

    private static void serializeTrieNode(DataOutputStream dos, TrieNode node) throws IOException {
        dos.writeInt(node.cntEndOfWord);
        dos.writeInt(node.children.size());
        for (char key : node.children.keySet()) {
            dos.writeChar(key);
            serializeTrieNode(dos, node.children.get(key));
        }
    }

    public static Indexer deserialize(String fileName) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
            int size = dis.readInt();
            Indexer indexer = new Indexer();
            for (int i = 0; i < size; i++) {
                indexer.addTrie(deserializeTrie(dis));
            }
            return indexer;
        }
    }

    private static Trie deserializeTrie(DataInputStream dis) throws IOException {
        Trie trie = new Trie();
        trie.root = deserializeTrieNode(dis);
        return trie;
    }

    private static TrieNode deserializeTrieNode(DataInputStream dis) throws IOException {
        TrieNode node = new TrieNode();
        node.cntEndOfWord = dis.readInt();
        int childrenSize = dis.readInt();
        for (int i = 0; i < childrenSize; i++) {
            char key = dis.readChar();
            node.children.put(key, deserializeTrieNode(dis));
        }
        return node;
    }
}
