/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.service.rest.v4.model.WsConfigMeta;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * @author ldare
 */
@Component
public class EtlConfigWsCommand {

    private final WaterwayDescriptorService waterwayDescriptorService;

    public EtlConfigWsCommand(WaterwayDescriptorService waterwayDescriptorService) {
        this.waterwayDescriptorService = checkNotNull(waterwayDescriptorService);
    }

    public List<WaterwayDescriptorRecord> doReadAll() {
        return waterwayDescriptorService.getAllDescriptors();
    }

    public WaterwayDescriptorRecord doRead(String code, Boolean checkIfExists) {
        if (checkIfExists) {
            return waterwayDescriptorService.getDescriptorOrNull(code);
        }
        return waterwayDescriptorService.getDescriptor(code);
    }

    public List<WaterwayItem> doReadItems(String code) {
        return list(waterwayDescriptorService.getAllItems()).filter(i -> equal(code, "_ALL") || equal(i.getDescriptorCode(), code));
    }

    public WaterwayDescriptorRecord doCreate(DataHandler dataHandler, WsConfigMeta meta, Boolean overwriteIfExists) {
        DataSource data = dataHandler != null ? toDataSource(dataHandler) : newDataSource(checkNotBlank(meta.getData(), "missing configuration file payload"));
        return waterwayDescriptorService.createUpdateDescriptor(data, meta == null ? null : meta.toMeta(), overwriteIfExists);
    }

    public WaterwayDescriptorRecord doUpdate(String code, DataHandler dataHandler, WsConfigMeta meta) {
        DataSource data = dataHandler != null ? toDataSource(dataHandler) : (meta == null || isBlank(meta.getData()) ? null : newDataSource(meta.getData()));
        if (data == null) {
            waterwayDescriptorService.updateDescriptorMeta(code, checkNotNull(meta, "missing config file meta").toMeta());
            return null;
        }
        return waterwayDescriptorService.createUpdateDescriptor(data, meta == null ? null : meta.toMeta());
    }

    public void doDelete(String code) {
        waterwayDescriptorService.deleteDescriptor(code);
    }
}
