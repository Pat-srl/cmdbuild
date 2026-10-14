/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static org.cmdbuild.etl.gate.EtlGateService.ETLGATE_REQUEST_METHOD;
import static org.cmdbuild.etl.gate.EtlGateService.ETLGATE_REQUEST_PATH;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.payloadToRecords;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

public interface EtlLoaderApi {

    WaterwayMessageData getContext();

    String getGateCode();

    Map<String, String> getConfig();

    List<EtlTemplate> getTemplates();

    Map<String, EtlLoaderApiAttachment> getAttachmentsByCode();

    EtlLoaderApiAttachmentHelper newAttachment(String code);

    default Map<String, String> getMeta() {
        return getContext().getMeta();
    }

    default boolean hasAttachment() {
        return getAttachments().size() == 1;
    }

    @Nullable
    default Object getObject() {
        return hasAttachment() ? getAttachment().getObject() : null;
    }

    default List getRecords() {
        return hasAttachment() ? payloadToRecords(getObject()) : emptyList();//TODO cache record parsing ??
    }

    default DataSource getData() {
        return hasAttachment() ? getAttachment().getData() : newDataSource("", "text/plain");
    }

    default String getDataAsString() {
        return hasAttachment() ? getAttachment().getDataAsString() : "";
    }

    default EtlLoaderApiAttachment getAttachment() {
        return getOnlyElement(getAttachments());//TODO improve this
    }

    default DataSource getData(String code) {
        return getAttachment(code).getData();
    }

    default String getDataAsString(String code) {
        return getAttachment(code).getDataAsString();
    }

    @Nullable
    default Object getObject(String code) {
        return hasAttachment(code) ? getAttachment(code).getObject() : null;
    }

    default List getRecords(String code) {
        return hasAttachment(code) ? payloadToRecords(getObject(code)) : emptyList();//TODO cache record parsing ??
    }

    default Collection<EtlLoaderApiAttachment> getAttachments() {
        return getAttachmentsByCode().values();
    }

    default EtlLoaderApiAttachment getAttachment(String code) {
        return checkNotNull(getAttachmentsByCode().get(checkNotBlank(code)), "attachment not found for code =< %s >", code);
    }

    default boolean hasAttachment(String code) {
        return getAttachmentsByCode().containsKey(checkNotBlank(code));
    }

    default EtlLoaderApiAttachmentHelper newAttachment(String name, Object data) {
        return newAttachment(name).withData(data);
    }

    default EtlLoaderApiAttachmentHelper newAttachment(String name, Object data, String... meta) {
        return newAttachment(name).withData(data).withMeta(meta);
    }

    default String getConfigNotBlank(String key) {
        return checkNotBlank(getConfig(key), "config not found for key =< %s >", key);
    }

    @Nullable
    default String getConfig(String key) {
        return getConfig().get(key);
    }

    @Nullable
    default String getParam(String key) {
        return firstNotBlankOrNull(getMeta(format("param_%s", key)), getMeta(key), getConfig(key));
    }

    default String getParamNotBlank(String key) {
        return checkNotBlank(getParam(key), "config/param not found for key =< %s >", key);
    }

    @Nullable
    default String getMeta(String key) {
        return getMeta().get(key);
    }

    @Nullable
    default String getPath() {
        return getParam(ETLGATE_REQUEST_PATH);
    }

    @Nullable
    default String getMethod() {
        return getParam(ETLGATE_REQUEST_METHOD);
    }

    default List<String> getPathParts() {
        return Splitter.on("/").omitEmptyStrings().trimResults().splitToList(nullToEmpty(getPath()));
    }

}
