package com.configvault.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servlet filter implementing a simple token-bucket rate limiter.
 *
 * <p>Rate limits are applied per client IP address using an in-memory
 * {@link ConcurrentHashMap}. Each client gets a bucket with a configurable
 * number of tokens that are refilled at a configurable rate.</p>
 *
 * <p>This filter only applies to requests matching the {@code /api/**} path
 * pattern. Non-API requests pass through without rate limiting.</p>
 *
 * <p>Configuration properties:</p>
 * <ul>
 *   <li>{@code rate-limit.capacity} - Maximum number of tokens per bucket</li>
 *   <li>{@code rate-limit.refill-tokens} - Number of tokens to add on each refill</li>
 *   <li>{@code rate-limit.refill-duration-seconds} - Duration between refills in seconds</li>
 * </ul>
 *
 * @author ConfigVault Team
 * @since 1.0.0
 */
@Component
@Order(1)
@Slf4j
public class RateLimitFilter implements Filter {

    private static final String RATE_LIMIT_RESPONSE_BODY =
            "{\"success\":false,\"message\":\"Rate limit exceeded. Try again later.\",\"status\":429}";

    private final ConcurrentHashMap<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.capacity:100}")
    private int capacity;

    @Value("${app.rate-limit.refill-tokens:100}")
    private int refillTokens;

    @Value("${app.rate-limit.refill-duration-seconds:60}")
    private long refillDurationSeconds;

    /**
     * Initializes the rate limit filter.
     *
     * @param filterConfig the filter configuration
     */
    @Override
    public void init(FilterConfig filterConfig) {
        log.info("RateLimitFilter initialized - capacity: {}, refillTokens: {}, refillDurationSeconds: {}",
                capacity, refillTokens, refillDurationSeconds);
    }

    /**
     * Applies rate limiting to incoming API requests.
     *
     * <p>For each request to {@code /api/**}, the filter checks the client's
     * token bucket. If tokens are available, the request proceeds and a token
     * is consumed. If no tokens remain, a 429 Too Many Requests response is
     * returned with a JSON error body.</p>
     *
     * @param servletRequest  the incoming request
     * @param servletResponse the outgoing response
     * @param filterChain     the filter chain
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestUri = request.getRequestURI();

        // Only apply rate limiting to API paths
        if (!requestUri.startsWith("/api/")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String clientIp = getClientIp(request);
        RateLimitBucket bucket = buckets.computeIfAbsent(clientIp,
                ip -> new RateLimitBucket(capacity, System.nanoTime()));

        synchronized (bucket) {
            refillBucket(bucket);

            if (bucket.tokens > 0) {
                bucket.tokens--;
                log.debug("Rate limit check passed for IP '{}'. Remaining tokens: {}", clientIp, bucket.tokens);
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                log.warn("Rate limit exceeded for IP '{}'. Request to '{}' rejected.", clientIp, requestUri);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(RATE_LIMIT_RESPONSE_BODY);
            }
        }
    }

    /**
     * Cleanup resources on filter destruction.
     */
    @Override
    public void destroy() {
        log.info("RateLimitFilter destroyed. Clearing {} tracked IP buckets.", buckets.size());
        buckets.clear();
    }

    /**
     * Refills the token bucket based on elapsed time since the last refill.
     *
     * @param bucket the rate limit bucket to refill
     */
    private void refillBucket(RateLimitBucket bucket) {
        long now = System.nanoTime();
        long elapsedNanos = now - bucket.lastRefillTime;
        long refillDurationNanos = refillDurationSeconds * 1_000_000_000L;

        if (elapsedNanos >= refillDurationNanos) {
            long refillCycles = elapsedNanos / refillDurationNanos;
            long tokensToAdd = refillCycles * refillTokens;
            bucket.tokens = (int) Math.min(capacity, bucket.tokens + tokensToAdd);
            bucket.lastRefillTime = now;
            log.trace("Refilled bucket: added {} tokens ({} cycles)", tokensToAdd, refillCycles);
        }
    }

    /**
     * Extracts the client IP address from the request, checking common proxy headers first.
     *
     * @param request the HTTP request
     * @return the client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            String ip = xForwardedFor.split(",")[0].trim();
            log.trace("Client IP resolved from X-Forwarded-For header: '{}'", ip);
            return ip;
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            log.trace("Client IP resolved from X-Real-IP header: '{}'", xRealIp);
            return xRealIp;
        }

        String remoteAddr = request.getRemoteAddr();
        log.trace("Client IP resolved from remote address: '{}'", remoteAddr);
        return remoteAddr;
    }

    /**
     * Inner class representing a rate limit token bucket for a single client.
     *
     * <p>Tracks the current number of available tokens and the timestamp
     * of the last refill operation.</p>
     */
    private static class RateLimitBucket {

        /** Current number of available tokens. */
        int tokens;

        /** Timestamp of the last refill in nanoseconds (from {@link System#nanoTime()}). */
        long lastRefillTime;

        /**
         * Constructs a new rate limit bucket.
         *
         * @param tokens         the initial number of tokens
         * @param lastRefillTime the initial refill timestamp in nanoseconds
         */
        RateLimitBucket(int tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }
}
