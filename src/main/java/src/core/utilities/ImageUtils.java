package src.core.utilities;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@UtilityClass
public class ImageUtils {

    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

    /**
     * Compresses the given byte array.
     *
     * @param data The byte array to compress
     * @return The compressed byte array
     */
    public static byte[] compressImage(byte[] data) {
        // Use the Deflater class for compression
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        // Create a ByteArrayOutputStream to hold the compressed data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            // Write the compressed data to the buffer
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (IOException ignored) {
            // Close error is ignored
        }
        return outputStream.toByteArray();
    }

    /**
     * Decompresses the given byte array.
     *
     * @param data The byte array to decompress
     * @return The decompressed byte array
     */
    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            byte[] buffer = new byte[4 * 1024];

            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            return outputStream.toByteArray();
        } catch (IOException | DataFormatException e) {
            log.error("Image decompression failed", e);
            throw new RuntimeException("Image decompression failed", e);
        } finally {
            inflater.end();
        }
    }


    /**
     * Resizes the image in the given byte array to the specified dimensions.
     *
     * @param data      The byte array of the image to resize
     * @param newWidth  New width
     * @param newHeight New height
     */
    public static byte[] resizeImage(byte[] data, int newWidth, int newHeight) throws IOException {
        // Decompress the original image
        byte[] decompressedData = decompressImage(compressImage(data));

        // Convert the decompressed data to a BufferedImage
        assert decompressedData != null;
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(decompressedData));

        // Create a new BufferedImage with the specified dimensions
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // Draw the original image into the new image at the specified size
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        // Compress the resized image and return the result
        return compressImage(imageToByteArray(resizedImage));
    }

    /**
     * Converts a BufferedImage to a byte array.
     *
     * @param image The BufferedImage to convert
     * @return The BufferedImage converted to a byte array
     * @throws IOException Thrown in case of a conversion error
     */
    private static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }
}
