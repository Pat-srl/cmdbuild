/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.ifc.inner;

import org.cmdbuild.utils.http.ExtServiceConfiguration;
import org.cmdbuild.utils.http.ExtServiceConfigurationImpl;
import org.cmdbuild.utils.http.HttpClient;
import org.cmdbuild.utils.io.BigByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ifc2XktHelperImpl implements Ifc2XktHelper {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final HttpClient httpClient;

    /**
     * Constructs a helper with default configuration (localhost,
     * admin/password).
     */
    public Ifc2XktHelperImpl() {
        this.httpClient = new HttpClient(ExtServiceConfigurationImpl.builder()
                .withUrl("http://localhost:8080/ifc2xkt/api/v1")
                .withUsername("admin")
                .withPassword("password")
                .build());
    }

    /**
     * Constructs a helper with the specified configuration.
     *
     * @param ifc2XktConfiguration the configuration for the IFC-to-XKT service
     */
    public Ifc2XktHelperImpl(ExtServiceConfiguration ifc2XktConfiguration) {
        this.httpClient = new HttpClient(ifc2XktConfiguration);
    }

    /**
     * Converts the given IFC file to XKT format by sending it to the remote
     * service.
     * <p>
     * Checks the service status before conversion. The IFC file is sent as a
     * multipart HTTP POST request, and the resulting XKT file is returned as a
     * {@link BigByteArray}.
     * </p>
     *
     * @param ifc the IFC file as a {@link BigByteArray}
     * @return the converted XKT file as a {@link BigByteArray}
     */
    @Override
    public BigByteArray ifc2Xkt(BigByteArray ifc) {
        this.httpClient.checkServiceStatus();
        return this.httpClient.postConversion(ifc, "/convert");
    }
}
