/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.repository;

import org.cmdbuild.dao.entrytype.AttributeGroupImpl;
import org.cmdbuild.dao.driver.repository.AttributeGroupRepository;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import jakarta.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.event.AttributeGroupModifiedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.entrytype.AttributeGroupData;
import org.cmdbuild.dao.entrytype.EntryTypeType;
import org.cmdbuild.dao.event.AttributeStructureChangedEvent;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToEntryTypeName;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import org.springframework.context.annotation.Primary;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.serializeListOfStrings;

@Component
@Primary
public class AttributeGroupRepositoryImpl implements AttributeGroupRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EventBus eventBus;
    private final Holder<List<AttributeGroupData>> allAttributeGroups;
    private final CmCache<Optional<AttributeGroupData>> attributeGroupsByKey;
    private final CmCache<List<AttributeGroupData>> attributeGroupsByOwner;

    public AttributeGroupRepositoryImpl(JdbcTemplate jdbcTemplate, CacheService cacheService, EventBusService eventService) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        allAttributeGroups = cacheService.newHolder("all_attribute_groups", CacheConfig.SYSTEM_OBJECTS);
        attributeGroupsByKey = cacheService.newCache("attribute_groups_by_key", CacheConfig.SYSTEM_OBJECTS);
        attributeGroupsByOwner = cacheService.newCache("attribute_groups_by_owner", CacheConfig.SYSTEM_OBJECTS);
        eventBus = eventService.getDaoEventBus();
        eventBus.register(new Object() {

            @Subscribe
            public void handleClassCreatedEvent(ClassStructureChangedEvent event) {//TODO improve this, intercept only class create ev.
                invalidateCache();
            }

            @Subscribe
            public void handleAttributeStructureChangedEvent(AttributeStructureChangedEvent event) {//TODO improve this 
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        allAttributeGroups.invalidate();
        attributeGroupsByKey.invalidateAll();
        attributeGroupsByOwner.invalidateAll();
    }

    @Override
    public List<AttributeGroupData> getAll() {
        return allAttributeGroups.get(this::doGetAll);
    }

    @Override
    @Nullable
    public AttributeGroupData getOrNull(String ownerName, EntryTypeType ownerType, String groupId) {
        return attributeGroupsByKey.get(key(ownerName, ownerType, groupId), () -> {
            return getAll().stream().filter((a) -> equal(a.getOwnerName(), ownerName) && equal(a.getOwnerType(), ownerType) && equal(a.getName(), groupId)).collect(toOptional());
        }).orElse(null);
    }

    @Override
    public AttributeGroupData create(AttributeGroupData group) {
        jdbcTemplate.update("INSERT INTO \"_AttributeGroup\" (\"Code\",\"Description\",\"Owner\",\"Index\",\"Config\") VALUES (?,?,?,?,?::jsonb)",
                group.getName(), group.getDescription(), group.getOwnerName(), group.getIndex(), toJson(group.getConfig()));
        invalidateCache();
        eventBus.post(AttributeGroupModifiedEvent.INSTANCE);
        return get(group.getOwnerName(), group.getOwnerType(), group.getName());
    }

    @Override
    public AttributeGroupData update(AttributeGroupData group) {
        jdbcTemplate.update("UPDATE \"_AttributeGroup\" SET \"Description\" = ?, \"Index\" = ?, \"Config\" = ?::jsonb WHERE \"Code\" = ? AND \"Owner\" = ? AND \"Status\" = 'A'",
                group.getDescription(), group.getIndex(), toJson(group.getConfig()), group.getName(), group.getOwnerName());
        invalidateCache();
        eventBus.post(AttributeGroupModifiedEvent.INSTANCE);
        return get(group.getOwnerName(), group.getOwnerType(), group.getName());
    }

    @Override
    public List<AttributeGroupData> getAttributeGroupsForEntryType(String ownerName, EntryTypeType ownerType) {
        return attributeGroupsByOwner.get(key(ownerName, ownerType), () -> doGetAttributeGroupsForEntryType(ownerName, ownerType));
    }

    @Override
    public void delete(AttributeGroupData group) {
        jdbcTemplate.update("UPDATE \"_AttributeGroup\" SET \"Status\" = 'N' WHERE \"Code\" = ? AND \"Owner\" = ? AND \"Status\" = 'A'", group.getName(), group.getOwnerName());
        invalidateCache();
        eventBus.post(AttributeGroupModifiedEvent.INSTANCE);
    }

    private List<AttributeGroupData> doGetAttributeGroupsForEntryType(String ownerName, EntryTypeType ownerType) {
        return getAll().stream().filter((a) -> equal(a.getOwnerName(), ownerName) && equal(a.getOwnerType(), ownerType)).collect(toImmutableList());
    }

    private List<AttributeGroupData> doGetAll() {
        return jdbcTemplate.query("WITH q AS (SELECT \"Code\",\"Description\",\"Owner\" _owner_name, _cm3_class_type_get(_cm3_utils_name_to_regclass(\"Owner\")) _owner_type, \"Index\", \"Config\" FROM \"_AttributeGroup\" WHERE \"Status\" = 'A') SELECT * FROM q ORDER BY _owner_name, _owner_type, \"Index\"", (ResultSet rs, int rowNum) -> {
            EntryTypeType ownerType = parseEnum(rs.getString("_owner_type"), EntryTypeType.class);
            return AttributeGroupImpl.builder()
                    .withName(rs.getString("Code"))
                    .withDescription(rs.getString("Description"))
                    .withIndex(rs.getInt("Index"))
                    .withOwnerType(ownerType)
                    .withOwnerName(sqlTableToEntryTypeName(rs.getString("_owner_name"), ownerType))
                    .withConfig(fromJson(rs.getString("Config"), MAP_OF_STRINGS))
                    .build();
        });
    }

}
