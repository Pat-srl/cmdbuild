/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import jakarta.activation.DataHandler;
import java.io.File;
import org.cmdbuild.modeldiff.schema.FeatureToggles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write output into zipped file in filesystem.
 *
 *
 * @author afelice
 */
public class ProcessXpdlZipper {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final FeatureToggles featureToggles;

    /**
     * Holds a zip file in CDMBuild <i>temp directory</i>.
     */
    private Zipper zipper;

    public final static String XDPL_FILENAME_STR = "process_%s-planId_%s.xpdl";

    /**
     * Used while zipping <code>XPDL files</code>, one at a time.
     *
     * @param zipFilenamePrefix the file to create prefix name (will have a
     * suffix with timestamp and unique id), like
     * <code>&lt;schemaName&gt_&lt;schemaId&gt;_XPDLs_</code>
     * @param featureToggles
     */
    public ProcessXpdlZipper(String zipFilenamePrefix, FeatureToggles featureToggles) {
        this.featureToggles = featureToggles;

        if (!this.featureToggles.handleProcessXpdl) {
            return;
        }

        // Will create a ZIP file <schemaName>_<schemaId>_XPDLs_<timestamp>_<unique>.zip
        this.zipper = new Zipper(zipFilenamePrefix);
    }

    /**
     * Add a <code>XPDL file</code> to the overall zip file.
     *
     * @param processName
     * @param planId
     * @param content
     *
     * @return generated zipEntryName
     */
    public String addXPDLSerialization(String processName, String planId, DataHandler content) {
        if (!this.featureToggles.handleProcessXpdl) {
            return null;
        }
        final String zipEntryName = XDPL_FILENAME_STR.formatted(processName, planId);

        // process-<processId>_planId-<planId>.xpdl
        zipper.addEntry(zipEntryName, content);

        return zipEntryName;
    }

    public File serializeAllXPDLs() {
        if (!this.featureToggles.handleProcessXpdl) {
            return null;
        }

        return zipper.serializeAll();
    }

} // end ProcessZipper class
