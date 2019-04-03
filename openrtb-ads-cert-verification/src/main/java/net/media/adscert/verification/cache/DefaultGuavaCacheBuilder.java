package net.media.adscert.verification.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.security.PublicKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * Builder to create a ready-to-use Guava cache.
 *
 * @author pranav.a
 * @author anupam.v
 *
 * @since 1.0
 */
public class DefaultGuavaCacheBuilder {
	private Long maximumSize;
	private Duration expireAfterAccess;
	private Duration expireAfterWrite;

	private DefaultGuavaCacheBuilder() {
		this.maximumSize = 1000L;
		this.expireAfterWrite = Duration.of(30, ChronoUnit.DAYS);
	}

	/**
	 * Creates a {@link DefaultGuavaCacheBuilder} for creating a {@link Cache} with default settings.
	 *
	 * @return {@link DefaultGuavaCacheBuilder}
	 */
	public static DefaultGuavaCacheBuilder newBuilder() {
		return new DefaultGuavaCacheBuilder();
	}

	/**
	 * See {@link CacheBuilder#maximumSize(long)}.
	 *
	 * @param maximumSize maximum entries the cache can hold
	 * @return {@link DefaultGuavaCacheBuilder}
	 */
	public DefaultGuavaCacheBuilder setMaximumSize(Long maximumSize) {
		this.maximumSize = maximumSize;
		return this;
	}

	/**
	 * See {@link CacheBuilder#expireAfterAccess(Duration)}.
	 *
	 * @param expireAfterAccess represents duration of time after which an entry should be removed
	 * @return {@link DefaultGuavaCacheBuilder}
	 */
	public DefaultGuavaCacheBuilder setExpireAfterAccess(Duration expireAfterAccess) {
		this.expireAfterAccess = expireAfterAccess;
		return this;
	}

	/**
	 * See {@link CacheBuilder#expireAfterWrite(Duration)}.
	 *
	 * @param expireAfterWrite represents duration of time that should elapse since an entry's
	 *                          creation or latest replacement of its value before the entry is removed
	 * @return {@link DefaultGuavaCacheBuilder}
	 */
	public DefaultGuavaCacheBuilder setExpireAfterWrite(Duration expireAfterWrite) {
		this.expireAfterWrite = expireAfterWrite;
		return this;
	}

	/**
	 * Calls {@link CacheBuilder#build()} with the values configured via {@link DefaultGuavaCacheBuilder}.
	 *
	 * @return {@link Cache}
	 */
	public Cache<String, PublicKey> build() {
		return CacheBuilder.newBuilder().maximumSize(this.maximumSize)
				.expireAfterAccess(this.expireAfterAccess)
				.expireAfterWrite(this.expireAfterWrite)
				.build();
	}
}
