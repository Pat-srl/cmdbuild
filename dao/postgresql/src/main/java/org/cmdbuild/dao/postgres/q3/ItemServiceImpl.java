/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.ItemService;
import org.cmdbuild.dao.beans.Item;
import org.cmdbuild.dao.driver.repository.ItemRepository;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.orm.CardMapperService;
import static org.cmdbuild.dao.utils.ItemUtils.getItems;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.springframework.stereotype.Component;

@Component
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final CardMapperService mapperService;

    public ItemServiceImpl(ItemRepository repository, CardMapperService mapper) {
        this.repository = checkNotNull(repository);
        this.mapperService = checkNotNull(mapper);
    }

    @Override
    public <T> T getItem(Class<T> model, long id) {
        CardMapper mapper = mapperService.getMapperForModel(model);
        Item item = getItem(mapper.getClassId(), id);
        return model.cast(mapper.dataToObject(item.getData()));
    }

    @Override
    public <T> List<T> getAllItemsForType(Class<T> model) {
        CardMapper mapper = mapperService.getMapperForModel(model);
        return getAllItemsForType(mapper.getClassId()).stream().map(i -> mapper.dataToObject(i.getData())).map(model::cast).collect(toImmutableList());
    }

    @Override
    public Item getItem(String type, long id) {
        return repository.getItem(type, id);
    }

    @Override
    public List<Item> getAllItemsForType(String type) {
        return repository.getAllItemsForType(type);
    }

    @Override
    public <T> Map<Long, T> getItemsForType(Class<T> model, List<Card> cards) {
        CardMapper mapper = mapperService.getMapperForModel(model);
        return getItems(cards).stream().filter(i -> equal(mapper.getClassId(), i.getTypeName())).collect(toMap(Item::getId, i -> model.cast(mapper.dataToObject(i.getData()))));
    }

}
