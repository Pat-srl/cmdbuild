/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents.contextmenu;

import static java.util.Arrays.asList;
import java.util.List;
import jakarta.activation.DataHandler;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;

public interface ContextMenuComponentService {

    List<UiComponentInfo> getAll();

    List<UiComponentInfo> getAllActive();

    UiComponentInfo get(long id);

    UiComponentInfo getByCode(String componentId);

    void delete(long id);

    UiComponentInfo deleteForTargetDevice(long id, TargetDevice targetDevice);

    UiComponentInfo createOrUpdate(List<byte[]> toByteArray);

    UiComponentInfo create(List<byte[]> toByteArray);

    UiComponentInfo update(long id, List<byte[]> toByteArray);

    UiComponentInfo update(UiComponentInfo customPage);

    byte[] getContextMenuFile(String contextMenuName, String filePath);

    DataHandler getContextMenuData(String code, TargetDevice targetDevice);

    default UiComponentInfo create(byte[]... data) {
        return create(asList(data));
    }

}
