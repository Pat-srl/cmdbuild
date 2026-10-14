package org.cmdbuild.common.utils;

import jakarta.activation.DataSource;
import jakarta.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempDataSource implements DataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PREFIX = "tempdatasource";

    private final String name;
    private final String contentType;
    private final File file;

    private TempDataSource(TempDataSourceBuilder builder) {
        this.name = builder.name;
        this.contentType = builder.contentType;
        this.file = builder.file;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContentType() {
        return defaultIfNull(contentType, new MimetypesFileTypeMap().getContentType(file));
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(file);
    }

    public File getFile() {
        return file;
    }

    public static TempDataSourceBuilder newInstance() {
        return new TempDataSourceBuilder();
    }

    public static class TempDataSourceBuilder implements Builder<TempDataSource, TempDataSourceBuilder> {

        private String name;
        private String contentType;
        private File file;

        private TempDataSourceBuilder() {
            // use factory method
        }

        private void validate() {
            checkNotBlank(name, "invalid file name");
            try {
                file = File.createTempFile(PREFIX, name);
                file.deleteOnExit();
            } catch (final Exception e) {
                LOGGER.error("error creating temporary file");
                throw new RuntimeException(e);
            }
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withContentType(final String contentType) {
            this.contentType = contentType;
            return this;
        }

        @Override
        public TempDataSource build() {
            validate();
            return new TempDataSource(this);
        }
    }
}
