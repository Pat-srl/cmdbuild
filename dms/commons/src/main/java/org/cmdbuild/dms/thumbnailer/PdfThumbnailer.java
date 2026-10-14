/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.thumbnailer;

import jakarta.activation.DataHandler;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import static org.cmdbuild.utils.io.CmImageUtils.resizeImage;
import static org.cmdbuild.utils.io.CmImageUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class PdfThumbnailer extends AbstractThumbnailer {

    public PdfThumbnailer() {
        super();
    }

    public PdfThumbnailer(int thumbHeight, int thumbWidth) {
        super(thumbHeight, thumbWidth);
    }

    @Override
    public Optional<DataHandler> generateThumbnail(InputStream input) throws IOException {
        try (PDDocument document = getDocument(input)) {
            BufferedImage tmpImage = writeImageFirstPage(document);

            byte[] resizedImage = toByteArray(tmpImage, "png");
            if (tmpImage.getHeight() != thumbHeight) {
                resizedImage = resizeImage(resizedImage, thumbWidth, thumbHeight);
            }
            return Optional.ofNullable(toDataHandler(resizedImage));
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public List<String> getAcceptedMIMETypes() {
        return list("application/pdf");
    }

    private PDDocument getDocument(InputStream input) throws IOException {
        return Loader.loadPDF(input.readAllBytes());
    }

    private BufferedImage writeImageFirstPage(PDDocument document) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        return pdfRenderer.renderImageWithDPI(0, 72, ImageType.RGB);
    }

}
