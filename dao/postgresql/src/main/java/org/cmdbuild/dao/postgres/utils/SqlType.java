/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.utils;

import java.util.List;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public interface SqlType {

    SqlTypeName getType();

    List<String> getParams();

    AttributeMetadata getMetadata();

    boolean hasSqlCast();

    String getSqlCast();

    String toSqlTypeString();

    CardAttributeType<?> toAttributeType();

    default String getSqlType() {
        return getType().name();
    }

    default boolean hasParams() {
        return !getParams().isEmpty();
    }

    default boolean isOfType(SqlTypeName... types) {
        return set(types).contains(getType());
    }
}
