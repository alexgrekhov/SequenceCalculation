package org.grekhov.sequencecalculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SequenceCalculationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SequenceCalculationApplication.class, args);
    }

}
