/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import org.cmdbuild.dao.beans.Item;
import org.cmdbuild.dao.driver.repository.ItemRepository;
import static org.cmdbuild.dao.utils.ItemUtils.parseItem;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
    }

    @Override
    public Item getItem(String type, long id) {
        checkNotBlank(type);
        checkNotNullAndGtZero(id);
        return parseItem(type, jdbcTemplate.queryForObject("SELECT _cm3_item_get(?)", String.class, id));
    }

    @Override
    public List<Item> getAllItemsForType(String type) {
        return jdbcTemplate.queryForList("SELECT item FROM _cm3_item_list(?) ORDER BY item_id", String.class, checkNotBlank(type)).stream().map(s -> parseItem(type, s)).collect(toImmutableList());
    }

}
