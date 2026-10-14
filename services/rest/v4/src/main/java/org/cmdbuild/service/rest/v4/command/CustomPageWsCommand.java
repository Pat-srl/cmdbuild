/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import jakarta.activation.DataHandler;
import org.cmdbuild.service.rest.v4.model.WsCustomPageData;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import org.cmdbuild.utils.io.CmIoUtils;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class CustomPageWsCommand {

    private final CustomPageService customPageService;

    public CustomPageWsCommand(CustomPageService customPageService) {

        this.customPageService = checkNotNull(customPageService);
    }

    public static List<byte[]> parseCustomUiComponentParams(List<DataHandler> files) {
        return CmCollectionUtils.list(files).without(f -> equal(f.getName(), "data")).map(CmIoUtils::toByteArray);
    }

    public List<UiComponentInfo> doList(Supplier<List<UiComponentInfo>> function) {
        return function.get();
    }

    public UiComponentInfo doGet(Long id, Function<Long, UiComponentInfo> function) {
        return function.apply(id);
    }

    public void doDelete(Long id) {
        customPageService.delete(id);
    }

    public UiComponentInfo doDeleteForTargetDevice(Long id, TargetDevice targetDevice) {
        return customPageService.deleteForTargetDevice(id, targetDevice);
    }

    public UiComponentInfo doCreate(List<DataHandler> files, WsCustomPageData data, Boolean merge) {
        UiComponentInfo customPage;
        if (merge) {
            customPage = customPageService.createOrUpdate(parseCustomUiComponentParams(files));
        } else {
            customPage = customPageService.create(parseCustomUiComponentParams(files));
        }
        customPage = UiComponentInfoImpl.copyOf(customPage).accept(b -> {
            if (data != null) {
                b.withDescription(data.getDescription()).withActive(data.isIsActive()).withIconFont(data.getIconFont());
            }
        }).build();
        return customPageService.update(customPage);
    }

    public UiComponentInfo doUpdate(Long id, List<DataHandler> files, WsCustomPageData data) {
        UiComponentInfo contextMenuComponent = customPageService.get(id);
        List<byte[]> versions = parseCustomUiComponentParams(files);
        checkArgument(!versions.isEmpty() || data != null, "missing data");
        if (!versions.isEmpty()) {
            contextMenuComponent = customPageService.update(id, versions);
        }
        if (data != null) {
            contextMenuComponent = customPageService.update(UiComponentInfoImpl
                    .copyOf(contextMenuComponent)
                    .withDescription(data.getDescription())
                    .withActive(data.isIsActive())
                    .withIconFont(data.getIconFont())
                    .build());
        }
        return contextMenuComponent;
    }

    public DataHandler doDownload(Long id, TargetDevice targetDevice) {
        UiComponentInfo customPage = customPageService.get(id);
        return customPageService.getCustomPageData(customPage.getName(), targetDevice);
    }
}
