package org.cmdbuild.uicomponents.admincustompage;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataHandler;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentConfiguration;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.data.UiComponentData;
import org.cmdbuild.uicomponents.data.UiComponentRepository;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_ADMINCUSTOMPAGE;
import org.cmdbuild.uicomponents.utils.UiComponentUtils;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.getAdminCustomPageCode;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.getComponentFile;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import org.springframework.stereotype.Component;

@Component
public class AdminCustomPageServiceImpl implements AdminCustomPageService {

    private final UiComponentRepository repository;
    private final OperationUserSupplier user;
    private final UiComponentConfiguration config;

    public AdminCustomPageServiceImpl(UiComponentRepository repository, OperationUserSupplier userStore, UiComponentConfiguration config) {
        this.repository = checkNotNull(repository);
        this.user = checkNotNull(userStore);
        this.config = checkNotNull(config);
    }

    @Override
    public List<UiComponentInfo> getAll() {
        return repository.getAllByType(UCT_ADMINCUSTOMPAGE).stream().map(UiComponentUtils::toComponentInfo).collect(toList());
    }

    @Override
    public List<UiComponentInfo> getAllByDevice() {
        TargetDevice targetDevice = user.getUser().getTargetDevice();
        return getAll().stream().filter(c -> c.allowsTargetDevice(targetDevice)).collect(toList());
    }

    @Override
    public UiComponentInfo getByName(String code) {
        UiComponentInfo customPage = getAll().stream().filter((i) -> i.getName().equals(code)).collect(onlyElement("admin custom page not found for name = %s", code));
        return customPage;
    }

    @Override
    public UiComponentInfo getByPluginOrNull(SystemPlugin plugin) {
        try {
            UiComponentInfo customPage = getAll().stream().filter((i) -> i.getDescription().equals(getAdminCustomPageCode(plugin.getName()))).collect(onlyElement("admin custom page not found for plugin = %s", plugin.getName()));
            return customPage;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public byte[] getCustomPageFile(String code, String path) {
        UiComponentData customPage = repository.getByTypeAndNameOrNull(UCT_ADMINCUSTOMPAGE, code);
        return doGetCustomPageFile(customPage, path, user.getUser().getTargetDevice());
    }

    @Override
    public DataHandler getCustomPageData(String code, TargetDevice targetDevice) {
        UiComponentData customPage = repository.getByTypeAndNameOrNull(UCT_ADMINCUSTOMPAGE, code);
        return newDataHandler(customPage.getData(targetDevice), "application/zip", format("%s.zip", normalize(customPage.getName())));
    }

    private byte[] doGetCustomPageFile(UiComponentData customPage, String path, TargetDevice targetDevice) {
        return getComponentFile(customPage, customPage.getData(targetDevice), path, config.isJsCompressionEnabled());
    }

}
