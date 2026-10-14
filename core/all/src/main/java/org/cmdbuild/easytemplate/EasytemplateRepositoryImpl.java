package org.cmdbuild.easytemplate;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Optional;
import jakarta.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.easytemplate.EasytemplateImpl.TEMPLATE_NAME;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EasytemplateRepositoryImpl implements EasytemplateRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final CmCache<Optional<Easytemplate>> templatesByCode;

    public EasytemplateRepositoryImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.templatesByCode = cacheService.newCache("easytemplates_by_code");
    }

    @Override
    @Nullable
    public String getTemplateOrNull(String name) {
        return templatesByCode.get(checkNotBlank(name), () -> Optional.ofNullable(doGetTemplateOrNull(name))).map(Easytemplate::getValue).orElse(null);
    }

    @Nullable
    public Easytemplate doGetTemplateOrNull(String name) {
        return dao.selectAll().from(EasytemplateImpl.class).where(TEMPLATE_NAME, EQ, checkNotBlank(name)).asModelOrNull();
    }

    @Override
    public Easytemplate create(Easytemplate template) {
        template = dao.create(template);
        templatesByCode.invalidate(template.getKey());
        return template;
    }

}
