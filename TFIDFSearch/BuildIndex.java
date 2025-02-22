import java.io.*;
import java.util.*;
import java.util.regex.*;
public class BuildIndex {
    public static void main(String[] args) throws IOException {
        String filePath = args[0];

        Pattern filenamePattern = Pattern.compile("corpus\\d+(?=.txt)");
        Matcher matcher = filenamePattern.matcher(filePath);
        matcher.find();

        final String fileName = matcher.group(0);
        if ((new File(fileName)).exists()) return;

        List<List<String>> texts = tidyUpDoc(filePath);
        Indexer idxr = new Indexer();

        // 串行流创建Trie并添加到Indexer，避免并行流可能导致的竞争条件
        texts.forEach(text -> {
            Trie trie = new Trie();
            text.forEach(trie::insert);
            idxr.addTrie(trie);
        });

        try {
            System.err.println("before");
            CustomBinaryExample.serialize(idxr, fileName + ".ser");
            System.err.println("after");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> tidyUpDoc(String fileName) {
        List<List<String>> textList = new ArrayList<>();
        try (BufferedReader bufReader = new BufferedReader(new FileReader(fileName))) {
            String line = "", temp;
            int i = 0;
            List<String> currentText = new ArrayList<>();
            while ((temp = bufReader.readLine()) != null) {
                line += temp;
                i++;
                if (i == 5) {
                    currentText.addAll(processText(line));
                    textList.add(currentText);
                    currentText = new ArrayList<>();
                    i = 0;
                    line = "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textList;
    }

    private static List<String> processText(String currText) {
        List<String> temp = new ArrayList<>();
        String fixedCurrText = currText.toLowerCase().replaceAll("[^a-z]+", " ").trim();
        Collections.addAll(temp, fixedCurrText.split(" "));
        return temp;
    }
}
