package com.sellerinsight.importjob.application;

import lombok.Getter;

@Getter
public class CsvImportProcessingException extends RuntimeException {

    private final int totalRowCount;
    private final int successRowCount;
    private final int failedRowCount;

    public CsvImportProcessingException(
            int totalRowCount,
            int successRowCount,
            int failedRowCount,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.totalRowCount = totalRowCount;
        this.successRowCount = successRowCount;
        this.failedRowCount = failedRowCount;
    }
}
