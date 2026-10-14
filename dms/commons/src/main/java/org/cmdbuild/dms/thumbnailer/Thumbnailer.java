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
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author ataboga
 */
public interface Thumbnailer {

    Optional<DataHandler> generateThumbnail(InputStream input) throws IOException;

    default List<String> getAcceptedMIMETypes() {
        return list();
    }
}
