package faang.school.projectservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse() {
        timeStamp = LocalDateTime.now();
    }
}
