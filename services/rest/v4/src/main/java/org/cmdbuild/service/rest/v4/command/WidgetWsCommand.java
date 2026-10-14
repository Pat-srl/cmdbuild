/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.base.Supplier;
import jakarta.activation.DataHandler;
import org.cmdbuild.service.rest.v4.model.WsWidgetComponentData;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import org.cmdbuild.uicomponents.widget.WidgetComponentService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.service.rest.v4.command.CustomPageWsCommand.parseCustomUiComponentParams;

/**
 * @author ldare
 */
@Component
public class WidgetWsCommand {

    private final WidgetComponentService widgetComponentService;

    public WidgetWsCommand(WidgetComponentService widgetComponentService) {
        this.widgetComponentService = checkNotNull(widgetComponentService);
    }

    public List<UiComponentInfo> doList(Supplier<List<UiComponentInfo>> function) {
        return function.get();
    }

    public UiComponentInfo doGet(Long id) {
        return widgetComponentService.get(id);
    }

    public DataHandler doDownload(Long id, TargetDevice targetDevice) {
        UiComponentInfo widgetComponent = widgetComponentService.get(id);
        return widgetComponentService.getWidgetData(widgetComponent.getName(), targetDevice);
    }

    public void doDeleteForTargetDevice(Long id, TargetDevice targetDevice) {
        widgetComponentService.deleteForTargetDevice(id, targetDevice);
    }

    public UiComponentInfo doCreate(List<DataHandler> files, WsWidgetComponentData data, Boolean merge) {
        UiComponentInfo info;
        if (merge) {
            info = widgetComponentService.createOrUpdate(parseCustomUiComponentParams(files));
        } else {
            info = widgetComponentService.create(parseCustomUiComponentParams(files));
        }
        info = UiComponentInfoImpl.copyOf(info).accept(b -> {
            if (data != null) {
                b.withDescription(data.getDescription()).withActive(data.isActive());
            }
        }).build();
        return info;
    }

    public UiComponentInfo doUpdate(Long id, List<DataHandler> files, WsWidgetComponentData data) {
        UiComponentInfo component = widgetComponentService.get(id);
        List<byte[]> versions = parseCustomUiComponentParams(files);
        checkArgument(!versions.isEmpty() || data != null, "missing data");
        if (!versions.isEmpty()) {
            component = widgetComponentService.update(id, versions);
        }
        if (data != null) {
            component = widgetComponentService.update(UiComponentInfoImpl.copyOf(component).withDescription(data.getDescription()).withActive(data.isActive()).build());
        }
        return component;
    }
}
