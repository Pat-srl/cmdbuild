/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import org.cmdbuild.dao.beans.Item;
import java.util.List;

public interface ItemRepository {

    Item getItem(String type, long id);

    List<Item> getAllItemsForType(String type);

}
