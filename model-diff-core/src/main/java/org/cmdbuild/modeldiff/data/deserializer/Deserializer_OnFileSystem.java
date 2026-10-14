/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data.deserializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;
import org.cmdbuild.modeldiff.core.SerializationHandle_FileSystem;
import org.cmdbuild.modeldiff.core.SerializationHandle_FileSystem_Zipped;
import org.cmdbuild.utils.io.CmIoUtils;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmExceptionUtils;

/**
 *
 * @author afelice
 * @param <U>
 */
public abstract class Deserializer_OnFileSystem <U> implements DataDeserializer<SerializationHandle_FileSystem, U> {
    
    protected Class<U> uClass;
    protected String errMsgDescr; 
    
    /**
     * 
     * @param uClass needed to overcome type erasure.
     * @param errMsgDescr error message descriptive info, used in error handling.
     */
    protected Deserializer_OnFileSystem(Class<U> uClass, String errMsgDescr) {
        this.uClass = uClass;
        this.errMsgDescr = errMsgDescr;
    }
    
    @Override
    public U deserialize(SerializationHandle_FileSystem serializedData) {
        String filename = serializedData.getSerializationInfo();
        if (!Files.exists(Path.of(filename))) {
            throw CmExceptionUtils.runtime("while deserializing json %s, file =< %s > not found.", errMsgDescr, filename);
        }
        if (!Files.isReadable(Path.of(filename))) {
            throw CmExceptionUtils.runtime("while deserializing json %s, file =< %s > not readable.", errMsgDescr, filename);
        }
        if (serializedData instanceof SerializationHandle_FileSystem_Zipped) {
            return deserializeZipped(new File(filename));
        } else {
            return CmJsonUtils.fromJson(CmIoUtils.readToString(new File(serializedData.getSerializationInfo())), uClass);
        }
    }

    protected U deserializeZipped(File zipFile) {
        U result = initData();
        try (ZipFile zip = new ZipFile(zipFile)) {
            zip.stream().filter(e -> e.getName().toLowerCase().endsWith(".json")).forEach(jsonEntry -> {
                try (InputStream jsonInStream = zip.getInputStream(jsonEntry)) {
                    U curExtractedPartialData = CmJsonUtils.fromJson(jsonInStream, uClass);
                    cumulateData(result, curExtractedPartialData);
                } catch (IOException ex) {
                    throw CmExceptionUtils.runtime("while deserializing json model (card data) file =< %s >, error deserializing entry =< %s > - %s.", zipFile.getAbsolutePath(), jsonEntry.getName(), ex);
                }
            });
        } catch (IOException ex) {
            throw CmExceptionUtils.runtime("while deserializing json model (card data), error deserializing file =< %s > - %s.", zipFile.getAbsolutePath(), ex);
        } // end try-resource zip file
        return result;
    }
    
    abstract protected U initData();
    
    abstract protected void cumulateData(U result, U partialData) throws IOException;
}
