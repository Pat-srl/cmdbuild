/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.google.common.annotations.VisibleForTesting;
import jakarta.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.cmTmpDir;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.LambdaExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class Zipper {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public final static int BUFFER_SIZE = 1024 * 16; //16KiB;

    // For zipped file
    private ZipOutputStream zos;
    private FileOutputStream fos;
    private File outputFile;

    private final String filenamePrefix;

    public Zipper(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;

        initOutputResources();
    }

    /**
     *
     * @param curEntryName
     * @param contentObj
     * <dl><dt>for textual content <dd>a {@link String};
     * <dt>for binary content <dd>a {@link ByteArrayInputStream}.
     * </dl>
     */
    public void addEntry(String curEntryName, final Object contentObj) {
        rethrowExc(() -> {
            Object curContent = contentObj;

            ZipEntry zipEntry = new ZipEntry(curEntryName);
            zos.putNextEntry(zipEntry);

            if (curContent instanceof DataHandler) {
                curContent = ((DataHandler) curContent).getContent();
            }

            // handle String to ByteArrayInputStream
            if (curContent instanceof String curContentStr) {
                curContent = new ByteArrayInputStream(curContentStr.getBytes(StandardCharsets.UTF_8));
            }
            if (curContent instanceof ByteArrayInputStream curContentByteArray) {
                // Write bufferized for performances (memory occupation) and number of I/O operations sake
                byte[] buffer = new byte[BUFFER_SIZE]; // read buffer
                int bytesRead;
                while ((bytesRead = curContentByteArray.read(buffer)) != -1) { // raises IOException
                    zos.write(buffer, 0, bytesRead);
                }
            } else {
                throw runtime("expected String or ByteArrayInputStream for content DataHandler of =< %s > file, found %s", curEntryName, curContent.getClass().getName());
            }

            zos.closeEntry();
        });
    }

    public File serializeAll() {
        closeOutputResources();
        return outputFile;
    }

    private void initOutputResources() {
        try {
            outputFile = getOutputFile();
            fos = new FileOutputStream(outputFile);
            zos = new ZipOutputStream(fos);
        } catch (IOException ex) {
            throw runtime(ex, "error initializing zipper to write zipped data to file %s", outputFile.getAbsolutePath());
        }
    }

    @VisibleForTesting
    protected File getOutputFile() {
        return CmIoUtils.customTempFile(cmTmpDir(), filenamePrefix, "zip", false); // Don't delete created zip file on exit
    }

    protected void closeOutputResources() {
        if (zos != null) {
            try {
                zos.flush();
                zos.close();
            } catch (IOException ex) {
                logger.warn("error closing zipping stream to write zipped data - {}", ex);
            }

            zos = null;
        }

        if (fos != null) {
            try {
                fos.flush();
                fos.close();
            } catch (IOException ex) {
                logger.warn("error closing file stream to write zipped data - {}", ex);
            }

            fos = null;
        }
    }

    protected <E extends Exception> void rethrowExc(LambdaExceptionUtils.Runnable_WithExceptions<E> runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            throw runtime(ex, "error writing zip content");
        }
    }

} // end Zipper class
