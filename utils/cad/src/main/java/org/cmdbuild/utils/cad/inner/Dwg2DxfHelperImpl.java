/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.cad.inner;

import org.cmdbuild.utils.http.ExtServiceConfiguration;
import org.cmdbuild.utils.http.ExtServiceConfigurationImpl;
import org.cmdbuild.utils.http.HttpClient;
import org.cmdbuild.utils.io.BigByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link Dwg2DxfHelper} that converts DWG files to DXF format
 * using a remote HTTP service.
 * <p>
 * This class manages HTTP connections and authentication to a DWG-to-DXF
 * conversion service. It provides methods to check the service status and to
 * send DWG files for conversion via multipart HTTP POST requests.
 * </p>
 *
 * <p>
 * Usage:
 *
 * <pre>
 * Dwg2DxfHelper helper = new Dwg2DxfHelperImpl();
 * BigByteArray dxf = helper.dwg2Dxf(dwgBytes);
 * helper.close();
 * </pre>
 * </p>
 *
 * <p>
 * The service URL, username, and password can be configured via the
 * {@link Dwg2DxfConfiguration} interface.
 * </p>
 */
public class Dwg2DxfHelperImpl implements Dwg2DxfHelper {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final HttpClient httpClient;

    /**
     * Constructs a helper with default configuration (localhost,
     * admin/password).
     */
    public Dwg2DxfHelperImpl() {
        this.httpClient = new HttpClient(ExtServiceConfigurationImpl.builder()
                .withUrl("http://localhost:8080/dwg2dxf/api/v1")
                .withUsername("admin")
                .withPassword("password")
                .build());
    }

    /**
     * Constructs a helper with the specified configuration.
     *
     * @param dwg2DxfConfiguration the configuration for the DWG-to-DXF service
     */
    public Dwg2DxfHelperImpl(ExtServiceConfiguration dwg2DxfConfiguration) {
        this.httpClient = new HttpClient(dwg2DxfConfiguration);
    }

    /**
     * Converts the given DWG file to DXF format by sending it to the remote
     * service.
     * <p>
     * Checks the service status before conversion. The DWG file is sent as a
     * multipart HTTP POST request, and the resulting DXF file is returned as a
     * {@link BigByteArray}.
     * </p>
     *
     * @param dwg the DWG file as a {@link BigByteArray}
     * @return the converted DXF file as a {@link BigByteArray}
     */
    @Override
    public BigByteArray dwg2Dxf(BigByteArray dwg) {
        this.httpClient.checkServiceStatus();
        return this.httpClient.postConversion(dwg, "/convert");
    }
}
