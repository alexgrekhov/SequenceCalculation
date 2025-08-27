package org.grekhov.sequencecalculation.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerateDatasetFile {

    public static void generateRandomFile(Path filePath, int size) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (int i = 0; i < size; i++) {
                int number = -50_000_000 + (int) (Math.random() * 100_000_000);
                writer.write(number + "\n");
            }
        }
    }
}
