package org.cmdbuild.etl.waterway.sqlfunction;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Map;
import org.cmdbuild.dao.config.inner.FunctionCardRepository;
import static org.cmdbuild.dao.config.inner.FunctionManagerImpl.buildFunctionUpgradeSqlScript;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.StoredFunctionModifiedEvent;
import org.cmdbuild.dao.sql.utils.SqlFunction;
import static org.cmdbuild.dao.sql.utils.SqlFunctionUtils.readSqlFunctions;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_FUNCTION;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.PostStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayStoredFunctionLoader {

    private final static String BUS_DESCRIPTOR_FUNCTION_CATEGORY = "bus";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService service;
    private final FunctionCardRepository functionRepository;
    private final DaoService dao;
    private final EventBus eventBus;

    public WaterwayStoredFunctionLoader(DaoService dao, FunctionCardRepository functionRepository, WaterwayDescriptorService service, EventBusService busService) {
        this.service = checkNotNull(service);
        this.dao = checkNotNull(dao);
        this.functionRepository = checkNotNull(functionRepository);
        this.eventBus = busService.getDaoEventBus();
        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                reloadFunctions();
            }
        });
    }

    @PostStartup
    public void reloadFunctions() {
        logger.info("reload functions from bus descriptors");
        Map<String, SqlFunction> storedFunctions = functionRepository.getFunctions(BUS_DESCRIPTOR_FUNCTION_CATEGORY);
        service.getAllItemsEnabled(WYCIT_FUNCTION).forEach(i -> readSqlFunctions(i.getConfig("data")).forEach(f -> {
            logger.debug("processing stored function = {} from bus descriptor item = {}", f, i);
            if (!storedFunctions.containsKey(f.getSignature()) || !equal(f.getHash(), storedFunctions.get(f.getSignature()).getHash())) {
                logger.info("upgrade function = {}", f);
                dao.getJdbcTemplate().execute(buildFunctionUpgradeSqlScript(f));
                functionRepository.update(BUS_DESCRIPTOR_FUNCTION_CATEGORY, f);
                eventBus.post(StoredFunctionModifiedEvent.INSTANCE);
            }
        }));
    }

}
