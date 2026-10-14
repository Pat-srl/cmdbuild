/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.homepage.inner;

import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.homepage.HomepageData;
import org.cmdbuild.homepage.HomepageRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class HomepageRepositoryImpl implements HomepageRepository {

    private final DaoService dao;

    public HomepageRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public List<HomepageData> getAll() {
        return dao.selectAll().from(HomepageData.class).asList();
    }

    @Override
    public HomepageData getHomepageById(long id) {
        return dao.getById(HomepageData.class, id);
    }

    @Override
    public HomepageData createHomepage(HomepageData homepage) {
        return dao.create(homepage);
    }

    @Override
    public HomepageData updateHomepage(HomepageData homepage) {
        return dao.update(homepage);
    }

    @Override
    @Nullable
    public List<HomepageData> getHomepageDataForGroupOrNull(String groupName) {
        return dao.selectAll().from(HomepageData.class).where("Active", EQ, true).where("GroupName", EQ, checkNotBlank(groupName)).asList();
    }

    @Override
    public void deleteHomepage(long id) {
        dao.delete(HomepageData.class, id);
    }

    @Override
    public HomepageData getHomepageByCode(String code) {
        return dao.selectAll().from(HomepageData.class).where(ATTR_CODE, EQ, checkNotBlank(code)).getOne();
    }

}
