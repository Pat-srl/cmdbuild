/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Optional;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import org.cmdbuild.client.rest.core.RestServiceClient;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.data.filter.beans.CmdbSorterImpl.sorter;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoExt;
import org.cmdbuild.etl.config.WaterwayItemInfo;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;

public interface EtlApi extends RestServiceClient {

    EtlGate getGate(String gateId);

    EtlGate updateGate(EtlGate gate);

    @Nullable
    EtlProcessingResult postToGate(String gateId, DataSource newDataSource);

    List<WaterwayDescriptorRecord> getAllDescriptors();

    List<WaterwayItemInfo> getAllItems();

    @Nullable
    WaterwayDescriptorRecord getDescriptorOrNull(String code);

    WaterwayDescriptorInfoExt getDescriptorInfo(String code);

    WaterwayDescriptorInfoExt createUpdateDescriptor(DataSource data);

    void enableDescriptorOrItem(String code);

    void disableDescriptorOrItem(String code);

    void deleteDescriptor(String code);

    PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query);

    WaterwayMessage getMessage(String messageReference);

    DataSource getMessageAttachment(String messageReference, String attachmentName);

    default WaterwayDescriptorInfoExt createUpdateDescriptor(String data) {
        return createUpdateDescriptor(newDataSource(data));
    }

    default List<WaterwayMessage> getLastMessages(long count) {
        return getMessages(DaoQueryOptionsImpl.builder().withLimit(count).withSorter(sorter("timestamp", DESC)).build()).elements();
    }

    default WaterwayDescriptorRecord getDescriptor(String code) {
        return checkNotNull(getDescriptorOrNull(code), "bus descriptor not found for code =< %s >", code);
    }

    default String getDescriptorData(String code) {
        return getDescriptor(code).getData();
    }

    @Nullable
    default String getDescriptorDataOrNull(String code) {
        return Optional.ofNullable(getDescriptorOrNull(code)).map(WaterwayDescriptorRecord::getData).orElse(null);
    }

}
