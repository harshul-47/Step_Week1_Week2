import java.util.*;

public class PlagiarismDetector {

    private int N = 5; // size of n-gram

    // n-gram → set of documents containing it
    private HashMap<String, Set<String>> index;

    // document → list of its n-grams
    private HashMap<String, List<String>> documentNgrams;

    public PlagiarismDetector() {
        index = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());

            index.get(gram).add(docId);
        }
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");

        List<String> grams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            grams.add(gram.toString().trim());
        }

        return grams;
    }

    // Analyze document for plagiarism
    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            Set<String> docs = index.get(gram);

            if (docs == null) continue;

            for (String otherDoc : docs) {

                if (otherDoc.equals(docId)) continue;

                matchCount.put(otherDoc,
                        matchCount.getOrDefault(otherDoc, 0) + 1);
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (String otherDoc : matchCount.keySet()) {

            int matches = matchCount.get(otherDoc);

            double similarity =
                    (matches * 100.0) / ngrams.size();

            System.out.println(
                    "Found " + matches +
                            " matching n-grams with " + otherDoc +
                            " → Similarity: " +
                            String.format("%.2f", similarity) + "%"
            );

            if (similarity > 60) {
                System.out.println("PLAGIARISM DETECTED");
            } else if (similarity > 10) {
                System.out.println("Suspicious similarity");
            }
        }
    }

    // Demo
    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String doc1 =
                "machine learning is a field of artificial intelligence " +
                        "that focuses on building systems that learn from data";

        String doc2 =
                "machine learning is a field of artificial intelligence " +
                        "that focuses on building systems that learn automatically";

        String doc3 =
                "the quick brown fox jumps over the lazy dog";

        detector.addDocument("essay_089.txt", doc1);
        detector.addDocument("essay_092.txt", doc2);
        detector.addDocument("essay_123.txt", doc1);

        detector.analyzeDocument("essay_123.txt");
    }
}