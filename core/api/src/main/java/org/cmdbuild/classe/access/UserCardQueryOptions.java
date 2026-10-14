/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;

public interface UserCardQueryOptions {

    DaoQueryOptions getQueryOptions();

    @Nullable
    UserCardQueryForDomain getForDomain();

    @Nullable
    String getFunctionValue();

    default boolean hasFunctionValue() {
        return isNotBlank(getFunctionValue());
    }

    default boolean hasForDomain() {
        return getForDomain() != null;
    }

}

//            @Nullable String selectFunctionValue,
//            @Nullable Domain forDomain,
//            @Nullable RelationDirection forDomainDir,
//            @Nullable Long forDomainOriginId
