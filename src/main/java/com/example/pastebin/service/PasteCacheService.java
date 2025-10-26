package com.example.pastebin.service;

import com.example.pastebin.dto.Paste;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasteCacheService {
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, Paste> pasteRedisTemplate;

    private static final String METADATA_PREFIX = "pasteMetadata:";
    private static final String CONTENT_PREFIX = "pasteContent:";
    private static final String ACCESS_COUNT_PREFIX = "pasteAccessCount:";

    public void cachePasteMetadata(Paste paste) {
        pasteRedisTemplate.opsForValue().set(METADATA_PREFIX + paste.getId(), paste, Duration.ofHours(6));
    }

    public Paste getCachedPasteMetadata(String pasteId) {
        return pasteRedisTemplate.opsForValue().get(METADATA_PREFIX + pasteId);
    }

    public void cachePasteContent(String key, String content) {
        stringRedisTemplate.opsForValue().set(CONTENT_PREFIX + key, content, Duration.ofMinutes(15));
    }

    public String getCachedPasteContent(String key) {
        return stringRedisTemplate.opsForValue().get(CONTENT_PREFIX + key);
    }

    public void evictCachedPaste(String id) {
        stringRedisTemplate.delete(CONTENT_PREFIX + id);
        stringRedisTemplate.delete(METADATA_PREFIX + id);
    }

    public long incrementAccessCount(String pasteId) {
        String key = ACCESS_COUNT_PREFIX + pasteId;
        Long count = stringRedisTemplate.opsForValue().increment(key);

        if (count != null && count == 1L) {
            stringRedisTemplate.expire(key, 1, TimeUnit.HOURS);
        }

        return count == null ? 0L : count;
    }
}
