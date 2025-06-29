package com.enotes.monolithic.service;

import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.List;

public interface CacheManagerService {

    public Collection<String> getCacheNames();

    public Collection<Cache> getAllCache();

    public Cache getCache(String cacheName);

    public void removeAllCache();

    public void removeCacheByName(List<String> cacheNames);
}