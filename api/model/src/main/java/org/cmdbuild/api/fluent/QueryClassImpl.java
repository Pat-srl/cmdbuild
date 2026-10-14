package org.cmdbuild.api.fluent;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.List;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.data.filter.beans.CmdbFilterImpl.noopFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class QueryClassImpl extends AbstractActiveCard implements QueryClass {

    private final Set<String> requestedAttributes = CmCollectionUtils.set();
    private CmdbFilter filter;

    public QueryClassImpl(FluentApiExecutor executor, String className) {
        super(executor, className, null);
    }

    @Override
    public QueryClassImpl withCode(String value) {
        super.setCode(value);
        return this;
    }

    @Override
    public QueryClassImpl withDescription(String value) {
        super.setDescription(value);
        return this;
    }

    @Override
    public QueryClassImpl with(String name, Object value) {
        return withAttribute(name, value);
    }

    @Override
    public QueryClassImpl withAttribute(String name, Object value) {
        super.set(name, value);
        return this;
    }

    @Override
    public QueryClassImpl limitAttributes(String... names) {
        requestedAttributes.addAll(asList(names));
        return this;
    }

    @Override
    public Set<String> getRequestedAttributes() {
        return unmodifiableSet(requestedAttributes);
    }

    @Override
    public CmdbFilter getFilter() {
        return firstNotNull(filter, noopFilter());
    }

    @Override
    public QueryClass withFilter(@Nullable String cmdbFilter) {
        filter = parseFilter(cmdbFilter);
        return this;
    }

    @Override
    public List<Card> fetch() {
        return executor().fetchCards(this);
    }

}
