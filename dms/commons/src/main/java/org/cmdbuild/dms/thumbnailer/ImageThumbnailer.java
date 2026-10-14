/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.thumbnailer;

import jakarta.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import static org.cmdbuild.utils.io.CmImageUtils.resizeImage;
import static org.cmdbuild.utils.io.CmIoUtils.toDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class ImageThumbnailer extends AbstractThumbnailer {

    public ImageThumbnailer() {
        super();
    }

    public ImageThumbnailer(int thumbHeight, int thumbWidth) {
        super(thumbHeight, thumbWidth);
    }

    @Override
    public Optional<DataHandler> generateThumbnail(InputStream input) throws IOException {
        return Optional.ofNullable(toDataHandler(resizeImage(input, thumbWidth, thumbHeight)));
    }

    @Override
    public List<String> getAcceptedMIMETypes() {
        return list("image/png", "image/jpeg", "image/tiff", "image/bmp", "image/jpg", "image/gif");
    }
}
