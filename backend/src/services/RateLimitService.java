package services;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitService {

    private static final int MAX_MESSAGES = 5;
    private static final long WINDOW_MILLIS = 1000L;

    private final ConcurrentHashMap<String, Deque<Long>> sendTimestamps =
            new ConcurrentHashMap<>();

    /**
     * Returns true if the user has exceeded the rate limit
     * (more than MAX_MESSAGES sends within WINDOW_MILLIS).
     */
    public boolean isRateLimited(String userId) {

        long now = System.currentTimeMillis();

        Deque<Long> timestamps =
                sendTimestamps.computeIfAbsent(
                        userId, k -> new ArrayDeque<>());

        synchronized (timestamps) {

            while (!timestamps.isEmpty()
                    && now - timestamps.peekFirst() > WINDOW_MILLIS) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_MESSAGES) {
                return true;
            }

            timestamps.addLast(now);
            return false;
        }
    }
}
