package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import dev.alabbad.utils.Transformer;

public class TestTransformer {
    // ===============================================
    // --> streamToBytes()
    // ===============================================
    @Test
    public void streamToBytesTest() throws IOException {
        byte[] dataBytes = "Hello World!".getBytes();

        InputStream inputStream = new ByteArrayInputStream(dataBytes);

        byte[] result = Transformer.streamToBytes(inputStream);

        assertArrayEquals(dataBytes, result);
    }

    @Test
    public void streamToBytesTest_EmptyStream() throws IOException {
        byte[] dataBytes = new byte[0];

        InputStream inputStream = new ByteArrayInputStream(dataBytes);

        byte[] result = Transformer.streamToBytes(inputStream);

        assertArrayEquals(dataBytes, result);
    }

    // ===============================================
    // --> bytesToBase64()
    // ===============================================
    @Test
    public void bytesToBase64Test() throws IOException {
        byte[] dataBytes = "Hello World".getBytes();

        String base64Value = Transformer.bytesToBase64(dataBytes);

        assertEquals("SGVsbG8gV29ybGQ=", base64Value);
    }

    // ===============================================
    // --> base64ToByteArrayInputStream()
    // ===============================================
    @Test
    public void base64ToByteArrayInputStreamTest() throws IOException {
        String base64Value = "SGVsbG8gV29ybGQ=";

        ByteArrayInputStream stream = Transformer.base64ToByteArrayInputStream(base64Value);

        byte[] buffer = new byte[1024];
        int bytesRead = stream.read(buffer);
        String result = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);

        assertEquals("Hello World", result);
    }
}
