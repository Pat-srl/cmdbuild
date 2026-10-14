/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formstructure;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Optional;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class FormStructureServiceImpl implements FormStructureService {

    private final DaoService dao;
    private final CmCache<Optional<FormStructure>> cache;

    public FormStructureServiceImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.cache = cacheService.newCache("form_structure_by_code", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public FormStructure getFormByCodeOrNull(String code) {
        checkNotBlank(code);
        return cache.get(code, () -> Optional.<FormStructureData>ofNullable(dao.selectAll().from(FormStructureData.class).where(ATTR_CODE, EQ, code).getOneOrNull()).map(fs -> new FormStructureImpl(fs.getData()))).orElse(null);
    }

    @Override
    public FormStructure createForm(String code, FormStructure form) {
        checkNotBlank(code);
        dao.createOnly(FormStructureDataImpl.builder().withCode(code).withData(form.getData()).build());
        cache.invalidate(code);
        return getFormByCode(code);
    }

    @Override
    public FormStructure updateForm(String code, FormStructure form) {
        checkNotBlank(code);
        FormStructureData card = dao.selectAll().from(FormStructureData.class).where(ATTR_CODE, EQ, code).getOne();
        dao.updateOnly(FormStructureDataImpl.copyOf(card).withData(form.getData()).build());
        cache.invalidate(code);
        return getFormByCode(code);
    }

    @Override
    public void deleteForm(String code) {
        dao.delete(dao.selectAll().from(FormStructureData.class).where(ATTR_CODE, EQ, code).getCard());
        cache.invalidate(code);
    }

}
