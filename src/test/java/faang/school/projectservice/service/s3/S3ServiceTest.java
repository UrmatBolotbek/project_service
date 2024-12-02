package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
    }

    @Test
    void uploadFileSuccess() throws IOException {
        String fileName = "test-file.txt";
        String contentType = "text/plain";
        String expectedUrl = "https://s3.amazonaws.com/test-bucket/test-file.txt";

        byte[] fileContent = "Test file content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileContent);

        when(amazonS3.getUrl("test-bucket", fileName)).thenReturn(new URL(expectedUrl));

        String resultUrl = s3Service.uploadFile(fileName, inputStream, contentType);

        assertEquals(expectedUrl, resultUrl);

        ArgumentCaptor<ObjectMetadata> metadataCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);
        verify(amazonS3, times(1)).putObject(
                eq("test-bucket"),
                eq(fileName),
                any(ByteArrayInputStream.class),
                metadataCaptor.capture()
        );

        ObjectMetadata capturedMetadata = metadataCaptor.getValue();
        assertEquals(contentType, capturedMetadata.getContentType());
        assertEquals(fileContent.length, capturedMetadata.getContentLength());
    }

    @Test
    void uploadFileShouldThrowException() throws IOException {
        String fileName = "test-file.txt";
        InputStream failingInputStream = mock(InputStream.class);

        when(failingInputStream.readAllBytes()).thenThrow(new IOException("Stream error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                s3Service.uploadFile(fileName, failingInputStream, "text/plain")
        );

        assertEquals("Failed to upload file", exception.getMessage());
        verifyNoInteractions(amazonS3);
    }
}