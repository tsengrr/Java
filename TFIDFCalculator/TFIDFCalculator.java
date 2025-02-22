import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TFIDFCalculator {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("請輸入檔案名稱");
            return;
        }
        String fileName = args[0], testName = args[1];
        String testcases = "";

        try {
            testcases = Files.readString(Paths.get(testName));
            List<List<String>> texts = tidyUpDoc(fileName);
            String[] stringArrTestcases = testcases.split("\\s+");
            
            List<Trie> textTries = new ArrayList<>();
            
            for (List<String> text : texts){
                Trie trie = new Trie();
                for (String s : text){
                    trie.insert(s);
                }
                textTries.add(trie);
            }

            File file = new File("output.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            int testLen = stringArrTestcases.length / 2;
            for (int i=0; i<testLen; i++){
                String queryWord = stringArrTestcases[i];
                int queryDocNumber = Integer.parseInt(stringArrTestcases[i+testLen]);

                List<String> currText = texts.get(queryDocNumber);
                double tfidf = tfIdfCalculate(currText, texts, queryWord, queryDocNumber, textTries);
                if(i==testLen-1){
                    bw.write(String.format("%.5f", tfidf));
                }
                else{
                    bw.write(String.format("%.5f", tfidf) + " ");
                }
            }
             bw.close();

        }catch (IOException e) {
            System.err.println("無法讀取文件 " + fileName);
            e.printStackTrace();
            return;
        }

    }

    public static List<List<String>> tidyUpDoc(String fileName) {
        List<String> textList = new ArrayList<>();
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(fileName));
            String line = "", temp;
            int i = 0;
            while ((temp = bufReader.readLine()) != null) {
                line += temp;
                i++;
                if (i == 5) {
                    textList.add(line);
                    i = 0;
                    line = "";
                }
            }

            bufReader.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
        
        List<List<String>> texts = new ArrayList<>();

        for (String currText : textList){
            List<String> temp = new ArrayList<>();

            String fixedcurrText = currText.toLowerCase().replaceAll("[^a-z]+", " ")
                .replaceAll("^[^a-z]+|[^a-z]+$", "");
            String[] contextList = fixedcurrText.split(" ");
            for (var s : contextList){
                temp.add(s);
            }
            texts.add(temp);
        }
        
        return texts;
    }

    public static double tf(List<String> doc, String term, Trie trie) {    
        double number_term_in_doc = (double)trie.cntHappen(term);
        return  number_term_in_doc / (double)trie.getSize();
    }

    public static double idf(List<List<String>> docs, String term, List<Trie> texTries) {
        double number_doc_contain_term = 0;
        for (Trie trie : texTries) {
            if (trie.cntHappen(term) > 0) {
                number_doc_contain_term++;
            }
        }
        return Math.log((double)texTries.size() / number_doc_contain_term);
    }
    
    public static double tfIdfCalculate(List<String> doc, List<List<String>> docs, String term, int queryDocNumber, List<Trie> texTries) {
        return tf(doc,term, texTries.get(queryDocNumber)) * idf(docs,term, texTries);
    }
}


class TrieNode  {
    Map<Character, TrieNode> children;
    int cntEndOfWord;
    int prefixCount;
  
    TrieNode() {
      children = new HashMap<>();
      cntEndOfWord = 0;
      prefixCount = 0;
    }
  }
  
  class Trie  {
    TrieNode root;
  
    Trie() {
      root = new TrieNode();
    }
  
    // 插入一个单词到 Trie
    public void insert(String word) {
      TrieNode node = root;
      for (char c : word.toCharArray()) {
        node.prefixCount++;
        node.children.putIfAbsent(c, new TrieNode());
        node = node.children.get(c);
      }
      node.cntEndOfWord++;
    }
  
    // 搜索 Trie 中是否存在该单词
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
    
    // 返回包含指定前缀的单词数量
    public int countWordsWithPrefix(String prefix) {
      TrieNode node = root;
      for (char c : prefix.toCharArray()) {
        node = node.children.get(c);
        if (node == null) {
          return 0;
        }
      }
      return node.prefixCount;
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
  