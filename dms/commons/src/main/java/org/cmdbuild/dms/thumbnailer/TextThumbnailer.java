/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.thumbnailer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.activation.DataHandler;
import static org.cmdbuild.utils.io.CmImageUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class TextThumbnailer extends AbstractThumbnailer {

    @Override
    public Optional<DataHandler> generateThumbnail(InputStream input) throws IOException {
        String text = readToString(input);

        BufferedImage img = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = img.createGraphics();

        Font font = new Font("Arial", Font.PLAIN, 11);
        graphics.setFont(font);

        graphics.dispose();

        img = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);
        graphics = img.createGraphics();
        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, thumbWidth, thumbHeight);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        graphics.setFont(font);
        FontMetrics fm = graphics.getFontMetrics();
        graphics.setColor(Color.BLACK);

        int textW = graphics.getFontMetrics().stringWidth(text);

        int lineCount = Math.max(1, textW / thumbWidth);

        int cc = text.length() / lineCount;

        int index = 0;
        ArrayList<String> lines = new ArrayList<>();

        while (index < text.length()) {
            String sub = text.substring(index, Math.min(index + cc, text.length()));
            lines.add(sub);
            index += cc;
        }

        int y = fm.getAscent();
        for (String line : lines) {
            y += graphics.getFontMetrics().getHeight();
            graphics.drawString(line, 0, y);
        }
        return Optional.ofNullable(toDataHandler(toByteArray(img, "png")));
    }

    @Override
    public List<String> getAcceptedMIMETypes() {
        return list("text/plain", "text/rtf", "application/x-sh");
    }
}
