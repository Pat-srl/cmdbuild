package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.cql.CqlUtils.getCqlSingleSelectElement;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import org.springframework.stereotype.Component;

@Component
public class EasytemplateCqlResolver implements Function<String, Object> {

    private final DaoService dao;

    public EasytemplateCqlResolver(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public Object apply(String expression) {
        String name = getCqlSingleSelectElement(expression);
        Card card = dao.selectAll().where(CqlFilterImpl.build(expression).toCmdbFilter()).getCard();
        if (equal(ATTR_ID, name)) {
            return card.getId();
        } else {
            return card.get(name);
        }
    }

}
