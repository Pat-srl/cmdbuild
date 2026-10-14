package org.cmdbuild.sysparam;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Optional;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SysparamRepositoryImpl implements SysparamRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final CmCache<Optional<String>> paramsByCode;

    public SysparamRepositoryImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.paramsByCode = cacheService.newCache("sysparams_by_code");
    }

    @Override
    public String getParamOrNull(String key) {
        return paramsByCode.get(checkNotBlank(key), () -> Optional.ofNullable(dao.selectAll().from("_SysParam").where(ATTR_CODE, EQ, key).getCardOrNull()).map(c -> c.getString("Value"))).orElse(null);
    }

}
