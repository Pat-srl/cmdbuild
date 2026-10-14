/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.services;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.orm.CardMapperConfig;

public class CardMapperConfigImpl<T> implements CardMapperConfig<T> {

    private final Class<T> targetClass;

    public CardMapperConfigImpl(Class<T> targetClass) {
        this.targetClass = checkNotNull(targetClass);
    }

    @Override
    public Class<T> getTargetClass() {
        return targetClass;
    }
}
