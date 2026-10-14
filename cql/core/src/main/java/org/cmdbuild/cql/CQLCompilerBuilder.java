package org.cmdbuild.cql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.runtime.tree.Tree;
import org.cmdbuild.cql.CQLBuilderListener.FieldValueType;
import static org.cmdbuild.cql.CQLParser.ALL;
import static org.cmdbuild.cql.CQLParser.AND;
import static org.cmdbuild.cql.CQLParser.ASC;
import static org.cmdbuild.cql.CQLParser.ATTRIBUTE;
import static org.cmdbuild.cql.CQLParser.ATTRIBUTEAS;
import static org.cmdbuild.cql.CQLParser.ATTRIBUTENAME;
import static org.cmdbuild.cql.CQLParser.ATTRIBUTES;
import static org.cmdbuild.cql.CQLParser.BGN;
import static org.cmdbuild.cql.CQLParser.BTW;
import static org.cmdbuild.cql.CQLParser.CLASS;
import static org.cmdbuild.cql.CQLParser.CLASSALIAS;
import static org.cmdbuild.cql.CQLParser.CLASSDOMREF;
import static org.cmdbuild.cql.CQLParser.CLASSID;
import static org.cmdbuild.cql.CQLParser.CLASSREF;
import static org.cmdbuild.cql.CQLParser.CONT;
import static org.cmdbuild.cql.CQLParser.DEFAULT;
import static org.cmdbuild.cql.CQLParser.DESC;
import static org.cmdbuild.cql.CQLParser.DOM;
import static org.cmdbuild.cql.CQLParser.DOMCARDS;
import static org.cmdbuild.cql.CQLParser.DOMID;
import static org.cmdbuild.cql.CQLParser.DOMMETA;
import static org.cmdbuild.cql.CQLParser.DOMNAME;
import static org.cmdbuild.cql.CQLParser.DOMOBJS;
import static org.cmdbuild.cql.CQLParser.DOMREF;
import static org.cmdbuild.cql.CQLParser.DOMTYPE;
import static org.cmdbuild.cql.CQLParser.DOMVALUE;
import static org.cmdbuild.cql.CQLParser.END;
import static org.cmdbuild.cql.CQLParser.EQ;
import static org.cmdbuild.cql.CQLParser.EXPR;
import static org.cmdbuild.cql.CQLParser.FIELD;
import static org.cmdbuild.cql.CQLParser.FIELDID;
import static org.cmdbuild.cql.CQLParser.FIELDOPERATOR;
import static org.cmdbuild.cql.CQLParser.FIELDVALUE;
import static org.cmdbuild.cql.CQLParser.FROM;
import static org.cmdbuild.cql.CQLParser.FUNCTION;
import static org.cmdbuild.cql.CQLParser.GROUP;
import static org.cmdbuild.cql.CQLParser.GROUPBY;
import static org.cmdbuild.cql.CQLParser.GT;
import static org.cmdbuild.cql.CQLParser.GTEQ;
import static org.cmdbuild.cql.CQLParser.HISTORY;
import static org.cmdbuild.cql.CQLParser.IN;
import static org.cmdbuild.cql.CQLParser.INPUTVAL;
import static org.cmdbuild.cql.CQLParser.ISNOTNULL;
import static org.cmdbuild.cql.CQLParser.ISNULL;
import static org.cmdbuild.cql.CQLParser.LIMIT;
import static org.cmdbuild.cql.CQLParser.LITBOOL;
import static org.cmdbuild.cql.CQLParser.LITDATE;
import static org.cmdbuild.cql.CQLParser.LITNUM;
import static org.cmdbuild.cql.CQLParser.LITSTR;
import static org.cmdbuild.cql.CQLParser.LITTIMESTAMP;
import static org.cmdbuild.cql.CQLParser.LOOKUP;
import static org.cmdbuild.cql.CQLParser.LOOKUPPARENT;
import static org.cmdbuild.cql.CQLParser.LT;
import static org.cmdbuild.cql.CQLParser.LTEQ;
import static org.cmdbuild.cql.CQLParser.NATIVE;
import static org.cmdbuild.cql.CQLParser.NOT;
import static org.cmdbuild.cql.CQLParser.NOTBGN;
import static org.cmdbuild.cql.CQLParser.NOTBTW;
import static org.cmdbuild.cql.CQLParser.NOTCONT;
import static org.cmdbuild.cql.CQLParser.NOTEND;
import static org.cmdbuild.cql.CQLParser.NOTEQ;
import static org.cmdbuild.cql.CQLParser.NOTGROUP;
import static org.cmdbuild.cql.CQLParser.NOTIN;
import static org.cmdbuild.cql.CQLParser.OFFSET;
import static org.cmdbuild.cql.CQLParser.OR;
import static org.cmdbuild.cql.CQLParser.ORDERBY;
import static org.cmdbuild.cql.CQLParser.SELECT;
import static org.cmdbuild.cql.CQLParser.TRUE;
import static org.cmdbuild.cql.CQLParser.WHERE;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provide a common event-based abstraction to a CQL compiler. <br>
 * It takes a CommonTree expression representation (that comes from the
 * CQLParser) and navigates through the tree, and emits events based on the tree
 * being analyzed.
 *
 */
public class CQLCompilerBuilder {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private CQLBuilderListener listener;

    private final Set<String> declaredClassesDomains = new HashSet<>();

    void addDeclaration(String... names) {
        for (String name : names) {
            if (name != null) {
                declaredClassesDomains.add(name);
            }
        }
    }

    boolean hasDeclaration(String name) {
        return declaredClassesDomains.contains(name);
    }

    private interface TreeWork {

        void work(Tree tree);
    }

    private class HandleFields implements TreeWork {

        boolean first = true;

        @Override
        public void work(Tree child) {
            if (first) {
                // field, group or domain
                handleElement(child, CQLBuilderListener.WhereType.FIRST);
                first = false;
            } else {
                // and, or
                switch (child.getType()) {
                    case AND ->
                        handleElement(child.getChild(0), CQLBuilderListener.WhereType.AND);
                    case OR ->
                        handleElement(child.getChild(0), CQLBuilderListener.WhereType.OR);
                    default ->
                        logUnknownTree(child);
                }
            }
        }

        private void handleElement(Tree child, CQLBuilderListener.WhereType type) {
            logger.debug("handle element " + child.getType() + ", type: " + type.name());
            switch (child.getType()) {
                case GROUP -> {
                    listener.startGroup(type, false);
                    withChildren(child, new HandleFields());
                    listener.endGroup();
                }
                case NOTGROUP -> {
                    listener.startGroup(type, true);
                    withChildren(child, new HandleFields());
                    listener.endGroup();
                }
                case FIELD ->
                    whereField(child, type);
                case DOM ->
                    whereDomain(child, type);
                case DOMREF ->
                    whereDomainRef(child, type);
                default ->
                    logUnknownTree(child);
            }
        }
    }

    public void setCQLBuilderListener(CQLBuilderListener listener) {
        this.listener = listener;
    }

    private void withChildren(Tree parent, TreeWork work) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            work.work(parent.getChild(i));
        }
    }

    public Tree firstChild(Tree parent, int type) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChild(i).getType() == type) {
                return parent.getChild(i);
            }
        }
        return null;
    }

    public String subtext(Tree tree) {
        return tree == null ? null : (tree.getChildCount() == 0 ? null : tree.getChild(0).getText());// tree.getText();
    }

    public String text(Tree tree) {
        return tree == null ? null : tree.getText();
    }

    private void logUnknownTree(Tree t) {
        logger.warn(marker(), "cql parsing error: unknown tree: '{}'", t.getText());
    }

    public void compile(Tree root) {
        this.declaredClassesDomains.clear();
        logger.debug("Start compilation");
        listener.globalStart();
        expression(root);
        logger.debug("Compilation ended.");
        listener.globalEnd();
    }

    public void expression(Tree expr) {
        logger.debug("Start Expression");

        listener.startExpression();

        Tree select, from, where, order, group, limit, offset;

        from = firstChild(expr, FROM);
        select = firstChild(expr, SELECT);
        where = firstChild(expr, WHERE);
        order = firstChild(expr, ORDERBY);
        group = firstChild(expr, GROUPBY);
        limit = firstChild(expr, LIMIT);
        offset = firstChild(expr, OFFSET);

        boolean history = null != firstChild(expr, HISTORY);
        // FROM statement is the only one required.
        from(from, history);

        if (select == null) {
            listener.defaultSelect();
        } else {
            select(select);
        }
        if (where == null) {
            listener.defaultWhere();
        } else {
            where(where);
        }
        if (order == null) {
            listener.defaultOrderBy();
        } else {
            order(order);
        }
        if (group == null) {
            listener.defaultGroupBy();
        } else {
            group(group);
        }
        if (limit == null) {
            listener.defaultLimit();
        } else {
            limit(limit);
        }
        if (offset == null) {
            listener.defaultOffset();
        } else {
            offset(offset);
        }

        logger.debug("End Expression");
        listener.endExpression();
    }

    public void from(Tree from, boolean history) {
        logger.debug("Start From");
        listener.startFrom(history);
        withChildren(from, child -> {
            switch (child.getType()) {
                case CLASSREF -> {
                    String calias = subtext(firstChild(child, CLASSALIAS));
                    String cname = subtext(firstChild(child, CLASS));
                    String cid = subtext(firstChild(child, CLASSID));
                    addDeclaration(cname, calias);
                    if (cid != null) {
                        int classId = Integer.parseInt(cid);
                        listener.addFromClass(classId, calias);
                    } else {
                        listener.addFromClass(cname, calias);
                    }
                }
                case DOM ->
                    fromDomain(child);
                default ->
                    logUnknownTree(child);
            }
        });
        logger.debug("End From");
        listener.endFrom();
    }

    private void fromDomain(Tree tree) {
        String classScope = subtext(firstChild(tree, CLASSDOMREF));
        Tree domType = firstChild(tree, DOMTYPE);
        CQLBuilderListener.DomainDirection dir = null == firstChild(domType, DEFAULT) ? CQLBuilderListener.DomainDirection.DEFAULT : CQLBuilderListener.DomainDirection.INVERSE;
        String domName = subtext(firstChild(tree, DOMNAME));
        String domId = subtext(firstChild(tree, DOMID));
        String dalias = subtext(firstChild(tree, DOMREF));

        addDeclaration(domName, dalias);
        if (domId != null) {
            long domainId = Long.parseLong(domId);
            listener.startFromDomain(classScope, domainId, dalias, dir);
        } else {
            listener.startFromDomain(classScope, domName, dalias, dir);
        }
        Tree subDomain = firstChild(tree, DOMCARDS);
        if (subDomain != null) {
            fromDomain(subDomain.getChild(0));
        }
        listener.endFromDomain();
    }

    public void select(Tree select) {
        logger.debug("Start Select");
        listener.startSelect();

        if (null != firstChild(select, ALL)) {
            listener.selectAll();
        } else {
            withChildren(select, child -> {
                switch (child.getType()) {
                    case CLASSREF -> {
                        String classRef = subtext(child);
                        listener.startSelectFromClass(classRef);
                        withChildren(firstChild(child, ATTRIBUTES), (Tree attr) -> {
                            selectAttribute(attr);
                        });
                        listener.endSelectFromClass();
                    }
                    case DOMREF -> {
                        String domRef = subtext(child);
                        listener.startSelectFromDomain(domRef);
                        Tree meta,
                                objs;
                        meta = firstChild(child, DOMMETA);
                        objs = firstChild(child, DOMOBJS);
                        if (meta != null) {
                            listener.startSelectFromDomainMeta();
                            withChildren(meta, tree -> {
                                selectAttribute(tree);
                            });
                            listener.endSelectFromDomainMeta();
                        }
                        if (objs != null) {
                            listener.startSelectFromDomainObjects();
                            withChildren(objs, tree -> {
                                selectAttribute(tree);
                            });
                            listener.endSelectFromDomainObjects();
                        }
                        listener.endSelectFromDomain();
                    }
                    case FUNCTION ->
                        selectFunction(child);
                    case ATTRIBUTE ->
                        selectAttribute(child);
                    default ->
                        logUnknownTree(child);
                }
            });
        }

        logger.debug("End Select");
        listener.endSelect();
    }

    private void selectAttribute(Tree tree) {
        String attrName = subtext(firstChild(tree, ATTRIBUTENAME));
        String attrAs = subtext(firstChild(tree, ATTRIBUTEAS));
        String clDomRef = subtext(firstChild(tree, CLASSDOMREF));
        listener.addSelectAttribute(attrName, attrAs, clDomRef);
    }

    private void selectFunction(Tree tree) {
        String funcName = subtext(tree.getChild(0));
        String funcAs = subtext(firstChild(tree, ATTRIBUTEAS));
        listener.startSelectFunction(funcName, funcAs);
        withChildren(firstChild(tree, ATTRIBUTES), child -> {
            selectAttribute(child);
        });
        listener.endSelectFunction();
    }

    public void where(Tree where) {
        logger.debug("Start Where");
        listener.startWhere();

        withChildren(where, new HandleFields());

        logger.debug("End Where");
        listener.endWhere();
    }

    private void whereDomain(Tree tree, CQLBuilderListener.WhereType type) {
        String classScope = subtext(firstChild(tree, CLASSDOMREF));
        Tree domType = firstChild(tree, DOMTYPE);
        CQLBuilderListener.DomainDirection dir = null == firstChild(domType, DEFAULT) ? CQLBuilderListener.DomainDirection.DEFAULT : CQLBuilderListener.DomainDirection.INVERSE;

        boolean isNot = null != firstChild(domType, NOT);
        String domName = subtext(firstChild(tree, DOMNAME));
        String domId = subtext(firstChild(tree, DOMID));

        if (domId != null) {
            long domainId = Long.parseLong(domId);
            listener.startDomain(type, classScope, domainId, dir, isNot);
        } else {
            listener.startDomain(type, classScope, domName, dir, isNot);
        }

        Tree domMeta, domObjs;
        domMeta = firstChild(tree, DOMVALUE);
        domObjs = firstChild(tree, DOMCARDS);

        if (domMeta != null) {
            listener.startDomainMeta();
            withChildren(domMeta, new HandleFields());
            listener.endDomainMeta();
        }
        if (domObjs != null) {
            listener.startDomainObjects();
            withChildren(domObjs, new HandleFields());
            listener.endDomainObjects();
        }
        listener.endDomain();
    }

    private void whereDomainRef(Tree tree, CQLBuilderListener.WhereType type) {
        String domRefName = subtext(firstChild(tree, DOMNAME));
        Tree domType = firstChild(tree, DOMTYPE);
        boolean isNot = null != firstChild(domType, NOT);

        listener.startDomainRef(type, domRefName, isNot);

        Tree domMeta, domObjs;
        domMeta = firstChild(tree, DOMVALUE);
        domObjs = firstChild(tree, DOMCARDS);

        if (domMeta != null) {
            listener.startDomainMeta();
            withChildren(domMeta, new HandleFields());
            listener.endDomainMeta();
        }
        if (domObjs != null) {
            listener.startDomainObjects();
            withChildren(domObjs, new HandleFields());
            listener.endDomainObjects();
        }
        listener.endDomainRef();
    }

    private void whereField(Tree tree, CQLBuilderListener.WhereType type) {
        Tree id = firstChild(tree, FIELDID);
        Tree operator = firstChild(tree, FIELDOPERATOR).getChild(0);
        Tree value = firstChild(tree, FIELDVALUE);

        boolean simple = false;
        boolean lookup;
        // has 1 child or has 2 children, and the first is a domain/class
        if (id.getChildCount() == 1 || id.getChildCount() == 2 && (hasDeclaration(subtext(id.getChild(0))))) {
            simple = true;
        }
        lookup = null != firstChild(id, LOOKUP);

        CQLBuilderListener.FieldOperator fieldop = null;

        boolean isNot = false;
        switch (operator.getType()) {
            case LTEQ, GTEQ, LT, GT, EQ, CONT, BGN, END, IN, BTW, ISNULL ->
                fieldop = CQLBuilderListener.FieldOperator.valueOf(operator.getText());
            case NOTEQ, NOTCONT, NOTBGN, NOTEND, NOTIN, NOTBTW -> {
                isNot = true;
                fieldop = CQLBuilderListener.FieldOperator.valueOf(operator.getText().substring(3));
            }
            case ISNOTNULL -> {
                isNot = true;
                fieldop = CQLBuilderListener.FieldOperator.ISNULL;
            }
            default ->
                logUnknownTree(operator);
        }

        if (simple) {
            String fieldId, cname = null;
            if (id.getChildCount() == 1) {
                fieldId = subtext(id.getChild(0));
            } else {
                cname = subtext(id.getChild(0));
                fieldId = subtext(id.getChild(1));
            }
            listener.startSimpleField(type, isNot, cname, fieldId, fieldop);
        } else if (lookup) {
            List<CQLBuilderListener.LookupOperator> operators = new ArrayList();
            String classDomRef = subtext(firstChild(id, CLASSDOMREF));
            String fieldId = id.getChild(0).getText();
            Tree lkpOp = firstChild(id, LOOKUP).getChild(0);
            while (lkpOp != null) {
                String op = lkpOp.getText();
                String attr = subtext(firstChild(lkpOp, ATTRIBUTE));
                operators.add(new CQLBuilderListener.LookupOperator(op, attr));

                lkpOp = firstChild(lkpOp, LOOKUPPARENT);
            }
            listener.startLookupField(type, isNot, classDomRef, fieldId,
                    operators.toArray(CQLBuilderListener.LookupOperator[]::new), fieldop);
        } else {
            List<String> path = new ArrayList();

            String classDomRef = null;
            int startIdx = 0;
            if (hasDeclaration(subtext(id.getChild(0)))) {
                classDomRef = subtext(id.getChild(0));
                startIdx = 1;
            }
            for (int i = startIdx; i < id.getChildCount(); i++) {
                path.add(subtext(id.getChild(i)));
            }

            listener.startComplexField(type, isNot, classDomRef, path.toArray(String[]::new), fieldop);
        }

        if (value != null) {
            withChildren(value, (Tree v) -> {
                handleFieldValue(v);
            });
        }
    }

    SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/DD");
    SimpleDateFormat df2 = new SimpleDateFormat("yy/MM/DD");
    SimpleDateFormat ts1 = new SimpleDateFormat("yyyy/MM/DD'T'HH:mm:ss");
    SimpleDateFormat ts2 = new SimpleDateFormat("yy/MM/DD'T'HH:mm:ss");

    private void handleFieldValue(Tree value) {
        switch (value.getType()) {
            case LITBOOL -> {
                listener.startValue(FieldValueType.BOOL);
                listener.value(null != firstChild(value, TRUE));
                listener.endValue();
            }
            case LITDATE -> {
                listener.startValue(FieldValueType.DATE);
                Date dt = null;
                String dttxt = value.getChild(0).getText();
                try {
                    if (dttxt.length() == 8) {
                        dt = df2.parse(dttxt);
                    } else {
                        dt = df1.parse(dttxt);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Cannot parse date: " + dttxt);
                }
                listener.value(dt);
                listener.endValue();
            }
            case LITTIMESTAMP -> {
                listener.startValue(FieldValueType.TIMESTAMP);
                Date ts = null;
                String tstxt = value.getChild(0).getText();
                try {
                    if (tstxt.length() == 17) {
                        ts = ts2.parse(tstxt);
                    } else {
                        ts = ts1.parse(tstxt);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Cannot parse timestamp: " + tstxt);
                }
                listener.value(ts);
                listener.endValue();
            }
            case INPUTVAL -> {
                listener.startValue(FieldValueType.INPUT);
                String varname = text(value.getChild(0));
                listener.value(new CQLBuilderListener.FieldInputValue(varname));
                listener.endValue();
            }
            case LITSTR -> {
                listener.startValue(FieldValueType.STRING);
                listener.value(extractLiteral(text(value.getChild(0))));
                listener.endValue();
            }
            case LITNUM -> {
                String numtxt = subtext(value);
                if (numtxt.indexOf('.') != -1) {
                    listener.startValue(FieldValueType.FLOAT);
                    listener.value(Float.valueOf(numtxt));
                } else {
                    listener.startValue(FieldValueType.INT);
                    listener.value(Long.valueOf(numtxt));
                }
                listener.endValue();
            }
            case EXPR -> {
                listener.startValue(FieldValueType.SUBEXPR);
                this.expression(value);
                listener.endValue();
            }
            case NATIVE -> {
                listener.startValue(FieldValueType.NATIVE);
                String nativetxt = subtext(value);
                nativetxt = nativetxt.substring(1, nativetxt.length() - 1);
                listener.value(new CQLBuilderListener.FieldNativeSQLValue(nativetxt));
                listener.endValue();
            }
            default ->
                logUnknownTree(value);
        }
    }

    private String extractLiteral(String literal) {
        char first = literal.charAt(0);
        literal = literal.substring(1, literal.length() - 1);
        if (first == '"') {
            literal = literal.replace("\\\"", "\"");
        } else {
            literal = literal.replace("\\'", "'");
        }

        return literal;
    }

    public void group(Tree group) {
        logger.debug("Start GroupBy");
        listener.startGroupBy();

        withChildren(group, (Tree c) -> {
            if (c.getType() != ATTRIBUTE) {
                throw new RuntimeException("GroupBy handle only attributes!");
            }
            String classDomainRef = subtext(firstChild(c, CLASSDOMREF));
            String attrName = subtext(firstChild(c, ATTRIBUTENAME));

            listener.addGroupByElement(classDomainRef, attrName);
        });

        logger.debug("End GroupBy");
        listener.endGroupBy();
    }

    public void order(Tree order) {
        logger.debug("Start OrderBy");
        listener.startOrderBy();

        withChildren(order, (Tree c) -> {
            String classDomRef = subtext(firstChild(c, CLASSDOMREF));
            String attrName = c.getChild(0).getText();
            boolean asc = null != firstChild(c, ASC);
            boolean desc = null != firstChild(c, DESC);

            CQLBuilderListener.OrderByType type;
            if (!asc && !desc) {
                type = CQLBuilderListener.OrderByType.DEFAULT;
            } else if (asc) {
                type = CQLBuilderListener.OrderByType.ASC;
            } else {
                type = CQLBuilderListener.OrderByType.DESC;
            }
            listener.addOrderByElement(classDomRef, attrName, type);
        });

        logger.debug("End OrderBy");
        listener.endOrderBy();

    }

    public void limit(Tree limit) {
        boolean literal = null != firstChild(limit, LITNUM);
        if (literal) {
            long limitInt = Long.parseLong(subtext(firstChild(limit, LITNUM)));
            listener.setLimit(limitInt);
        } else {
            listener.setLimit(new CQLBuilderListener.FieldInputValue(subtext(firstChild(limit, INPUTVAL))));
        }
    }

    public void offset(Tree offset) {
        boolean literal = null != firstChild(offset, LITNUM);
        if (literal) {
            long offsetInt = Long.parseLong(subtext(firstChild(offset, LITNUM)));
            listener.setOffset(offsetInt);
        } else {
            listener.setOffset(new CQLBuilderListener.FieldInputValue(subtext(firstChild(offset, INPUTVAL))));
        }
    }

}
