/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.KeyFromPartsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectHolder {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<SelectElement> select = CmCollectionUtils.list();
    private final Map<String, SelectElement> selectByNameAndJoinId = CmMapUtils.map();

    public void add(SelectElement element) {
        logger.trace("add select element = {}", element);
        select.add(element);
        checkArgument(selectByNameAndJoinId.put(KeyFromPartsUtils.key(element.getJoinFrom(), element.getName()), element) == null, "duplicate select element for name =< %s > joinId =< %s >", element.getName(), element.getJoinFrom());
    }

    @Nullable
    public SelectElement getByNameAndJoinOrNull(String joinId, String name) {
        return selectByNameAndJoinId.get(KeyFromPartsUtils.key(joinId, name));
    }

    @Nullable
    public SelectElement getByNameOrNull(String name) {
        return getByNameAndJoinOrNull(QueryBuilderServiceImpl.JOIN_ID_DEFAULT, name);
    }

    public SelectElement getByNameOrBuild(String name, Supplier<SelectElement> supplierIfNull) {
        return Optional.ofNullable(getByNameOrNull(name)).orElseGet(supplierIfNull);
    }

    public boolean isEmpty() {
        return select.isEmpty();
    }

    public List<SelectElement> getElements() {
        return Collections.unmodifiableList(select);
    }

    public Stream<SelectElement> stream() {
        return select.stream();
    }

    public SelectElement getByName(String name) {
        return checkNotNull(getByNameOrNull(name), "select element not found for name =< %s >", name);
    }

}
