package faang.school.projectservice.validator.cover;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoverValidator {

    public void validateFileSize(long fileSize) {
        if (fileSize > 5 * 1024 * 1024) {
            log.error("File size exceeds the 5MB limit: {} bytes", fileSize);
            throw new IllegalArgumentException("File size exceeds the 5MB limit.");
        }
    }
}