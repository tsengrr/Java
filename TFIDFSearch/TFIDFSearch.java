import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.*;

public class TFIDFSearch {
    public static String[] buffer;
    public static int n;
    public static String[] resultLists;
    final public static Pattern andChecker = Pattern.compile(" AND ");
    final public static Pattern spliter = Pattern.compile(" AND | OR ");
    // 全局缓存
    private static final ConcurrentHashMap<String, Integer> wordCountGlobalCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> docCountGlobalCache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("請輸入檔案名稱和測試檔名稱");
            return;
        }
        
        String fileName = args[0], testName = args[1];
        
        try {
            Indexer idxr = CustomBinaryExample.deserialize(fileName + ".ser");
            
            try (BufferedReader bufReader = new BufferedReader(new FileReader(testName))) {
                // 讀取文件
                List<String> lines = bufReader.lines().collect(Collectors.toList());
                n = Integer.parseInt(lines.get(0)); // 第一行:每個 query 輸出 n 筆結果
                buffer = lines.subList(1, lines.size()).toArray(new String[0]);
                resultLists = new String[buffer.length];
                
                // 使用并行流處理每一行
                IntStream.range(0, buffer.length).parallel().forEach(index -> parseLine(index, idxr));
                
                // 寫入文件
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
                    for (var result : resultLists) {
                        bw.write(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void parseLine(int index, Indexer idxr) {
        boolean isAnd = andChecker.matcher(buffer[index]).find();
        String[] words = spliter.split(buffer[index]);
        Integer[] result = isAnd ? andDealer(words, n, idxr) : orDealer(words, n, idxr);

        if (result == null) {
            String resultList = String.join(" ", Stream.generate(() -> "-1").limit(n).toArray(String[]::new));
            resultLists[index] = resultList + "\n";
        } else {
            String resultList = String.join(
                " ",
                Stream.concat(
                    Arrays.stream(result).map(Object::toString),
                    Stream.generate(() -> "-1").limit(n - result.length)
                ).toArray(String[]::new)
            );
            resultLists[index] = resultList + "\n";
        }
    }

    public static Integer[] andDealer(String[] words, int targetLength, Indexer idxr) {
        int size = idxr.getSize();
        Set<Integer> docSet = ConcurrentHashMap.newKeySet();

        Arrays.sort(words, (s1, s2) -> Integer.compare(s2.length(), s1.length()));

        // 使用并行流找到包含第一个單詞的文档
        IntStream.range(0, size).parallel().forEach(i -> {
            Trie trie = idxr.getTrie(i);
            if (trie.cntHappen(words[0]) > 0) {
                docSet.add(i);
            }
        });

        // 檢查這些文档是否包含其余的單詞，并移除不包含的文档
        docSet.parallelStream().forEach(docId -> {
            Trie trie = idxr.getTrie(docId);
            for (int k = 1; k < words.length; k++) {
                if (trie.cntHappen(words[k]) == 0) {
                    docSet.remove(docId);
                    break;
                }
            }
        });

        if (docSet.isEmpty()) {
            return null;
        }

        // 計算每個文档的TF-IDF值
        Map<Integer, Double> TFIDFmap = docSet.parallelStream().collect(Collectors.toMap(
            docId -> docId,
            docId -> tfIdfCalculate(size, words, docId, idxr)
        ));

        // 按TF-IDF值排序，TF-IDF相同时按文档ID排序
        List<Map.Entry<Integer, Double>> sortedList = TFIDFmap.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder())
            .thenComparing(Map.Entry.comparingByKey()))
            .collect(Collectors.toList());

        return sortedList.stream().limit(targetLength).map(Map.Entry::getKey).toArray(Integer[]::new);
    }

    public static Integer[] orDealer(String[] words, int targetLength, Indexer idxr) {
        int size = idxr.getSize();
        Set<Integer> docSet = ConcurrentHashMap.newKeySet();

        // 遍历所有單詞，獲取包含這些單詞的文档索引并集
        Arrays.stream(words).parallel().forEach(word -> {
            IntStream.range(0, size).parallel().forEach(j -> {
                Trie trie = idxr.getTrie(j);
                if (trie.cntHappen(word) > 0) {
                    docSet.add(j);
                }
            });
        });

        if (docSet.isEmpty()) {
            return null;
        }

        Map<Integer, Double> TFIDFmap = docSet.parallelStream().collect(Collectors.toMap(
            docId -> docId,
            docId -> tfIdfCalculate(size, words, docId, idxr)
        ));

        // 按TF-IDF值排序，TF-IDF相同时按文档ID排序
        List<Map.Entry<Integer, Double>> sortedList = TFIDFmap.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder())
            .thenComparing(Map.Entry.comparingByKey()))
            .collect(Collectors.toList());

        return sortedList.stream().limit(targetLength).map(Map.Entry::getKey).toArray(Integer[]::new);
    }

    public static double tfIdfCalculate(int totalsize, String[] words, int queryDocNumber, Indexer idxr) {
        Trie trie = idxr.getTrie(queryDocNumber);
        double sum = 0;
        
        for (String word : words) {
            int cntHappen = wordCountGlobalCache.computeIfAbsent(word + "#" + queryDocNumber, k -> trie.cntHappen(word));
            double tf = (double) cntHappen / (double) idxr.getTrieSize(queryDocNumber);
            
            int number_doc_contain_term = docCountGlobalCache.computeIfAbsent(word, k -> {
                return (int) IntStream.range(0, totalsize).parallel()
                    .filter(i -> idxr.getTrie(i).cntHappen(word) > 0)
                    .count();
            });

            double idf = number_doc_contain_term != 0
                ? Math.log((double) totalsize / number_doc_contain_term)
                : 0;

            sum += tf * idf;
        }

        return sum;
    }
}
