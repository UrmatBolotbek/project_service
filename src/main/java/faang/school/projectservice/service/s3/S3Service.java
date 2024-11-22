package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(String fileName, InputStream inputStream, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            byte[] buffer = inputStream.readAllBytes();
            metadata.setContentLength(buffer.length);

            amazonS3.putObject(bucketName, fileName, new ByteArrayInputStream(buffer), metadata);

            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
            log.info("File '{}' successfully uploaded to '{}'", fileName, fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("Error reading input stream for file '{}': {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}