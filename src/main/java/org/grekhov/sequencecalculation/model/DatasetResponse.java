package org.grekhov.sequencecalculation.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DatasetResponse {
    private String datasetId;

    public DatasetResponse(String datasetId) {
        this.datasetId = datasetId;
    }


}