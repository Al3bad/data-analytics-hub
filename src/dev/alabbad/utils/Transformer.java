package dev.alabbad.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * A collection of methods for data transformation
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class Transformer {
    /**
     * Convert input stream to bytes
     *
     * @param stream input stream to be converted
     * @return bytes array
     * @throws IOException
     */
    public static byte[] streamToBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }

    /**
     * Convert bytes to base64 string
     *
     * @param bytes bytes to be converted
     * @return base64 representation of the bytes
     */
    public static String bytesToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * @param base64Data
     * @return
     */
    public static ByteArrayInputStream base64ToByteArrayInputStream(String base64Data) {
        return new ByteArrayInputStream(Base64.getDecoder().decode(base64Data));
    }
}
