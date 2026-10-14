/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.services;

import org.cmdbuild.dao.orm.SetterContext;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.Item;
import org.cmdbuild.dao.orm.GetterContext;

public enum DummyGetterSetterContext implements SetterContext, GetterContext {

    INSTANCE;

    @Override
    public Item getItem(String type, long id) {
        throw new DaoException("embedded items not supported for this operation");
    }

    @Override
    public void addItem(Item item) {
        throw new DaoException("embedded items not supported for this operation");
    }

}
