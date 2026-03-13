import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCacheSystem {

    private static final int L1_CAPACITY = 10000;
    private static final int L2_CAPACITY = 100000;

    // L1 cache (memory)
    private LinkedHashMap<String, VideoData> L1;

    // L2 cache (SSD simulated)
    private LinkedHashMap<String, VideoData> L2;

    // L3 database
    private HashMap<String, VideoData> database;

    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;

    public MultiLevelCacheSystem() {

        L1 = new LinkedHashMap<String, VideoData>(L1_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> e) {
                return size() > L1_CAPACITY;
            }
        };

        L2 = new LinkedHashMap<String, VideoData>(L2_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> e) {
                return size() > L2_CAPACITY;
            }
        };

        database = new HashMap<>();
    }

    // Add video to database
    public void addVideoToDatabase(String id, String content) {
        database.put(id, new VideoData(id, content));
    }

    // Get video
    public VideoData getVideo(String videoId) {

        // L1 Cache
        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2 Cache
        if (L2.containsKey(videoId)) {

            L2Hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2.get(videoId);

            // promote to L1
            L1.put(videoId, video);

            System.out.println("Promoted to L1");

            return video;
        }

        System.out.println("L2 Cache MISS");

        // L3 Database
        if (database.containsKey(videoId)) {

            L3Hits++;
            System.out.println("L3 Database HIT (150ms)");

            VideoData video = database.get(videoId);

            L2.put(videoId, video);

            return video;
        }

        System.out.println("Video not found");
        return null;
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        double l1Rate = total == 0 ? 0 : (L1Hits * 100.0 / total);
        double l2Rate = total == 0 ? 0 : (L2Hits * 100.0 / total);
        double l3Rate = total == 0 ? 0 : (L3Hits * 100.0 / total);

        System.out.println("\nCache Statistics:");

        System.out.println("L1: Hit Rate " + String.format("%.2f", l1Rate) + "% Avg Time: 0.5ms");

        System.out.println("L2: Hit Rate " + String.format("%.2f", l2Rate) + "% Avg Time: 5ms");

        System.out.println("L3: Hit Rate " + String.format("%.2f", l3Rate) + "% Avg Time: 150ms");

        System.out.println("Overall Hit Rate: " +
                String.format("%.2f", (l1Rate + l2Rate)) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        cache.addVideoToDatabase("video_123", "Movie Data 123");
        cache.addVideoToDatabase("video_999", "Movie Data 999");

        cache.getVideo("video_123");
        cache.getVideo("video_123");

        cache.getVideo("video_999");

        cache.getStatistics();
    }
}