package org.cmdbuild.api.fluent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;

public interface Attachment extends AttachmentDescriptor {

    @Nullable
    String getUrl();

    @Nullable
    default Object getDocument() {
        return null;
    }

    @Nullable
    default DataSource getData() {
        return null;
    }

    default boolean hasUrl() {
        return isNotBlank(getUrl());
    }

    default boolean hasDocument() {
        return getDocument() != null;
    }

    default boolean hasData() {
        return getData() != null;
    }

    default InputStream getInputStream() {
        try {
            return Optional.ofNullable(getData()).map(rethrowFunction(DataSource::getInputStream)).orElse(null);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    default byte[] getBytes() {
        return Optional.ofNullable(getData()).map(CmIoUtils::toByteArray).orElse(null);
    }
}
