/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.thumbnailer;

import jakarta.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class GenericThumbnailer extends AbstractThumbnailer {

    public GenericThumbnailer() {
        super();
    }

    public GenericThumbnailer(int thumbHeight, int thumbWidth) {
        super(thumbHeight, thumbWidth);
    }

    @Override
    public Optional<DataHandler> generateThumbnail(InputStream input) throws IOException {
        return Optional.empty();
    }

}
