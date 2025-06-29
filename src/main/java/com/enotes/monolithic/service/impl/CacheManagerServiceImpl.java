package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.service.CacheManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CacheManagerServiceImpl implements CacheManagerService {
    Logger logger = LoggerFactory.getLogger(CacheManagerServiceImpl.class);

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Collection<String> getCacheNames() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            logger.info("Cache name: {}", cacheManager.getCache(cacheName));
        }
        return cacheNames;
    }

    @Override
    public Collection<Cache> getAllCache() {
        Collection<String> caches = cacheManager.getCacheNames();
        for (String cache : caches) {
            logger.info("Cache name: {}", cache);
        }
        return caches.stream().map(cacheManager::getCache).toList();
    }

    @Override
    public Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        logger.info("Cache name: {}", cache);
        return cache;
    }

    @Override
    public void removeAllCache() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        logger.warn("removing all cache");
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            logger.info("Removing cache: {}", cache);
            cache.clear();
        }
    }

    @Override
    public void removeCacheByName(List<String> cacheNames) {
        for(String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            logger.info("Removing cache: {}", cache);
            cache.clear();
        }
    }
}
