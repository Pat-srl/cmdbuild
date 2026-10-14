/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.Schema;
import org.bimserver.ifc.step.deserializer.Ifc2x3tc1StepDeserializer;
import org.bimserver.ifc.step.deserializer.Ifc4StepDeserializer;
import org.bimserver.ifc.step.deserializer.IfcStepDeserializer;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.cmdbuild.utils.ifc.IfcModel;
import org.cmdbuild.utils.ifc.utils.IfcUtils;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.buildPackageMetadata;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.isZip;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.listenToStreamProgress;
import org.cmdbuild.utils.io.StreamProgressListener;
import org.cmdbuild.utils.lang.CmExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfcLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String fileName;
    private final DataSource data;
    private final long length;

    private StreamProgressListener progressListener;

    public IfcLoader(DataSource data) {
        this.data = checkNotNull(data);
        if (isBlank(data.getName())) {
            this.fileName = isZip(data) ? "file.zip" : "file.ifc";
        } else {
            this.fileName = data.getName();
        }
        this.length = CmIoUtils.countBytes(data);
    }

    public IfcModel loadIfc() {
        try {
            Schema schema = IfcUtils.detectSchema(data);
            IfcStepDeserializer deserializer;
            deserializer = switch (schema) {
                case IFC2X3TC1 ->
                    new Ifc2x3tc1StepDeserializer();
                case IFC4 ->
                    new Ifc4StepDeserializer(schema);
                default ->
                    throw new UnsupportedOperationException("unsupported schema = " + schema);
            };
            deserializer.init(buildPackageMetadata(schema));
            logger.info("load ifc =< {} > ( {} {} )", fileName, FileUtils.byteCountToDisplaySize(length), CmIoUtils.getContentType(data));
            try (InputStream in = data.getInputStream()) {
                deserializer.read(listenToStreamProgress(in, (e) -> {
                    logger.debug("loading ifc =< {} > : {}", fileName, e.getProgressDescriptionDetailed());
                    if (progressListener != null) {
                        progressListener.handleStreamProgressEvent(e);
                    }
                }), fileName, length, null);
            }
            logger.info("loaded ifc =< {} >", fileName);
            IfcModelInterface model = deserializer.getModel();
            model.fixInverseMismatches();
            return new IfcModelImpl(model);
        } catch (DeserializeException | IOException ex) {
            throw CmExceptionUtils.runtime(ex);
        }
    }

    public IfcLoader withProgressListener(StreamProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

}
