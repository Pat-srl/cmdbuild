package org.cmdbuild.gis;

public interface GisSchemaService {

	String getPostgisVersion();

	void checkGisSchemaAndCreateIfMissing();

	boolean isGisSchemaOk();
}
