package net.media.adscert.verification.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

public class DefaultGuavaCacheBuilder {
	private Long maximumSize;
	private Integer refreshTime;
	private TimeUnit refreshTimeUnit;

	private DefaultGuavaCacheBuilder() {
		this.maximumSize = 1000L;
		this.refreshTime = 30;
	}

	public static DefaultGuavaCacheBuilder newBuilder() {
		return new DefaultGuavaCacheBuilder();
	}

	public DefaultGuavaCacheBuilder setMaximumSize(Long maximumSize) {
		this.maximumSize = maximumSize;
		return this;
	}

	public DefaultGuavaCacheBuilder setRefreshTime(Integer refreshTime, TimeUnit timeUnit) {
		this.refreshTime = refreshTime;
		this.refreshTimeUnit = timeUnit;
		return this;
	}

	public Cache<String, PublicKey> build() {
		return CacheBuilder.newBuilder().maximumSize(this.maximumSize)
				.refreshAfterWrite(this.refreshTime, this.refreshTimeUnit)
				.build();
	}
}
