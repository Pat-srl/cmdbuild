/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.homepage.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.homepage.utils.HomepageConstants.DEFAULT_HOMEPAGE_GROUP_NAME;

import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.homepage.HomepageData;
import org.cmdbuild.homepage.HomepageRepository;
import org.cmdbuild.homepage.HomepageService;
import org.springframework.stereotype.Component;

@Component
public class HomepageServiceImpl implements HomepageService {

    private final HomepageRepository repository;
    private final OperationUserSupplier userStore;

    public HomepageServiceImpl(HomepageRepository repository, OperationUserSupplier userStore, CacheService cacheService) {
        this.repository = checkNotNull(repository);
        this.userStore = checkNotNull(userStore);
    }

    @Override
    public List<HomepageData> getAll() {
        return repository.getAll();
    }

    @Override
    public HomepageData getForCurrentUser() {
        return getForCurrentUserGroup();
    }

    @Override
    public HomepageData getActiveForCurrentUser() {
        return getForCurrentUserGroup().isActive() ? getForCurrentUserGroup() : null;
    }

    private HomepageData getForCurrentUserGroup() {
        OperationUser user = userStore.getUser();
        String defaultGroup = user.getDefaultGroupNameOrNull();
        HomepageData homepage = isBlank(defaultGroup) ? null : getHomepageByGroupOrNull(defaultGroup).stream().findFirst().orElse(null);
        if(homepage == null) {
            homepage = getHomepageByGroupOrNull(DEFAULT_HOMEPAGE_GROUP_NAME).stream().findFirst().orElse(null);
        }
        return homepage;
    }

    private List<HomepageData> getHomepageByGroupOrNull(String groupName) {
        return repository.getHomepageDataForGroupOrNull(groupName);
    }

    @Override
    public HomepageData getOne(long id) {
        return repository.getHomepageById(id);
    }

    @Override
    public HomepageData getOneForUser(long id) {
        HomepageData homepage = getOne(id);
        return homepage;
    }

    @Override
    public HomepageData update(HomepageData homepage) {
        if (homepage.getCode().equalsIgnoreCase("default")) {
            checkArgument(homepage.isActive(), "Cannot update homepage with code 'default' to inactive");;
        }
        return repository.updateHomepage(homepage);
    }

    @Override
    public void delete(long id) {
        checkArgument(!getOne(id).getCode().equalsIgnoreCase("default"), "Cannot delete homepage with code 'default'");
        repository.deleteHomepage(id);
    }

    @Override
    public boolean isActiveAndAccessibleByCode(String code) {
        HomepageData homepage = repository.getHomepageByCode(code);
        return homepage != null && homepage.isActive();
    }

    @Override
    public HomepageData getByCode(String code) {
        return repository.getHomepageByCode(code);
    }

    @Override
    public HomepageData getForUserByName(String code) {
        HomepageData homepage = getByCode(code);
        return homepage;
    }

    @Override
    public HomepageData create(HomepageData homepage) throws IllegalArgumentException {
        checkArgument(!homepage.getCode().equalsIgnoreCase("default"), "Cannot create homepage with code 'default'");
        return repository.createHomepage(homepage);
    }

    public HomepageData create(String groupName, String code, String description, String config, boolean isActive, String iconFont) throws IllegalArgumentException {
        HomepageData homepage = new HomepageDataImpl.HomepageDataImplBuilder()
                .withGroupName(groupName)
                .withCode(code)
                .withDescription(description)
                .withConfig(config)
                .withActive(isActive)
                .withIconFont(iconFont)
                .build();
        return create(homepage);
    }

}
