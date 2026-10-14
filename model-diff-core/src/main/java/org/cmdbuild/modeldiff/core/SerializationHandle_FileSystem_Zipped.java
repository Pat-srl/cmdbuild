/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

import java.io.File;

/**
 * Full path of <b>zipped</b> file on file system that contains the serialization
 * 
 * @author afelice
 */
public class SerializationHandle_FileSystem_Zipped extends SerializationHandle_FileSystem {
 
    public SerializationHandle_FileSystem_Zipped(File outputZippedFile) {
        super(outputZippedFile);
    }
    
}
