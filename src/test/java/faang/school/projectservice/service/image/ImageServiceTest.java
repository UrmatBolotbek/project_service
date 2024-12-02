package faang.school.projectservice.service.image;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    @InjectMocks
    private ImageService imageService;

    @Test
    void processImageThrowIOException() {

        InputStream invalidInputStream = new ByteArrayInputStream(new byte[0]);

        IOException exception = assertThrows(IOException.class, () ->
                imageService.processImage(invalidInputStream, true)
        );

        assertEquals("Failed to read input stream into an image.", exception.getMessage());
    }
}