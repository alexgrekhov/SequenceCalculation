package org.grekhov.sequencecalculation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class HistogramService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final NumberService numberService;
    private static final long CACHE_TTL = 10; // минуты

    public HistogramService(RedisTemplate<String, Object> redisTemplate,
                            ObjectMapper objectMapper,
                            NumberService numberService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.numberService = numberService;
    }

    public Map<String, Integer> getHistogram(String datasetId, int buckets) throws IOException {
        String key = "histogram:" + datasetId + ":" + buckets;

        // --- Проверка кэша ---
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<>() {
            });
        }

        // --- Берём гистограмму из NumberService ---
        Map<String, Integer> histogram = numberService.buildHistogram(datasetId, buckets);

        // --- Сохраняем в Redis ---
        redisTemplate.opsForValue().set(key, histogram, CACHE_TTL, TimeUnit.MINUTES);

        return histogram;
    }
}
