package com.hapnium.core.qr_code;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.codec.binary.Base64;
import com.hapnium.core.exception.HapQrCodeException;
import com.hapnium.core.qr_code.models.QrCodeRequest;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * <h1>QrCode</h1>
 * A utility class for generating QR codes with additional features such as adding logos,
 * encoding URLs, and returning QR codes as Base64 encoded images.
 * It uses the ZXing library to generate the QR codes and the Apache Commons Codec library
 * to encode the image into Base64 format.
 */
class QrCode implements QrCodeService {
    @Override
    public String getAuthenticatorUrl(String secret, String account, String issuer) {
        if((secret == null || secret.isEmpty()) || (account == null || account.isEmpty()) || (issuer == null || issuer.isEmpty())) {
            throw new HapQrCodeException(QrCodeConstant.MISSING_KEY, "You must provide a secret, account, issuer and secret");
        }

        return "otpauth://totp/"
                + URLEncoder.encode(issuer + ":" + account, StandardCharsets.UTF_8).replace("+", "%20")
                + "?secret=" + URLEncoder.encode(secret.replace(" ", "").toUpperCase(), StandardCharsets.UTF_8).replace("+", "%20")
                + "&issuer=" + URLEncoder.encode(issuer, StandardCharsets.UTF_8).replace("+", "%20");
    }

    @Override
    public String generate(QrCodeRequest param) {
        if(param == null) {
            throw new HapQrCodeException(QrCodeConstant.MISSING_PARAMETER, "You must provide the parameters for the QrCode you want to generate");
        }

        BufferedImage qrImage = getBufferedImage(param);

        if(param.getLogo() != null && !param.getLogo().isEmpty()) {
            drawLogo(param, qrImage);
        }

        try {
            // Convert the final image to a Base64 encoded string
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);
            outputStream.flush();

            return Base64.encodeBase64String(outputStream.toByteArray());
        } catch (Exception e) {
            throw new HapQrCodeException(QrCodeConstant.GENERATION_FAILED, e.getMessage(), e);
        }
    }

    /**
     * Generates a BufferedImage for the QR code using ZXing library.
     *
     * @param param The parameters to customize the QR code.
     * @return A BufferedImage representing the QR code.
     * @throws HapQrCodeException if image generation fails.
     */
    private @NotNull BufferedImage getBufferedImage(QrCodeRequest param) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(param.getUrl(), BarcodeFormat.QR_CODE, param.getWidth(), param.getHeight());

            // Convert the BitMatrix to a BufferedImage with the specified color
            return createScaledQRCode(trimWhitePadding(bitMatrix), param);
        } catch (Exception e) {
            throw new HapQrCodeException(QrCodeConstant.IMAGE_GENERATION_FAILED, e.getMessage(), e);
        }
    }

    /**
     * Trims the white padding from the QR code.
     *
     * @param matrix The BitMatrix representing the QR code.
     * @return A BitMatrix with trimmed white padding.
     */
    private @NotNull BitMatrix trimWhitePadding(@NotNull BitMatrix matrix) {
        int[] enclosingRectangle = matrix.getEnclosingRectangle();
        int w = enclosingRectangle[2];
        int h = enclosingRectangle[3];

        BitMatrix trimmedMatrix = new BitMatrix(w, h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (matrix.get(x + enclosingRectangle[0], y + enclosingRectangle[1])) {
                    trimmedMatrix.set(x, y);
                }
            }
        }

        return trimmedMatrix;
    }

    /**
     * Creates a scaled BufferedImage from the BitMatrix with optional padding.
     *
     * @param bitMatrix The BitMatrix representing the QR code.
     * @param param The parameters that customize the QR code (e.g., size, color, transparency).
     * @return A BufferedImage representing the QR code.
     */
    private @NotNull BufferedImage createScaledQRCode(@NotNull BitMatrix bitMatrix, @NotNull QrCodeRequest param) {
        // Determine padding when not transparent
        int p = param.getTransparent() ? 0 : Math.min(param.getWidth(), param.getHeight()) / 20; // 5% of the smaller dimension

        // Adjust dimensions for padding
        int w = param.getWidth() + 2 * p;
        int h = param.getHeight() + 2 * p;

        // Create the BufferedImage with adjusted dimensions
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int bgColor = param.getTransparent() ? 0x00FFFFFF : 0xFFFFFFFF; // Transparent or white background

        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();

        // Calculate the scale factors for width and height based on the original width and height
        double scaleX = (double) param.getWidth() / matrixWidth;
        double scaleY = (double) param.getHeight() / matrixHeight;

        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setComposite(AlphaComposite.Src);

        // Fill the entire image with the background color
        graphics.setColor(new Color(bgColor, true));
        graphics.fillRect(0, 0, w, h);

        // Draw the scaled QR code onto the image with the padding offset
        graphics.setColor(new Color(param.getColor(), true));
        for (int x = 0; x < matrixWidth; x++) {
            for (int y = 0; y < matrixHeight; y++) {
                if (bitMatrix.get(x, y)) {
                    int scaledX = (int) (x * scaleX) + p;
                    int scaledY = (int) (y * scaleY) + p;
                    int scaledWidth = (int) ((x + 1) * scaleX) - (int) (x * scaleX);
                    int scaledHeight = (int) ((y + 1) * scaleY) - (int) (y * scaleY);
                    graphics.fillRect(scaledX, scaledY, scaledWidth, scaledHeight);
                }
            }
        }
        graphics.dispose();

        return image;
    }

    /**
     * Draws a logo in the center of the QR code if specified.
     *
     * @param param The parameters that specify logo, color, transparency, etc.
     * @param qrImage The QR code image where the logo should be added.
     * @throws HapQrCodeException if an error occurs while adding the logo.
     */
    private void drawLogo(QrCodeRequest param, BufferedImage qrImage) {
        // Add a logo to the QR code from the URL
        try {
            BufferedImage image = ImageIO.read(new URI(param.getLogo()).toURL());
            int w = param.getWidth() / 5;
            int h = param.getHeight() / 5;

            Graphics2D graphics = qrImage.createGraphics();
            // Reduce the logo size when isTransparent is false
            if (!param.getTransparent()) {
                w = w * 2 / 3; // Reduce the logo width
                h = h * 2 / 3; // Reduce the logo height
            }

            int centerX = (qrImage.getWidth() - w) / 2;
            int centerY = (qrImage.getHeight() - h) / 2;

            // If param.getTransparent() is false, add a square background with border radius
            if (!param.getTransparent()) {
                int bgSize = Math.max(w, h) + 10; // Slightly larger than the reduced logo
                int bgX = (qrImage.getWidth() - bgSize) / 2;
                int bgY = (qrImage.getHeight() - bgSize) / 2;
                int br = 15; // Border radius for rounded corners

                // Draw the square background with rounded corners
                graphics.setColor(new Color(0x0E1218));
                graphics.fillRoundRect(bgX, bgY, bgSize, bgSize, br, br);
            }

            // Draw the logo on top
            graphics.setComposite(AlphaComposite.SrcOver);
            graphics.drawImage(image, centerX, centerY, w, h, null);
            graphics.dispose();
        } catch (Exception e) {
            throw new HapQrCodeException(QrCodeConstant.LOGO_ADDITION_FAILED, e.getMessage(), e);
        }
    }
}