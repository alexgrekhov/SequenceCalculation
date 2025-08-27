package org.grekhov.sequencecalculation.controller;

import org.grekhov.sequencecalculation.service.HistogramService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class UiController {

    private final HistogramService histogramService;

    public UiController(HistogramService histogramService) {
        this.histogramService = histogramService;
    }

    @GetMapping("/")
    public String startPage() {
        return "index";
    }

    @GetMapping("/params")
    public String paramsPage() {
        return "params";
    }

    @PostMapping("/params")
    public String handleParams(@RequestParam String datasetId,
                               @RequestParam(required = false, defaultValue = "10") int bucket,
                               Model model) throws IOException {

        // ✅ Используем метод getHistogram
        Map<String, Integer> histogram = histogramService.getHistogram(datasetId, bucket);

        model.addAttribute("labels", new ArrayList<>(histogram.keySet()));
        model.addAttribute("data", new ArrayList<>(histogram.values()));

        return "histogram";
    }

    @GetMapping("/cache")
    public String cachePage(Model model) {
        // Пример: берем все ключи histogram:* из Redis
        model.addAttribute("cached", List.of("dataset1", "dataset2"));
        return "cache";
    }
}

