/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import jakarta.activation.DataHandler;
import java.io.IOException;
import org.cmdbuild.modeldiff.core.CmModelDiffErrors;
import org.cmdbuild.modeldiff.schema.FeatureToggles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read {@link Process} <i>XPDL definition</i> from a zipped file in filesystem
 * created by {@link ProcessXpdlZipper}.
 *
 * @author afelice
 */
public class ProcessXpdlUnzipper {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final FeatureToggles featureToggles;
    private Unzipper unzipper;

    private final static String XDPL_FILENAME_STR = ProcessXpdlZipper.XDPL_FILENAME_STR;

    /**
     * Used while unzipping a <code>XPDL file</code>.
     *
     * @param zipFilename the zip file complete filename from which to unzip
     * content
     * @param notBlockingErrors a not blocking error is added if
     * <code>.zip</code> not found.
     * @param featureToggles
     */
    public ProcessXpdlUnzipper(String zipFilename, CmModelDiffErrors notBlockingErrors, FeatureToggles featureToggles) {
        this.featureToggles = featureToggles;

        if (!this.featureToggles.handleProcessXpdl || zipFilename == null) {
            return;
        }

        // Throws IOException
        try {
            this.unzipper = new Unzipper(zipFilename);
        } catch (IOException excIO) {
            String errMsg = "while unzipping XPDL files %s".formatted(excIO.getMessage());
            notBlockingErrors.add(errMsg);
            logger.error(errMsg);
        }
    }

    /**
     *
     * @param processName
     * @param planId
     * @return
     * @throws {@link CmException} if <code>XPDL file</code> not found.
     */
    public DataHandler readXPDLSerialization(String processName, String planId) {
        if (!this.featureToggles.handleProcessXpdl || unzipper == null) {
            return null;
        }

        // Unzip from ZipEntry process_<processId>-planId_<planId>.xpdl
        return unzipper.unzip(XDPL_FILENAME_STR.formatted(processName, planId), "error while unzipping XDPL for process =< %s >, planId =< %s >".formatted(processName, planId));
    }

    public void close() {
        if (unzipper != null) {
            unzipper.close();
        }
    }

} // end ProcessHandler class
