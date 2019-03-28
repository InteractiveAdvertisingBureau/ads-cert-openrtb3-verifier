package net.media.adscert.verification.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.security.PublicKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

public class DefaultGuavaCacheBuilder {
	private Long maximumSize;
	private Duration expireAfterAccess;
	private Duration expireAfterWrite;

	private DefaultGuavaCacheBuilder() {
		this.maximumSize = 1000L;
		this.expireAfterWrite = Duration.of(30, ChronoUnit.DAYS);
	}

	public static DefaultGuavaCacheBuilder newBuilder() {
		return new DefaultGuavaCacheBuilder();
	}

	public DefaultGuavaCacheBuilder setMaximumSize(Long maximumSize) {
		this.maximumSize = maximumSize;
		return this;
	}

	public DefaultGuavaCacheBuilder setExpireAfterAccess(Duration expireAfterAccess) {
		this.expireAfterAccess = expireAfterAccess;
		return this;
	}

	public DefaultGuavaCacheBuilder setExpireAfterWrite(Duration expireAfterWrite) {
		this.expireAfterWrite = expireAfterWrite;
		return this;
	}

	public Cache<String, PublicKey> build() {
		return CacheBuilder.newBuilder().maximumSize(this.maximumSize)
				.expireAfterAccess(this.expireAfterAccess)
				.expireAfterWrite(this.expireAfterWrite)
				.build();
	}
}
