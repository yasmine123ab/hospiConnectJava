package org.hospiconnect.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating QR codes
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code as a JavaFX Image from the provided content
     * 
     * @param content The content to encode in the QR code
     * @param size The size of the QR code in pixels
     * @return A JavaFX Image containing the QR code
     * @throws WriterException If there's an error generating the QR code
     * @throws IOException If there's an error creating the image
     */
    public static Image generateQRCode(String content, int size) throws WriterException, IOException {
        // Create QR code writer
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        // Set QR code hints (error correction, margin, etc.)
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High error correction
        hints.put(EncodeHintType.MARGIN, 2); // Margin size
        

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] imageData = outputStream.toByteArray();
        

        return new Image(new ByteArrayInputStream(imageData));
    }
}