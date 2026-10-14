/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.google.common.annotations.VisibleForTesting;
import org.cmdbuild.email.EmailAccount;
import java.io.ByteArrayInputStream;
import java.io.File;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.encode.CmPackUtils.unpack;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author afelice
 */
public class EmailMSGraphClient_ClientCertificate extends BaseEmailMSGraphClientProvider {

    public final static String CLIENT_ID_KEY = "clientId";
    public final static String TENANT_ID_KEY = "tenantId";
    public final static String CLIENT_PRIVATE_KEY = "clientPrivateKey";
    public final static String CLIENT_CERTIFICATE_KEY = "clientCertificate";

    @VisibleForTesting
    protected File _tempCertificateFile;
    private final ClientCertificateCredentialBuilder wrappedBuilder = new ClientCertificateCredentialBuilder();

    public EmailMSGraphClient_ClientCertificate(EmailAccount emailAccount) {
        super(emailAccount);
    }

    @Override
    protected TokenCredential buildCredential() {
        String clientId = msConfig.getString(CLIENT_ID_KEY);
        String tenantId = msConfig.getString(TENANT_ID_KEY);

        String filePrefix = format("%s_%s", clientId, tenantId);
        String fileSuffix = "pem";
        String certificatePath = storeAsTempFile(filePrefix, fileSuffix, new ByteArrayInputStream(unpackFullCertificatePem(msConfig).getBytes(StandardCharsets.UTF_8)));

        return wrappedBuilder
                .clientId(clientId)
                .tenantId(tenantId)
                .pemCertificate(certificatePath)
                .build();
    }

    @Override
    public void close() {
        releaseTemporaryData();
        super.close();
    }

    /**
     * Unpack and concatenate <b>both</b> application Private Key PEM and
     * application Certificate PEM <b>in the same PEM (text) format
     *
     * @param msConfig
     * @return
     * @throws JSONException
     */
    private String unpackFullCertificatePem(JSONObject msConfig) throws JSONException {
        return unpack(msConfig.getString(CLIENT_PRIVATE_KEY)) + unpack(msConfig.getString(CLIENT_CERTIFICATE_KEY));
    }

    /**
     *
     * @param filePrefix
     * @param fileSuffix
     * @param byteInputStream
     * @return absolute path of created temporary file
     */
    private String storeAsTempFile(String filePrefix, String fileSuffix, ByteArrayInputStream byteInputStream) {
        byte[] certificateBytes = byteInputStream.readAllBytes();
        _tempCertificateFile = CmIoUtils.currentUserTempFile(
                filePrefix, fileSuffix,
                certificateBytes);

        return _tempCertificateFile.getAbsolutePath();
    }

    private void releaseTemporaryData() {
        if (_tempCertificateFile != null) {
            _tempCertificateFile.delete();
        }
    }
}
