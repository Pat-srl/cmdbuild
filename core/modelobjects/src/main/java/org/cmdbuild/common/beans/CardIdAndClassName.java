/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.beans;

import jakarta.annotation.Nullable;
import static org.cmdbuild.common.beans.CardIdAndClassNameUtils.serializeCardIdAndClassName;

public interface CardIdAndClassName extends IdAndDescription {

    @Override
    Long getId();

    String getClassName();

    @Override
    @Nullable
    default String getDescription() {
        return null;
    }

    @Override
    @Nullable
    default String getCode() {
        return null;
    }

    @Override
    default String getTypeName() {
        return getClassName();
    }

    default String serialize() {
        return serializeCardIdAndClassName(this);
    }

}
