package org.grekhov.sequencecalculation.service;

import org.grekhov.sequencecalculation.model.DatasetResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CachedNumberService {
    int findMaxValue(String datasetId) throws IOException;

    int findMinValue(String datasetId) throws IOException;

    double findAverageValue(String datasetId) throws IOException;

    int findMedianValueOptimized(String datasetId) throws IOException;

    Map<String, Integer> buildHistogram(String datasetId, int buckets) throws IOException;

    List<Integer> findLongestIncreasingSequence(String datasetId) throws IOException;

    List<Integer> findLongestDecreasingSequence(String datasetId) throws IOException;

    String createDataset(MultipartFile file) throws IOException;
}
