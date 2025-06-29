package com.enotes.monolithic.controller;

import com.enotes.monolithic.service.CacheManagerService;
import com.enotes.monolithic.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Tag(name = "Cache", description = "Cache Management APIs")
@RestController
@RequestMapping("/api/v1/cache")
public class CacheControllerV1 {
    @Autowired
    private CacheManagerService cacheManagerService;

    @Operation(summary = "Get All Cache Names", tags = { "Cache" }, description = "Get All Cache Names")
    @GetMapping("/get-all-cache-names")
    public ResponseEntity<?> getAllCacheNames() {
        Collection<String> cacheNames = cacheManagerService.getCacheNames();
        return CommonUtil.createBuildResponse(cacheNames, HttpStatus.OK);
    }

    @Operation(summary = "Get All Cache", tags = { "Cache" }, description = "Get All Cache")
    @GetMapping("/get-all-cache")
    public ResponseEntity<?> getAllCache() {
        Collection<Cache> cacheNames = cacheManagerService.getAllCache();
        return CommonUtil.createBuildResponse(cacheNames, HttpStatus.OK);
    }


    @Operation(summary = "Get Cache", tags = { "Cache" }, description = "Get Cache")
    @GetMapping("/{cacheName}")
    public ResponseEntity<?> getCache(@PathVariable String cacheName) {
        Cache cache = cacheManagerService.getCache(cacheName);
        return CommonUtil.createBuildResponse(cache, HttpStatus.OK);
    }

    @Operation(summary = "Remove All Cache", tags = { "Cache" }, description = "Remove All Cache")
    @DeleteMapping("/remove-all-cache")
    public ResponseEntity<?> removeAllCache() {
        cacheManagerService.removeAllCache();
        return CommonUtil.createBuildResponseMessage("All cache removed", HttpStatus.OK);
    }

}
