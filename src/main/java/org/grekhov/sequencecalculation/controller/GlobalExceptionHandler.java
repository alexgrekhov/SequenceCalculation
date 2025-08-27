package org.grekhov.sequencecalculation.controller;

import org.grekhov.sequencecalculation.exception.DatasetNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatasetNotFoundException.class)
    public ResponseEntity<String> handleDatasetNotFound(DatasetNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIO(IOException ex) {
        return ResponseEntity.status(500).body("Ошибка при обработке файла: " + ex.getMessage());
    }
}

