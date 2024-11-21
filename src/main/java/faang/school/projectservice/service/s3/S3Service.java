package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(String fileName, InputStream inputStream, String contentType) {
        log.info("Uploading file '{}' to bucket '{}'", fileName, bucketName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        amazonS3.putObject(bucketName, fileName, inputStream, metadata);
        String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
        log.info("File '{}' successfully uploaded to '{}'", fileName, fileUrl);
        return fileUrl;
    }
}