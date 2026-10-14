/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import org.springframework.stereotype.Component;

@Component
public class GisStyleRulesRepositoryImpl implements GisStyleRulesRepository {

    private final DaoService dao;

    public GisStyleRulesRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public GisStyleRulesetData create(GisStyleRulesetData rules) {
        return dao.create(rules);
    }

    @Override
    public GisStyleRulesetData update(GisStyleRulesetData rules) {
        return dao.update(rules);
    }

    @Override
    public List<GisStyleRulesetData> getAll() {
        return dao.selectAll().from(GisStyleRulesetData.class).orderBy("Owner", ASC, ATTR_CODE, ASC).asList();
    }

    @Override
    public void delete(long id) {
        dao.delete(GisStyleRulesetData.class, id);
    }

}
