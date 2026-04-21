package com.sellerinsight.importjob.application;

import com.sellerinsight.common.error.BusinessException;
import com.sellerinsight.common.error.ErrorCode;
import com.sellerinsight.importjob.config.CsvImportProperties;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class CsvFileValidator {

    private final CsvImportProperties csvImportProperties;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.CSV_IMPORT_INVALID_FILE);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null
                || originalFilename.isBlank()
                || !originalFilename.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new BusinessException(ErrorCode.CSV_IMPORT_INVALID_FILE_TYPE);
        }

        if (file.getSize() > csvImportProperties.maxFileSize().toBytes()) {
            throw new BusinessException(ErrorCode.CSV_IMPORT_FILE_TOO_LARGE);
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return;
        }

        String normalizedContentType = contentType.toLowerCase(Locale.ROOT)
                .split(";")[0]
                .trim();

        boolean allowed = csvImportProperties.allowedContentTypes().stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(normalizedContentType::equals);

        if (!allowed) {
            throw new BusinessException(ErrorCode.CSV_IMPORT_INVALID_FILE_TYPE);
        }
    }
}
