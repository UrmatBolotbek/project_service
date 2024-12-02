package faang.school.projectservice.service.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class ImageService {

    private static final int SQUARE_IMAGE_SIZE = 1080;
    private static final int RECTANGLE_IMAGE_WIDTH = 1080;
    private static final int RECTANGLE_IMAGE_HEIGHT = 566;

    public InputStream processImage(InputStream inputStream, boolean isSquare) throws IOException {
        log.info("Reading and processing image...");
        BufferedImage originalImage = ImageIO.read(inputStream);

        if (originalImage == null) {
            throw new IOException("Failed to read input stream into an image.");}

        int targetWidth = isSquare ? SQUARE_IMAGE_SIZE : RECTANGLE_IMAGE_WIDTH;
        int targetHeight = isSquare ? SQUARE_IMAGE_SIZE : RECTANGLE_IMAGE_HEIGHT;

        log.info("Resizing image to width: {}, height: {}", targetWidth, targetHeight);
        BufferedImage resizedImage = resizeImage(originalImage, targetWidth, targetHeight);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpeg", outputStream);
        log.info("Image successfully resized and written to output stream.");
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        log.info("Creating resized image...");
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        log.info("Resized image created.");
        return resizedImage;
    }
}