package org.grekhov.sequencecalculation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class CachedNumberServiceImpl implements CachedNumberService {

    private final NumberService numberService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final long CACHE_TTL = 10; // минуты


    @Autowired
    public CachedNumberServiceImpl(NumberService numberService,
                                   RedisTemplate<String, Object> redisTemplate,
                                   ObjectMapper objectMapper) {
        this.numberService = numberService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private String buildKey(String datasetId, String operation) {
        return "dataset:" + datasetId + ":" + operation;
    }

    @Override
    public int findMaxValue(String datasetId) throws IOException {
        String key = buildKey(datasetId, "max");
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof Integer value) return value;

        int result = numberService.findMaxValue(datasetId);
        redisTemplate.opsForValue().set(key, result, CACHE_TTL, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public int findMinValue(String datasetId) throws IOException {
        String key = buildKey(datasetId, "min");
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof Integer value) return value;

        int result = numberService.findMinValue(datasetId);
        redisTemplate.opsForValue().set(key, result, CACHE_TTL, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public double findAverageValue(String datasetId) throws IOException {
        String key = buildKey(datasetId, "average");
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof Double value) return value;

        double result = numberService.findAverageValue(datasetId);
        redisTemplate.opsForValue().set(key, result, CACHE_TTL, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public int findMedianValueOptimized(String datasetId) throws IOException {
        String key = buildKey(datasetId, "median");
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof Integer value) return value;

        int result = numberService.findMedianValueOptimized(datasetId);
        redisTemplate.opsForValue().set(key, result, CACHE_TTL, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public Map<String, Integer> buildHistogram(String datasetId, int buckets) throws IOException {
        String key = buildKey(datasetId, "histogram:" + buckets);
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<>() {
            });
        }

        Map<String, Integer> result = numberService.buildHistogram(datasetId, buckets);
        redisTemplate.opsForValue().set(key, result, CACHE_TTL, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public List<Integer> findLongestIncreasingSequence(String datasetId) throws IOException {
        String key = buildKey(datasetId, "longest-increasing");
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<>() {
            });
        }

        List<Integer> result = numberService.findLongestIncreasingSequence(datasetId);
        redisTemplate.opsForValue().set(key, result, CACHE_TTL, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public List<Integer> findLongestDecreasingSequence(String datasetId) throws IOException {
        String key = buildKey(datasetId, "longest-decreasing");
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<>() {
            });
        }

        List<Integer> result = numberService.findLongestDecreasingSequence(datasetId);
        redisTemplate.opsForValue().set(key, result, CACHE_TTL, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public String createDataset(MultipartFile file) throws IOException {
        // Генерация уникального datasetId
        String datasetId = String.valueOf(System.currentTimeMillis()); // или UUID.randomUUID().toString()
        String datasetPath = "datasets/" + datasetId + ".txt";

        // Создаем директорию, если не существует
        Path dir = Paths.get("datasets");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path path = Paths.get(datasetPath);

        if (file != null && !file.isEmpty()) {
            // Сохраняем загруженный файл
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, path);
            }
        } else {
            // Генерируем файл с 10 млн чисел
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                Random random = new Random();
                for (int i = 0; i < 10_000_000; i++) {
                    int number = random.nextInt(100_000_001) - 50_000_000;
                    writer.write(number + "\n");
                }
            }
        }

        return datasetId;
    }

}
