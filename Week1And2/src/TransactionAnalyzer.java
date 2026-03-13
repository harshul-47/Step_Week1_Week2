import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time; // timestamp in milliseconds

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class TransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public List<String> findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                result.add("(" + other.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Two-Sum with 1-hour window
    public List<String> findTwoSumWithWindow(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                long diff = Math.abs(t.time - other.time);

                if (diff <= 3600000) { // 1 hour
                    result.add("(" + other.id + ", " + t.id + ")");
                }
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Duplicate detection
    public List<String> detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();
        List<String> duplicates = new ArrayList<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                duplicates.add(
                        "Duplicate: amount=" + list.get(0).amount +
                                ", merchant=" + list.get(0).merchant
                );
            }
        }

        return duplicates;
    }

    // K-Sum
    public List<List<Integer>> findKSum(int k, int target) {

        List<List<Integer>> result = new ArrayList<>();
        backtrack(0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int start, int k, int target,
                           List<Integer> current,
                           List<List<Integer>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0 || target < 0)
            return;

        for (int i = start; i < transactions.size(); i++) {

            current.add(transactions.get(i).id);

            backtrack(
                    i + 1,
                    k - 1,
                    target - transactions.get(i).amount,
                    current,
                    result
            );

            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        long now = System.currentTimeMillis();

        analyzer.addTransaction(
                new Transaction(1, 500, "Store A", "acc1", now));

        analyzer.addTransaction(
                new Transaction(2, 300, "Store B", "acc2", now));

        analyzer.addTransaction(
                new Transaction(3, 200, "Store C", "acc3", now));

        analyzer.addTransaction(
                new Transaction(4, 500, "Store A", "acc4", now));

        System.out.println("Two-Sum (target=500):");
        System.out.println(analyzer.findTwoSum(500));

        System.out.println("\nTwo-Sum within 1 hour:");
        System.out.println(analyzer.findTwoSumWithWindow(500));

        System.out.println("\nDuplicate detection:");
        System.out.println(analyzer.detectDuplicates());

        System.out.println("\nK-Sum (k=3, target=1000):");
        System.out.println(analyzer.findKSum(3, 1000));
    }
}