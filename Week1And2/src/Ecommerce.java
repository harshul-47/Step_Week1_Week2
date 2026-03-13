import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Ecommerce {

    // Hash table for product stock
    private ConcurrentHashMap<String, AtomicInteger> stockMap;

    // Waiting list for each product (FIFO)
    private ConcurrentHashMap<String, LinkedHashMap<Integer, Integer>> waitingList;

    public Ecommerce() {
        stockMap = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    // Add product with initial stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Instant stock check O(1)
    public String checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) {
            return "Product not found";
        }
        return stock.get() + " units available";
    }

    // Purchase item
    public synchronized String purchaseItem(String productId, int userId) {

        AtomicInteger stock = stockMap.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();
            return "Success, " + remaining + " units remaining";
        }

        // Add to waiting list
        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);
        queue.put(userId, queue.size() + 1);

        return "Added to waiting list, position #" + queue.size();
    }

    // View waiting list
    public void printWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        if (queue.isEmpty()) {
            System.out.println("Waiting list empty");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() + " Position #" + entry.getValue());
        }
    }

    // Benchmark simulation
    public static void main(String[] args) {

        Ecommerce manager = new Ecommerce();

        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println(manager.checkStock("IPHONE15_256GB"));

        for (int i = 1; i <= 105; i++) {
            System.out.println(
                    manager.purchaseItem("IPHONE15_256GB", i)
            );
        }

        manager.printWaitingList("IPHONE15_256GB");
    }
}