/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import org.cmdbuild.service.rest.v4.model.WsContextMenuComponentData;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import org.cmdbuild.uicomponents.contextmenu.ContextMenuComponentService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.service.rest.v4.command.CustomPageWsCommand.parseCustomUiComponentParams;

/**
 * @author ldare
 */
@Component
public class ContextMenuComponentWsCommand {

    private final ContextMenuComponentService contextMenuComponentService;

    public ContextMenuComponentWsCommand(ContextMenuComponentService contextMenuComponentService) {
        this.contextMenuComponentService = checkNotNull(contextMenuComponentService);
    }

    public UiComponentInfo doGet(Long id) {
        return contextMenuComponentService.get(id);
    }

    public List<UiComponentInfo> doList() {
        return contextMenuComponentService.getAll();
    }

    public void doDelete(Long id) {
        contextMenuComponentService.delete(id);
    }

    public UiComponentInfo doDeleteForTargetDevice(Long id, TargetDevice targetDevice) {
        return contextMenuComponentService.deleteForTargetDevice(id, targetDevice);
    }

    public DataHandler doDownload(Long id, TargetDevice targetDevice) {
        UiComponentInfo customMenuComponent = contextMenuComponentService.get(id);
        return contextMenuComponentService.getContextMenuData(customMenuComponent.getName(), targetDevice);
    }

    public UiComponentInfo doCreate(List<DataHandler> files, Boolean merge, WsContextMenuComponentData data) {
        UiComponentInfo info;
        if (merge) {
            info = contextMenuComponentService.createOrUpdate(parseCustomUiComponentParams(files));
        } else {
            info = contextMenuComponentService.create(parseCustomUiComponentParams(files));
        }
        info = UiComponentInfoImpl.copyOf(info).accept(b -> {
            if (data != null) {
                b.withDescription(data.getDescription()).withActive(data.getIsActive());
            }
        }).build();
        return contextMenuComponentService.update(info);
    }

    public UiComponentInfo doUpdate(Long id, List<DataHandler> files, WsContextMenuComponentData data) {
        UiComponentInfo contextMenuComponent = contextMenuComponentService.get(id);
        List<byte[]> versions = parseCustomUiComponentParams(files);
        checkArgument(!versions.isEmpty() || data != null, "missing data");
        if (!versions.isEmpty()) {
            contextMenuComponent = contextMenuComponentService.update(id, versions);
        }
        if (data != null) {
            contextMenuComponent = contextMenuComponentService.update(UiComponentInfoImpl.copyOf(contextMenuComponent).withDescription(data.getDescription()).withActive(data.getIsActive()).build());
        }
        return contextMenuComponent;
    }
}
