/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

import java.io.File;

/**
 * Full path of file on file system that contains the serialization.
 * 
 * @author afelice
 */
public class SerializationHandle_FileSystem implements SerializationHandle {

    protected final File outputFile; 
    
    public SerializationHandle_FileSystem(File outputFile) {
        this.outputFile = outputFile;
    }
    
    @Override
    public String getSerializationInfo() {
        return outputFile.getAbsolutePath();
    }
    
}
