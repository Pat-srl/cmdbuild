/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.google.common.annotations.VisibleForTesting;
import jakarta.activation.DataHandler;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.cmdbuild.utils.io.CmIoUtils;
import org.cmdbuild.utils.lang.CmException;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.LambdaExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class Unzipper {

    public final static int BUFFER_SIZE = 1024 * 16; //16KiB;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ZipFile zipFile;
    private final String zipFilename;


    public Unzipper(String aZipFilename) throws IOException {
        this.zipFilename = aZipFilename;
        // Raises IOException
        this.zipFile = initInputFile(aZipFilename);
    }

    /**
     * Unzip a {@link Process} <code>XPDL</code> entry from a zip {@link File}.
     *
     * <p>
     * A single <code>XPDL</code> is extracted because in <i>apply diff</i>
     * only added/to update ones are needed. Unaltered ones are so not
     * extracted.
     *
     * @param zipEntryFileName
     * @param errMsgContext
     * @return
     * @throws {@link CmException} if <code>Zip entry</code> not found.
     */
    public DataHandler unzip(String zipEntryFileName, String errMsgContext) {

        try {
            // find ZipEntry for that file
            ZipEntry fileEntry = zipFile.getEntry(zipEntryFileName);
            if (fileEntry == null) {
                throw new CmException("%s, couldn't find file entry =< %s > in zip file =< %s >".formatted(errMsgContext, zipEntryFileName, zipFilename));
            }

            // InputStream on file
            try (InputStream fileInputStream = zipFile.getInputStream(fileEntry); // This ByteArrayOutputStream is an in-memory stuff containing the docuemnt content bytes:
                    // won't disappear even if ZipEntry is closed
                      ByteArrayOutputStream fileByteArrayOutputStream = new ByteArrayOutputStream()) {

                // Write bufferized for performances (memory occupation) and number of I/O operations sake
                byte[] buffer = new byte[BUFFER_SIZE]; // read buffer
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    fileByteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                // Build a DataHandler, detecting its mime-type from content
                return CmIoUtils.toDataHandler(fileByteArrayOutputStream.toByteArray(), zipEntryFileName);
            }
        } catch (IOException ex) {
            throw runtime("%s, error unzipping cotent =< %s > from zip file =< %s > - %s".formatted(errMsgContext, zipEntryFileName, this.zipFilename, ex));
        } // end try-clause
    }

    public void close() {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException ex) {
                logger.warn("error closing unzipping file to read zipped data - {}", ex);
            }
        }
    }

    @VisibleForTesting
    protected ZipFile initInputFile(String zipFilename) throws IOException {
        File file = new File(zipFilename);

        if (!file.exists()) {
            throw new IOException("error initializing unzipper to read zipped data from not found file =< %s >".formatted(file.getAbsoluteFile()));
        }

        // reads the zip entries, so hundreds of files means some seconds
        // raises IOException, ZipException
        try {
            return new ZipFile(file);
        } catch (IOException ex) {
            throw new IOException("error initializing unzipper to read zipped data from file =< %s > - %s".formatted(this.zipFilename, ex));
        }
    }

    protected <E extends Exception> void rethrowExc(LambdaExceptionUtils.Runnable_WithExceptions<E> runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            throw runtime(ex, "error fetching zip content");
        }
    }
} // end Unzipper class
