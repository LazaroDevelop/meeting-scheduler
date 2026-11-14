package net.space.developer.meetingscheduler.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Enables the Caffeine caches declared in {@code spring.cache.*}.
 * <p>
 * The caching advice runs outside the transactional one, so evictions happen only after
 * the write has been committed and a concurrent read cannot re-cache stale data.
 */
@Configuration
@EnableCaching(order = Ordered.HIGHEST_PRECEDENCE)
public class CacheConfig {
}
