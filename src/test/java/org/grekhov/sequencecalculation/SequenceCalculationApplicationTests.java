package org.grekhov.sequencecalculation;

import org.grekhov.sequencecalculation.service.NumberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceCalculationApplicationTests {

    private NumberServiceImpl numberService;
    private Path datasetDir;

    @BeforeEach
    void setUp() throws IOException {
        numberService = new NumberServiceImpl();
        datasetDir = numberService.getDatasetDir(); // Получаем папку, где NumberService ищет файлы
        if (!Files.exists(datasetDir)) {
            Files.createDirectories(datasetDir);
        }
    }

    private String createDataset(String content) throws IOException {
        String datasetId = UUID.randomUUID().toString();
        Path filePath = datasetDir.resolve(datasetId + ".txt");
        Files.writeString(filePath, content);
        return datasetId;
    }

    @Test
    void testFindMaxValue() throws Exception {
        String datasetId = createDataset("10\n20\n50\n5\n49999996\n-1\n0");
        int result = numberService.findMaxValue(datasetId);
        assertEquals(49_999_996, result);
    }

    @Test
    void testFindMinValue() throws Exception {
        String datasetId = createDataset("10\n20\n-999\n5\n-49999971\n0");
        int result = numberService.findMinValue(datasetId);
        assertEquals(-49_999_971, result);
    }

    @Test
    void testFindAverageValue() throws Exception {
        String datasetId = createDataset("10\n20\n30\n40");
        double result = numberService.findAverageValue(datasetId);
        assertEquals(25.0, result, 0.0001);
    }


    @Test
    void testFindIncreasingSequence() throws Exception {
        String datasetId = createDataset("1\n2\n3\n2\n3\n4\n5\n1");
        List<Integer> result = numberService.findLongestIncreasingSequence(datasetId);
        assertEquals(List.of(2, 3, 4, 5), result);
    }

    @Test
    void testFindDecreasingSequence() throws Exception {
        String datasetId = createDataset("10\n9\n8\n11\n10\n5\n4\n3\n2\n1");
        List<Integer> result = numberService.findLongestDecreasingSequence(datasetId);
        assertEquals(List.of(11, 10, 5, 4, 3, 2, 1), result);
    }
}
