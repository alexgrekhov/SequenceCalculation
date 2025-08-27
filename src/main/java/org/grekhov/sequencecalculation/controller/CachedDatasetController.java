package org.grekhov.sequencecalculation.controller;

import org.grekhov.sequencecalculation.service.CachedNumberService;
import org.grekhov.sequencecalculation.util.GenerateDatasetFile;
import org.grekhov.sequencecalculation.model.DatasetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cached-dataset")
public class CachedDatasetController {

    @Autowired
    private CachedNumberService cachedNumberService;

    private final Path datasetDir = Path.of("datasets");

    public CachedDatasetController() throws IOException {
        if (!Files.exists(datasetDir)) {
            Files.createDirectories(datasetDir);
        }
    }

    // POST /cached-dataset — загрузка или генерация файла
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DatasetResponse> uploadDataset(@RequestParam(value = "file", required = false)
                                                         org.springframework.web.multipart.MultipartFile file) throws IOException {
        String datasetId = UUID.randomUUID().toString();
        Path filePath = datasetDir.resolve(datasetId + ".txt");

        if (file != null) {
            try (var in = file.getInputStream(); var out = Files.newOutputStream(filePath)) {
                in.transferTo(out);
            }
        } else {
            GenerateDatasetFile.generateRandomFile(filePath, 10_000_000);
        }

        return ResponseEntity.ok(new DatasetResponse(datasetId));
    }

    @GetMapping("/{id}/max")
    public ResponseEntity<Integer> getMax(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findMaxValue(id));
    }

    @GetMapping("/{id}/min")
    public ResponseEntity<Integer> getMin(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findMinValue(id));
    }

    @GetMapping("/{id}/average")
    public ResponseEntity<Double> getAverage(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findAverageValue(id));
    }

    @GetMapping("/{id}/median")
    public ResponseEntity<Integer> getMedian(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findMedianValueOptimized(id));
    }

    @GetMapping("/{id}/histogram")
    public ResponseEntity<Map<String, Integer>> getHistogram(@PathVariable("id") String id,
                                                             @RequestParam("buckets") int buckets) throws IOException {
        return ResponseEntity.ok(cachedNumberService.buildHistogram(id, buckets));
    }

    @GetMapping("/{id}/longest-increasing")
    public ResponseEntity<List<Integer>> getLongestIncreasing(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findLongestIncreasingSequence(id));
    }

    @GetMapping("/{id}/longest-decreasing")
    public ResponseEntity<List<Integer>> getLongestDecreasing(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findLongestDecreasingSequence(id));
    }
}
