/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.io;

import static com.google.zxing.BarcodeFormat.QR_CODE;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.imageio.ImageIO;
import static org.cmdbuild.utils.io.CmImageUtils.getImageFormat;
import static org.cmdbuild.utils.io.CmImageUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

/**
 *
 * @author ataboga
 */
public class CmQuickResponseCodeUtils {

    public static byte[] generateQuickResponseCode(String text, int qrCodeheight, int qrCodewidth) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(
                    new String(text.getBytes(UTF_8), UTF_8),
                    QR_CODE,
                    qrCodewidth,
                    qrCodeheight,
                    map(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L)
            );
            BufferedImage bufferedImage = toBufferedImage(matrix);
            return toByteArray(bufferedImage, "png");
        } catch (WriterException ex) {
            throw runtime(ex, "error generating qrcode");
        }
    }

    public static String readQuickResponseCodeCropped(byte[] imageBytes, int x, int y, int w, int h) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            BufferedImage subImgage = image.getSubimage(x, y, w, h);
            return readQuickResponseCode(toByteArray(subImgage, firstNotBlank(getImageFormat(imageBytes), "png")));
        } catch (Exception ex) {
            throw runtime(ex, "error cropping image");
        }
    }

    public static String readQuickResponseCode(byte[] imageBytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            BufferedImage grayScaleImage = convertToGrayScale(image);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new CmBufferedImageLuminanceSource(grayScaleImage)));
            return new MultiFormatReader().decode(binaryBitmap).getText();
        } catch (Exception ex) {
            throw runtime(ex, "error reading qrcode");
        }
    }

    private static BufferedImage convertToGrayScale(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        int[] rowPixels = new int[width];
        BitArray row = new BitArray(width);
        for (int y = 0; y < height; y++) {
            row = matrix.getRow(y, row);
            for (int x = 0; x < width; x++) {
                rowPixels[x] = row.get(x) ? 0xFF000000 : 0xFFFFFFFF;
            }
            image.setRGB(0, y, width, 1, rowPixels, 0, width);
        }
        return image;
    }

    private static class CmBufferedImageLuminanceSource extends LuminanceSource {

        private final BufferedImage image;

        public CmBufferedImageLuminanceSource(BufferedImage image) {
            super(image.getWidth(), image.getHeight());
            this.image = image;
        }

        @Override
        public byte[] getRow(int y, byte[] row) {
            if (row == null || row.length < getWidth()) {
                row = new byte[getWidth()];
            }
            image.getRaster().getDataElements(0, y, getWidth(), 1, row);
            return row;
        }

        @Override
        public byte[] getMatrix() {
            byte[] matrix = new byte[getWidth() * getHeight()];
            image.getRaster().getDataElements(0, 0, getWidth(), getHeight(), matrix);
            return matrix;
        }
    }
}
