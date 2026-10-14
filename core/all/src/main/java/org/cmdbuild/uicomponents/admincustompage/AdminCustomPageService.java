package org.cmdbuild.uicomponents.admincustompage;

import jakarta.activation.DataHandler;
import java.util.List;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;

public interface AdminCustomPageService {

    List<UiComponentInfo> getAll();

    List<UiComponentInfo> getAllByDevice();

    byte[] getCustomPageFile(String code, String path);

    DataHandler getCustomPageData(String code, TargetDevice targetDevice);

    UiComponentInfo getByName(String code);

    UiComponentInfo getByPluginOrNull(SystemPlugin plugin);
}
