package net.media.adscert.verification.defaults;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

public class DefaultGuavaCacheBuilder {
	private Long maximumSize;
	private Integer refreshTime;
	private Integer expireTime;

	private DefaultGuavaCacheBuilder() {
		this.maximumSize = 1000L;
		this.refreshTime = 30;
		this.expireTime = 20;
	}

	public DefaultGuavaCacheBuilder newBuilder() {
		return new DefaultGuavaCacheBuilder();
	}

	public DefaultGuavaCacheBuilder setMaximumSize(Long maximumSize) {
		this.maximumSize = maximumSize;
		return this;
	}

	public DefaultGuavaCacheBuilder setRefreshTime(Integer refreshTime) {
		this.refreshTime = refreshTime;
		return this;
	}

	public DefaultGuavaCacheBuilder setExpireTime(Integer expireTime) {
		this.expireTime = expireTime;
		return this;
	}

	public Cache<String, PublicKey> build() {
		return CacheBuilder.newBuilder().maximumSize(this.maximumSize)
				.expireAfterAccess(this.expireTime, TimeUnit.DAYS)
				.refreshAfterWrite(refreshTime, TimeUnit.DAYS)
				.build();
	}
}
