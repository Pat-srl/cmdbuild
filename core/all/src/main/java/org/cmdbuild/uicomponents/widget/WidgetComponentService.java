/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents.widget;

import static java.util.Arrays.asList;
import java.util.List;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;

public interface WidgetComponentService {

    List<UiComponentInfo> getAll();

    List<UiComponentInfo> getAllActive();

    List<UiComponentInfo> getActiveForCurrentUserAndDevice();

    UiComponentInfo get(long id);

    void delete(long id);

    UiComponentInfo deleteForTargetDevice(long id, TargetDevice targetDevice);

    UiComponentInfo createOrUpdate(List<byte[]> data);

    UiComponentInfo create(List<byte[]> data);

    UiComponentInfo update(long id, List<byte[]> data);

    UiComponentInfo update(UiComponentInfo widget);

    byte[] getWidgetFile(String name, String filePath);

    DataHandler getWidgetData(String code, TargetDevice targetDevice);

    @Nullable
    UiComponentInfo getOneByCodeOrNull(String type);

    default UiComponentInfo create(byte[]... data) {
        return create(asList(data));
    }
}
