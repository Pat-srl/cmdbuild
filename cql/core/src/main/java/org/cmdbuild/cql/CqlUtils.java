/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cql;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cmdbuild.cql.compiler.CQLCompiler;
import org.cmdbuild.cql.compiler.CQLCompilerListener;
import org.cmdbuild.cql.compiler.impl.ClassDeclarationImpl;
import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;
import org.cmdbuild.cql.compiler.impl.FactoryImpl;
import org.cmdbuild.cql.compiler.impl.SelectImpl;
import org.cmdbuild.cql.compiler.select.FieldSelect;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;

public class CqlUtils {

    public static CqlQueryImpl compileAndCheck(String expr) {
        try {
            CQLCompiler compiler = new CQLCompiler();
            CQLCompilerListener listener = new CQLCompilerListener();
            listener.setFactory(new FactoryImpl());
            FactoryImpl.CmdbuildCheck = true;

            compiler.compile(expr, listener);

            CqlQueryImpl compiled = (CqlQueryImpl) listener.getRootQuery();
            compiled.check();
            return compiled;
        } catch (Exception ex) {
            throw new EcqlException(ex, "error processing cql expr = '%s'", abbreviate(expr));
        }
    }

    public static CqlQueryImpl compileWithTemplateParams(String cqlQueryTemplate) throws Exception {
        String compilableCqlQuery = substituteCqlVariableNames(cqlQueryTemplate);
        return compileAndCheck(compilableCqlQuery);
    }

    public static List<String> getCqlSelectElements(String expr) {
        CqlQueryImpl query = compileAndCheck(expr);
        ClassDeclarationImpl mainClass = query.getFrom().mainClass();
        SelectImpl select = query.getSelect();
        if (select.isDefault()) {
            return emptyList();
        } else {
            return list(select.get(mainClass).getElements()).filter(FieldSelect.class).map(FieldSelect::getName);
        }
    }

    public static String getCqlSingleSelectElement(String expr) {
        return getOnlyElement(getCqlSelectElements(expr));
    }

    /*
	 * {ns:varname} is not parsable, so we need to substitute them with fake
	 * ones to parse the CQL query string
     */
    private static String substituteCqlVariableNames(String cqlQuery) {
        Pattern r = Pattern.compile("\\{[^\\{\\}]+\\}");
        Matcher m = r.matcher(cqlQuery);
        return m.replaceAll("{fake}");
    }
}
