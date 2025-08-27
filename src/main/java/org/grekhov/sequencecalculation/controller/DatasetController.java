package org.grekhov.sequencecalculation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Getter;
import lombok.Setter;
import org.grekhov.sequencecalculation.service.CachedNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dataset")
public class DatasetController {

    @Autowired
    private CachedNumberService cachedNumberService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать dataset",
            description = "Загружает файл с числами или генерирует dataset из 10 млн чисел",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dataset создан",
                            content = @Content(schema = @Schema(implementation = DatasetIdResponse.class)))
            })
    public ResponseEntity<DatasetIdResponse> createDataset(
            @Parameter(description = "Файл с числами (опционально)") @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        String datasetId = cachedNumberService.createDataset(file);
        return ResponseEntity.ok(new DatasetIdResponse(datasetId));
    }

    @GetMapping("/{id}/max")
    @Operation(summary = "Максимальное значение", description = "Возвращает максимальное число в dataset")
    public ResponseEntity<Integer> getMax(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findMaxValue(id));
    }

    @GetMapping("/{id}/min")
    @Operation(summary = "Минимальное значение", description = "Возвращает минимальное число в dataset")
    public ResponseEntity<Integer> getMin(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findMinValue(id));
    }

    @GetMapping("/{id}/average")
    @Operation(summary = "Среднее значение", description = "Возвращает среднее арифметическое чисел в dataset")
    public ResponseEntity<Double> getAverage(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findAverageValue(id));
    }

    @GetMapping("/{id}/median")
    @Operation(summary = "Медиана", description = "Возвращает медиану чисел в dataset")
    public ResponseEntity<Integer> getMedian(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findMedianValueOptimized(id));
    }

    @GetMapping("/{id}/histogram")
    @Operation(summary = "Гистограмма", description = "Возвращает распределение чисел по бакетам")
    public ResponseEntity<Map<String, Integer>> getHistogram(
            @PathVariable("id") String id,
            @Parameter(description = "Количество бакетов") @RequestParam("buckets") int buckets
    ) throws IOException {
        return ResponseEntity.ok(cachedNumberService.buildHistogram(id, buckets));
    }

    @GetMapping("/{id}/longest-increasing")
    @Operation(summary = "Длиннейшая возрастающая последовательность",
            description = "Возвращает список чисел самой длинной возрастающей последовательности")
    public ResponseEntity<List<Integer>> getLongestIncreasing(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findLongestIncreasingSequence(id));
    }

    @GetMapping("/{id}/longest-decreasing")
    @Operation(summary = "Длиннейшая убывающая последовательность",
            description = "Возвращает список чисел самой длинной убывающей последовательности")
    public ResponseEntity<List<Integer>> getLongestDecreasing(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(cachedNumberService.findLongestDecreasingSequence(id));
    }

    // Внутренний класс для JSON ответа при создании dataset
    @Setter
    @Getter
    public static class DatasetIdResponse {
        private String datasetId;

        public DatasetIdResponse(String datasetId) {
            this.datasetId = datasetId;
        }

    }
}



