package org.cmdbuild.uicomponents.custompage;

import static java.util.Arrays.asList;
import java.util.List;
import jakarta.activation.DataHandler;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;

public interface CustomPageService {

    List<UiComponentInfo> getAll();

    List<UiComponentInfo> getAllForCurrentUser();

    List<UiComponentInfo> getActiveForCurrentUserAndDevice();

    UiComponentInfo get(long id);

    UiComponentInfo getForUser(long id);

    PrivilegeSubjectWithInfo getCustomPageAsPrivilegeSubjectById(long id);

    UiComponentInfo create(List<byte[]> data);

    UiComponentInfo createOrUpdate(List<byte[]> data);

    UiComponentInfo update(long id, List<byte[]> data);

    UiComponentInfo update(UiComponentInfo customPage);

    byte[] getCustomPageFile(String code, String path);

    DataHandler getCustomPageData(String code, TargetDevice targetDevice);

    void delete(long id);

    UiComponentInfo deleteForTargetDevice(long id, TargetDevice targetDevice);

    UiComponentInfo getByName(String code);

    boolean isActiveAndAccessibleByName(String code);

    default UiComponentInfo create(byte[]... data) {
        return create(asList(data));
    }
}
