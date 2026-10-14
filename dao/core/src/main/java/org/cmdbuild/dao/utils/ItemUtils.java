/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.Item;
import org.cmdbuild.dao.beans.ItemImpl;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.JSON;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;

public class ItemUtils {

    public static Item parseItem(String data) {
        return parseItem(null, data);
    }

    public static Item parseItem(@Nullable String type, String data) {
        try {
            return parseItem(type, fromJson(data, MAP_OF_OBJECTS));
        } catch (Exception ex) {
            throw new DaoException(ex, "error reading item from data =< %s >", abbreviate(data));
        }
    }

    public static List<Item> parseItems(String data) {
        return fromJson(data, LIST_OF_MAP_OF_OBJECTS).stream().map(i -> parseItem(null, i)).collect(toImmutableList());
    }

    public static List<Item> getItems(List<Card> cards) {
        List<Item> list = list();
        cards.forEach(c -> c.getType().getAllAttributes().stream().filter(a -> a.isOfType(JSON) && a.getMetadata().isItems()).forEach(a -> {
            String data = c.getString(a.getName());
            if (isNotBlank(data)) {
                list.addAll(parseItems(data));
            }
        }));
        return list;
    }

    public static Item getItem(Card card, String type, long id) {
        return getItem(singletonList(card), type, id);
    }

    public static Item getItem(List<Card> cards, String type, long id) {
        checkNotBlank(type);
        checkNotNullAndGtZero(id);
        return getItems(cards).stream().filter(i -> equal(i.getTypeName(), type) && equal(i.getId(), id)).collect(onlyElement("item not found for type =< %s > id = %s", type, id));
    }

    private static Item parseItem(@Nullable String type, Map<String, Object> data) {
        Item item = new ItemImpl(data);
        checkArgument(item.hasId(), "invalid item, missing _id field");
        checkArgument(isBlank(type) || equal(item.getTypeName(), type), "invalid item, invalid _type field, expected =< %s > but found =< %s >", type, item.getTypeName());
        return item;
    }

}
