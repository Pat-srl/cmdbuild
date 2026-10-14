/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.ecql.EcqlExpression;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EcqlExpressionImpl implements EcqlExpression {

    private final String ecql;
    private final Map<String, Object> context;

    public EcqlExpressionImpl(String ecql) {
        this(ecql, emptyMap());
    }

    public EcqlExpressionImpl(String ecql, Map<String, Object> context) {
        this.ecql = checkNotBlank(ecql, "ecql expression is null or blank");
        this.context = map(checkNotNull(context)).immutable();
    }

    @Override
    public String getEcql() {
        return ecql;
    }

    @Override
    public Map<String, Object> getContext() {
        return context;
    }

}
