import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    List<String> queries = new ArrayList<>();
}

public class AutocompleteSystem {

    private TrieNode root;
    private HashMap<String, Integer> frequencyMap;

    public AutocompleteSystem() {
        root = new TrieNode();
        frequencyMap = new HashMap<>();
    }

    // Insert query into Trie
    public void insertQuery(String query, int freq) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + freq);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());

            node = node.children.get(c);

            node.queries.add(query);
        }
    }

    // Search suggestions for prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        PriorityQueue<String> pq =
                new PriorityQueue<>(
                        (a, b) -> frequencyMap.get(a) - frequencyMap.get(b)
                );

        for (String q : node.queries) {

            pq.offer(q);

            if (pq.size() > 10)
                pq.poll();
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty())
            result.add(pq.poll());

        Collections.reverse(result);

        return result;
    }

    // Update frequency when a search happens
    public void updateFrequency(String query) {

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);

        insertQuery(query, 0);
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.insertQuery("java tutorial", 1234567);
        system.insertQuery("javascript", 987654);
        system.insertQuery("java download", 456789);
        system.insertQuery("java 21 features", 1);

        System.out.println("Suggestions for 'jav':");

        List<String> results = system.search("jav");

        int rank = 1;

        for (String r : results) {

            System.out.println(
                    rank + ". " + r +
                            " (" + system.frequencyMap.get(r) + " searches)"
            );

            rank++;
        }

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println(
                "\nUpdated frequency for 'java 21 features': "
                        + system.frequencyMap.get("java 21 features")
        );
    }
}