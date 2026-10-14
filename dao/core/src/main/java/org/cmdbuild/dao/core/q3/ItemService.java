/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.repository.ItemRepository;

public interface ItemService extends ItemRepository {

    <T> T getItem(Class<T> model, long id);

    <T> List<T> getAllItemsForType(Class<T> model);

    <T> Map<Long, T> getItemsForType(Class<T> model, List<Card> cards);

}
