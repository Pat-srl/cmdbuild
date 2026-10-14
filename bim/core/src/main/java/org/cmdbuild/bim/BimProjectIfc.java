package org.cmdbuild.bim;

public interface BimProjectIfc {

    Long getId();
    
    String getProjectId();

    byte[] getIfcFile();

    byte[] getIfcDecompressedFile();

}
