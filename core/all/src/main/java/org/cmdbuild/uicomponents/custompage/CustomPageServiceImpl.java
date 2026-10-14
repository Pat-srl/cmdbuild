package org.cmdbuild.uicomponents.custompage;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataHandler;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.authorization.CustomPageAsPrivilegeSubject;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentConfiguration;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.data.UiComponentData;
import org.cmdbuild.uicomponents.data.UiComponentDataImpl;
import org.cmdbuild.uicomponents.data.UiComponentRepository;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_CUSTOMPAGE;
import org.cmdbuild.uicomponents.utils.UiComponentUtils;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.checkUglifyJs;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.getCodeFromExtComponentData;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.getComponentFile;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.normalizeComponentData;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomPageServiceImpl implements CustomPageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UiComponentRepository repository;
    private final OperationUserSupplier user;
    private final Holder<List<UiComponentInfo>> allInfo;
    private final CmCache<UiComponentInfo> infoById, infoByName;
    private final CmCache<byte[]> processedFileByIdPath;
    private final UiComponentConfiguration config;

    public CustomPageServiceImpl(UiComponentRepository repository, OperationUserSupplier userStore, CacheService cacheService, UiComponentConfiguration config) {
        this.repository = checkNotNull(repository);
        this.user = checkNotNull(userStore);
        this.config = checkNotNull(config);
        allInfo = cacheService.newHolder("custom_page_all_info", CacheConfig.SYSTEM_OBJECTS);
        infoById = cacheService.newCache("custom_page_info_by_id", CacheConfig.SYSTEM_OBJECTS);
        infoByName = cacheService.newCache("custom_page_info_by_name", CacheConfig.SYSTEM_OBJECTS);
        processedFileByIdPath = cacheService.newCache("custom_page_file_by_id_path", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateCache() {
        allInfo.invalidate();
        infoById.invalidateAll();
        infoByName.invalidateAll();
        processedFileByIdPath.invalidateAll();
    }

    @ConfigListener(UiComponentConfiguration.class)
    public void handleConfigReload() {
        processedFileByIdPath.invalidateAll();
    }

    @Override
    public List<UiComponentInfo> getAll() {
        return allInfo.get(this::doGetAll);
    }

    @Override
    public List<UiComponentInfo> getAllForCurrentUser() {
        return getAll().stream().filter(this::canRead).collect(toList());
    }

    @Override
    public List<UiComponentInfo> getActiveForCurrentUserAndDevice() {
        TargetDevice targetDevice = user.getUser().getTargetDevice();
        return getAllForCurrentUser().stream().filter(UiComponentInfo::isActive).filter(c -> c.allowsTargetDevice(targetDevice)).collect(toList());
    }

    @Override
    public boolean isActiveAndAccessibleByName(String code) {
        UiComponentInfo customPage = doGetByName(code);
        return customPage.isActive() && canRead(customPage);
    }

    @Override
    public UiComponentInfo get(long id) {
        UiComponentInfo customPage = doGetById(id);
        return customPage;
    }

    @Override
    public UiComponentInfo getForUser(long id) {
        UiComponentInfo customPage = doGetById(id);
        checkArgument(canRead(customPage), "unable to access custom page = %s: permission denied", id);
        return customPage;
    }

    @Override
    public PrivilegeSubjectWithInfo getCustomPageAsPrivilegeSubjectById(long id) {
        return new CustomPageAsPrivilegeSubject(doGetById(id));
    }

    @Override
    public UiComponentInfo getByName(String code) {
        UiComponentInfo customPage = doGetByName(code);
        return customPage;
    }

    @Override
    public UiComponentInfo create(List<byte[]> data) {
        UiComponentData customPage = UiComponentDataImpl.builder().withData(normalizeComponentData(data)).withName(getCodeFromExtComponentData(data)).withType(UCT_CUSTOMPAGE).build();
        checkUglifyJs(customPage, config.isJsCompressionEnabled());
        checkArgument(repository.getByTypeAndNameOrNull(UCT_CUSTOMPAGE, customPage.getName()) == null, "cannot create custom page with name = '%s', a custom page with this name already exists", customPage.getName());
        return create(customPage);
    }

    @Override
    public UiComponentInfo createOrUpdate(List<byte[]> data) {
        UiComponentData current = repository.getByTypeAndNameOrNull(UCT_CUSTOMPAGE, getCodeFromExtComponentData(data));
        if (current == null) {
            return create(data);
        } else {
            return update(current.getId(), data);
        }
    }

    @Override
    public UiComponentInfo update(long id, List<byte[]> data) {
        UiComponentData current = repository.getById(id);
        String name = getCodeFromExtComponentData(data);
        checkArgument(equal(name, current.getName()), "uploaded custom page name = '%s' does not match name = '%s' of this custom page with id = %s", name, current.getName(), id);
        Map<TargetDevice, byte[]> mergedData = current.getData();
        mergedData.putAll(normalizeComponentData(data));
        UiComponentData newData = UiComponentDataImpl.copyOf(current).withData(mergedData).build();
        checkUglifyJs(newData, config.isJsCompressionEnabled());
        return update(newData);
    }

    @Override
    public UiComponentInfo update(UiComponentInfo customPage) {
        UiComponentData data = repository.getById(customPage.getId());
        data = UiComponentDataImpl.copyOf(data)
                .withDescription(customPage.getDescription())//description is the only mutable attr
                .withActive(customPage.isActive())
                .withIconFont(customPage.getIconFont())
                .build();
        return update(data);
    }

    @Override
    public void delete(long id) {
        repository.delete(id);
        invalidateCache();
    }

    @Override
    public UiComponentInfo deleteForTargetDevice(long id, TargetDevice targetDevice) {
        repository.update(UiComponentDataImpl.copyOf(repository.getById(id)).withoutDataForTargetDevice(targetDevice).build());
        invalidateCache();
        return get(id);
    }

    @Override
    public byte[] getCustomPageFile(String code, String path) {
        UiComponentData customPage = repository.getByTypeAndNameOrNull(UCT_CUSTOMPAGE, code);
        checkArgument(canRead(customPage), "unable to access custom page = %s: permission denied", code);
        return processedFileByIdPath.get(key(String.valueOf(customPage.getId()), path, user.getUser().getTargetDevice()), () -> doGetCustomPageFile(customPage, path, user.getUser().getTargetDevice()));
    }

    @Override
    public DataHandler getCustomPageData(String code, TargetDevice targetDevice) {
        UiComponentData customPage = repository.getByTypeAndNameOrNull(UCT_CUSTOMPAGE, code);
        return newDataHandler(customPage.getData(targetDevice), "application/zip", format("%s.zip", normalize(customPage.getName())));
    }

    private List<UiComponentInfo> doGetAll() {
        return repository.getAllByType(UCT_CUSTOMPAGE).stream().map(UiComponentUtils::toComponentInfo).collect(toList());
    }

    private UiComponentInfo doGetByName(String code) {
        return infoByName.get(code, () -> doReadOne(code));
    }

    private UiComponentInfo doGetById(long id) {
        return infoById.get(id, () -> doReadOne(id));
    }

    private UiComponentInfo update(UiComponentData data) {
        logger.info("update custom page = {}", data);
        repository.update(data);
        invalidateCache();
        return getForUser(data.getId());
    }

    private UiComponentInfo doReadOne(long id) {
        try {
            return getAll().stream().filter((i) -> i.getId() == id).collect(onlyElement());
        } catch (Exception ex) {
            throw new CustomPageException(ex, "custom page not found for id = %s", id);
        }
    }

    private boolean canRead(UiComponentInfo value) {
        return canRead(new CustomPageAsPrivilegeSubject(value));
    }

    private boolean canRead(UiComponentData value) {
        return canRead(new CustomPageAsPrivilegeSubject(value));
    }

    private boolean canRead(CustomPageAsPrivilegeSubject value) {
        return user.getUser().getPrivilegeContext().hasReadAccess(value);
    }

    private UiComponentInfo create(UiComponentData customPage) {
        logger.info("create custom page = {}", customPage);
        customPage = repository.create(customPage);
        invalidateCache();
        return getForUser(customPage.getId());
    }

    private UiComponentInfo doReadOne(String code) {
        checkNotBlank(code);
        return getAll().stream().filter((i) -> i.getName().equals(code)).collect(onlyElement("custom page not found for name = %s", code));
    }

    private byte[] doGetCustomPageFile(UiComponentData customPage, String path, TargetDevice targetDevice) {
        return getComponentFile(customPage, customPage.getData(targetDevice), path, config.isJsCompressionEnabled());
    }

}
