/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.etl.config.WaterwayDescriptorMeta;
import org.cmdbuild.etl.config.WaterwayDescriptorRepository;
import org.cmdbuild.etl.config.WaterwayItem;
import org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.loadItems;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.loadPluginDescriptors;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.loadSystemDescriptors;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.prepareRecord;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.toRecord;
import org.cmdbuild.systemplugin.SystemPluginService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayDescriptorRepositoryImpl implements WaterwayDescriptorRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final SystemPluginService systemPluginService;
    private final Map<String, WaterwayDescriptorRecord> systemDescriptors;

    public WaterwayDescriptorRepositoryImpl(SystemPluginService systemPluginService, DaoService dao) {
        this.dao = checkNotNull(dao);
        this.systemPluginService = checkNotNull(systemPluginService);
        systemDescriptors = map(loadSystemDescriptors(), WaterwayDescriptorRecord::getCode).immutable();
        logger.info("system bus descriptors = {}", Joiner.on(", ").join(systemDescriptors.keySet()));
    }

    @Nullable
    @Override
    public WaterwayDescriptorRecord getDescriptorOrNull(String code) {
        return firstNotNullOrNull(dao.selectAll().from(WaterwayDescriptorRecord.class).where(ATTR_CODE, EQ, checkNotBlank(code)).getOneOrNull(), systemDescriptors.get(code), getPluginDescriptors().get(code));
    }

    @Override
    public WaterwayDescriptorRecord updateDescriptorMeta(String code, WaterwayDescriptorMeta meta) {
        WaterwayDescriptorRecord record = getDescriptor(code);
        return dao.update(toRecord(record.getData(), meta, record));
    }

    @Override
    public WaterwayDescriptorRecord createUpdateDescriptor(DataSource data, @Nullable WaterwayDescriptorMeta meta, boolean overwriteExisting) {
        WaterwayDescriptorRecord record = prepareRecord(data, meta, this::getDescriptorOrNull, this::getAllDescriptors);
        if (isNullOrLtEqZero(record.getId())) {
            record = dao.create(record);
        } else {
            checkArgument(overwriteExisting, "cannot overwrite descriptor with code =< %s >", record.getCode());
            record = dao.update(record);
        }
        return record;
    }

    @Override
    public List<WaterwayItem> getAllItems() {
        return loadItems(getAllDescriptors());
    }

    @Override
    public void deleteDescriptor(String code) {
        dao.delete(getDescriptor(code));
    }

    @Override
    public List<WaterwayDescriptorRecord> getAllDescriptors() {
        return list(map(systemDescriptors).with(getPluginDescriptors()).with(map(dao.selectAll().from(WaterwayDescriptorRecord.class).orderBy(ATTR_CODE).asList(WaterwayDescriptorRecord.class), WaterwayDescriptorRecord::getCode)).values())
                .map(WaterwayDescriptorUtils::validateRecord).sorted(WaterwayDescriptorRecord::getCode);
    }

    private Map<String, WaterwayDescriptorRecord> getPluginDescriptors() {
        return map(systemPluginService.getSystemPlugins().stream().flatMap(p -> loadPluginDescriptors(p.getResources("busdescriptors", "yml", "yaml")).stream()).collect(toList()), WaterwayDescriptorRecord::getCode).immutable();
    }
}
