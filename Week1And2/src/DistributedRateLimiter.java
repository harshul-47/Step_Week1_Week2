import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    int maxTokens;
    double tokens;
    double refillRate;
    long lastRefillTime;

    TokenBucket(int maxTokens, int refillPeriodSeconds) {
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.refillRate = (double) maxTokens / refillPeriodSeconds;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {
        refill();

        if (tokens >= 1) {
            tokens--;
            return true;
        }

        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();

        double seconds = (now - lastRefillTime) / 1000.0;

        double tokensToAdd = seconds * refillRate;

        tokens = Math.min(maxTokens, tokens + tokensToAdd);

        lastRefillTime = now;
    }

    int getRemainingTokens() {
        return (int) tokens;
    }
}

public class DistributedRateLimiter {

    private ConcurrentHashMap<String, TokenBucket> buckets =
            new ConcurrentHashMap<>();

    private final int MAX_REQUESTS = 1000;
    private final int REFILL_PERIOD = 3600;

    public String checkRateLimit(String clientId) {

        buckets.putIfAbsent(
                clientId,
                new TokenBucket(MAX_REQUESTS, REFILL_PERIOD)
        );

        TokenBucket bucket = buckets.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" +
                    bucket.getRemainingTokens() +
                    " requests remaining)";
        }

        return "Denied (0 requests remaining, retry later)";
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = buckets.get(clientId);

        if (bucket == null)
            return "Client not found";

        int remaining = bucket.getRemainingTokens();
        int used = MAX_REQUESTS - remaining;

        return "{used: " + used +
                ", limit: " + MAX_REQUESTS + "}";
    }

    public static void main(String[] args) {

        DistributedRateLimiter limiter =
                new DistributedRateLimiter();

        System.out.println(
                limiter.checkRateLimit("abc123"));

        System.out.println(
                limiter.checkRateLimit("abc123"));

        System.out.println(
                limiter.checkRateLimit("abc123"));

        System.out.println(
                limiter.getRateLimitStatus("abc123"));
    }
}