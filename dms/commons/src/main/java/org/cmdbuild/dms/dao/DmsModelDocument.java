/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;

public interface DmsModelDocument extends DocumentInfoAndDetail {

    @Nullable
    Long getId();

    long getCardId();

    @Override
    default boolean hasContent() {
        return isNotBlank(getHash());
    }

    @Override
    @Nullable
    public default Card getMetadata() {
        return null;
    }

}
