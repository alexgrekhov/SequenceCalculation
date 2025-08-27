package org.grekhov.sequencecalculation.service;

import lombok.Getter;
import org.grekhov.sequencecalculation.exception.DatasetNotFoundException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Getter
@Service
public class NumberServiceImpl implements NumberService {

    private final Path datasetDir = Paths.get("datasets");

    public NumberServiceImpl() throws IOException {
        if (!Files.exists(datasetDir)) {
            Files.createDirectories(datasetDir);
        }
    }

    // Проверка файла
    private Path getDatasetPath(String datasetId) {
        Path file = datasetDir.resolve(datasetId + ".txt");
        if (!Files.exists(file)) {
            throw new DatasetNotFoundException(datasetId);
        }
        return file;
    }

    @Override
    public int findMaxValue(String datasetId) throws IOException {
        Path file = getDatasetPath(datasetId);
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            int max = Integer.MIN_VALUE;
            String line;
            while ((line = reader.readLine()) != null) {
                int num = Integer.parseInt(line);
                if (num > max) max = num;
            }
            return max;
        } catch (NumberFormatException e) {
            throw new IOException("Ошибка разбора числа в файле", e);
        }
    }

    @Override
    public int findMinValue(String datasetId) throws IOException {
        Path file = getDatasetPath(datasetId);
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            int min = Integer.MAX_VALUE;
            String line;
            while ((line = reader.readLine()) != null) {
                int num = Integer.parseInt(line);
                if (num < min) min = num;
            }
            return min;
        } catch (NumberFormatException e) {
            throw new IOException("Ошибка разбора числа в файле", e);
        }
    }

    @Override
    public double findAverageValue(String datasetId) throws IOException {
        Path file = getDatasetPath(datasetId);
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            long sum = 0;
            long count = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                sum += Integer.parseInt(line);
                count++;
            }
            return count > 0 ? (double) sum / count : 0;
        } catch (NumberFormatException e) {
            throw new IOException("Ошибка разбора числа в файле", e);
        }
    }

    @Override
    public int findMedianValueOptimized(String datasetId) throws IOException {
        Path file = getDatasetPath(datasetId);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        long count = 0;

        // 1-й проход: min, max, количество
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int num = Integer.parseInt(line);
                if (num < min) min = num;
                if (num > max) max = num;
                count++;
            }
        }

        // 2-й проход: подсчет через бакеты
        int bucketCount = 1_000_000;
        long[] buckets = new long[bucketCount];
        double bucketSize = (double)(max - min + 1) / bucketCount;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int num = Integer.parseInt(line);
                int idx = (int)((num - min) / bucketSize);
                if (idx >= bucketCount) idx = bucketCount - 1;
                buckets[idx]++;
            }
        }

        long mid = (count + 1) / 2;
        long cumulative = 0;
        int medianBucket = 0;
        for (int i = 0; i < bucketCount; i++) {
            cumulative += buckets[i];
            if (cumulative >= mid) {
                medianBucket = i;
                break;
            }
        }

        int bucketStart = min + (int)(medianBucket * bucketSize);
        int bucketEnd = min + (int)((medianBucket + 1) * bucketSize) - 1;
        return (bucketStart + bucketEnd) / 2;
    }

//    @Override
//    public Map<String, Integer> buildHistogram(String datasetId, int buckets) throws IOException {
//        Path file = getDatasetPath(datasetId);
//
//        // 1-й проход: min и max
//        int min = Integer.MAX_VALUE;
//        int max = Integer.MIN_VALUE;
//        try (BufferedReader reader = Files.newBufferedReader(file)) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                int num = Integer.parseInt(line);
//                if (num < min) min = num;
//                if (num > max) max = num;
//            }
//        }
//
//        double bucketSize = (double)(max - min + 1) / buckets;
//        int[] counts = new int[buckets];
//
//        try (BufferedReader reader = Files.newBufferedReader(file)) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                int num = Integer.parseInt(line);
//                int idx = (int)((num - min) / bucketSize);
//                if (idx >= buckets) idx = buckets - 1;
//                counts[idx]++;
//            }
//        }
//
//        Map<String, Integer> histogram = new LinkedHashMap<>();
//        for (int i = 0; i < buckets; i++) {
//            int bucketStart = min + (int)(i * bucketSize);
//            int bucketEnd = min + (int)((i + 1) * bucketSize) - 1;
//            histogram.put(bucketStart + "-" + bucketEnd, counts[i]);
//        }
//        return histogram;
//    }

    @Override
    public Map<String, Integer> buildHistogram(String datasetId, int buckets) throws IOException {
        Path file = getDatasetPath(datasetId);

        // Используем TreeMap для хранения бакетов по границам
        Map<Integer, Integer> tempBuckets = new TreeMap<>();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int totalNumbers = 0;

        // Первый проход: вычисляем min, max и заполняем временные бакеты
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int num = Integer.parseInt(line);
                min = Math.min(min, num);
                max = Math.max(max, num);
                tempBuckets.put(num, tempBuckets.getOrDefault(num, 0) + 1);
                totalNumbers++;
            }
        }

        Map<String, Integer> histogram = new LinkedHashMap<>();
        if (totalNumbers == 0) return histogram; // пустой файл
        if (min == max) {
            histogram.put(min + "-" + max, totalNumbers);
            return histogram;
        }

        // Вычисляем диапазон одного бакета
        double bucketRange = (double)(max - min + 1) / buckets;
        int[] counts = new int[buckets];

        // Распределяем временные бакеты по финальным
        for (Map.Entry<Integer, Integer> entry : tempBuckets.entrySet()) {
            int num = entry.getKey();
            int count = entry.getValue();
            int idx = (int) Math.floor((num - min) / bucketRange);
            if (idx >= buckets) idx = buckets - 1;
            counts[idx] += count;
        }

        // Формируем ключи бакетов
        for (int i = 0; i < buckets; i++) {
            int bucketStart = min + (int) Math.floor(i * bucketRange);
            int bucketEnd = min + (int) Math.floor((i + 1) * bucketRange) - 1;
            histogram.put(bucketStart + "-" + bucketEnd, counts[i]);
        }

        return histogram;
    }


    @Override
    public List<Integer> findLongestIncreasingSequence(String datasetId) throws IOException {
        Path file = getDatasetPath(datasetId);
        List<Integer> longest = new ArrayList<>();
        List<Integer> sequence = new ArrayList<>();
        int prev = Integer.MIN_VALUE;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int curr = Integer.parseInt(line);
                if (curr > prev) {
                    sequence.add(curr);
                } else {
                    if (sequence.size() > longest.size()) longest = new ArrayList<>(sequence);
                    sequence.clear();
                    sequence.add(curr);
                }
                prev = curr;
            }
        }
        if (sequence.size() > longest.size()) longest = sequence;
        return longest;
    }

    @Override
    public List<Integer> findLongestDecreasingSequence(String datasetId) throws IOException {
        Path file = getDatasetPath(datasetId);
        List<Integer> longest = new ArrayList<>();
        List<Integer> sequence = new ArrayList<>();
        int prev = Integer.MAX_VALUE;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int curr = Integer.parseInt(line);
                if (curr < prev) {
                    sequence.add(curr);
                } else {
                    if (sequence.size() > longest.size()) longest = new ArrayList<>(sequence);
                    sequence.clear();
                    sequence.add(curr);
                }
                prev = curr;
            }
        }
        if (sequence.size() > longest.size()) longest = sequence;
        return longest;
    }


}
