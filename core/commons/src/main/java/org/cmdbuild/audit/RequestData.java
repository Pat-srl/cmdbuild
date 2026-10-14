/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.nullToEmpty;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.audit.RequestInfo.NO_SESSION_ID;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmMultipartUtils.getSinglePlaintextPart;
import static org.cmdbuild.utils.io.CmMultipartUtils.hasSinglePlaintextPart;
import static org.cmdbuild.utils.io.CmMultipartUtils.isMultipart;
import static org.cmdbuild.utils.io.CmMultipartUtils.isPlaintext;
import static org.cmdbuild.utils.json.CmJsonUtils.prettifyIfJson;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.xml.CmXmlUtils.prettifyIfXml;
import org.cmdbuild.fault.FaultEvent;

public interface RequestData extends RequestInfo {

    @Nullable
    Long getId();

    String getClient();

    String getUserAgent();

    List<FaultEvent> getFaultEvents();

    @Nullable
    String getPayloadText();

    @Nullable
    String getPayloadContentType();

    @Nullable
    Long getPayloadSize();

    @Nullable
    String getResponseText();

    @Nullable
    String getResponseContentType();

    @Nullable
    Long getResponseSize();

    @Nullable
    String getLogs();

    Map<String, String> getRequestHeaders();

    Map<String, String> getResponseHeaders();

    @Nullable
    byte[] getPayloadBytes();

    @Nullable
    byte[] getResponseBytes();

    @Nullable
    byte[] getTcpDumpBytes();

    default long getPayloadSizeOrZero() {
        return firstNotNull(getPayloadSize(), 0l);
    }

    default long getResponseSizeOrZero() {
        return firstNotNull(getResponseSize(), 0l);
    }

    default boolean hasPayloadBytes() {
        return getPayloadBytes() != null && getPayloadBytes().length > 0;
    }

    default boolean hasResponseBytes() {
        return getResponseBytes() != null && getResponseBytes().length > 0;
    }

    default byte[] getBinaryPayload() {
        if (hasPayloadBytes()) {
            return getPayloadBytes();
        } else {
            return nullToEmpty(getPayloadText()).getBytes(StandardCharsets.UTF_8);
        }
    }

    default byte[] getBinaryResponse() {
        if (hasResponseBytes()) {
            return getResponseBytes();
        } else {
            return nullToEmpty(getResponseText()).getBytes(StandardCharsets.UTF_8);
        }
    }

    default boolean hasLogs() {
        return isNotBlank(getLogs());
    }

    default boolean hasSession() {
        return !equal(getSessionId(), NO_SESSION_ID) && isNotBlank(getSessionId());
    }

    default boolean hasPayload() {
        return hasPayloadText() || hasPayloadBytes();
    }

    default boolean hasResponse() {
        return hasResponseText() || hasResponseBytes();
    }

    default boolean hasPayloadText() {
        return isNotBlank(getPayloadText());
    }

    default boolean hasResponseText() {
        return isNotBlank(getResponseText());
    }

    default boolean isBinaryPayload() {
        return hasPayload() && !isPlaintext(getPayloadContentType());
    }

    default boolean isBinaryResponse() {
        return hasResponse() && !isPlaintext(getResponseContentType());
    }

    default boolean isPlaintextPayload() {
        return hasPayload() && isPlaintext(getPayloadContentType());
    }

    default boolean isPlaintextResponse() {
        return hasResponse() && isPlaintext(getResponseContentType());
    }

    default String getBestPlaintextPayload() {
        String plaintextPayload = null;
        if (isPlaintextPayload()) {
            plaintextPayload = getPayloadText();
        } else if (isBinaryPayload()) {
            DataSource dataSource = newDataSource(getBinaryPayload(), getPayloadContentType());
            if (isMultipart(dataSource) && hasSinglePlaintextPart(dataSource)) {
                plaintextPayload = getSinglePlaintextPart(dataSource);
            } else {
                plaintextPayload = "< BINARY PAYLOAD >";
            }
        }
        return prettifyIfJson(prettifyIfXml(nullToEmpty(plaintextPayload)));
    }

    default String getBestPlaintextResponse() {
        String plaintextResponse = null;
        if (isPlaintextResponse()) {
            plaintextResponse = getResponseText();
        } else if (isBinaryResponse()) {
            DataSource dataSource = newDataSource(getBinaryResponse(), getResponseContentType());
            if (isMultipart(dataSource) && hasSinglePlaintextPart(dataSource)) {
                plaintextResponse = getSinglePlaintextPart(dataSource);
            } else {
                plaintextResponse = "< BINARY RESPONSE >";
            }
        }
        return prettifyIfJson(prettifyIfXml(nullToEmpty(plaintextResponse)));
    }

    default boolean hasTcpDump() {
        return getTcpDumpBytes() != null && getTcpDumpBytes().length > 0;
    }

}
