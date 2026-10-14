/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.q3;

import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.utils.FilteringOptions;
import org.cmdbuild.dao.entrytype.Classe;

public interface DaoQueryOptions extends FilteringOptions {

    @Override
    DaoQueryOptions mapAttrNames(Map<String, String> map);

    @Override
    DaoQueryOptions withOffset(@Nullable Long offset);

    @Override
    DaoQueryOptions withoutPaging();

    DaoQueryOptions expandFulltextFilter(Classe userclass);
}
