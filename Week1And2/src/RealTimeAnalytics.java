import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalytics {

    // page → visit count
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // page → unique users
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // source → visit count
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    // process event
    public synchronized void processEvent(PageEvent event) {

        pageViews.put(
                event.url,
                pageViews.getOrDefault(event.url, 0) + 1
        );

        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        trafficSources.put(
                event.source,
                trafficSources.getOrDefault(event.source, 0) + 1
        );
    }

    // get top pages
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<Map.Entry<String, Integer>> result = new ArrayList<>();

        int count = 0;

        while (!pq.isEmpty() && count < 10) {
            result.add(pq.poll());
            count++;
        }

        return result;
    }

    // dashboard
    public void getDashboard() {

        System.out.println("Top Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(
                    rank + ". " + url +
                            " - " + views + " views (" +
                            unique + " unique)"
            );

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = 0;
        for (int c : trafficSources.values()) total += c;

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);

            double percent = (count * 100.0) / total;

            System.out.println(
                    source + ": " +
                            String.format("%.1f", percent) + "%"
            );
        }
    }

    public static void main(String[] args) {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.processEvent(
                new PageEvent("/article/breaking-news", "user_123", "google"));

        analytics.processEvent(
                new PageEvent("/article/breaking-news", "user_456", "facebook"));

        analytics.processEvent(
                new PageEvent("/sports/championship", "user_789", "direct"));

        analytics.processEvent(
                new PageEvent("/sports/championship", "user_123", "google"));

        analytics.processEvent(
                new PageEvent("/article/breaking-news", "user_999", "google"));

        analytics.getDashboard();
    }
}