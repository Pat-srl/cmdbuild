/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableBiMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.unkey;
import org.cmdbuild.utils.random.CmRandomUtils;

public class AliasBuilder {

    private final Set<String> uniqueAliases = CmCollectionUtils.set();
    private final Map<String, String> aliasStore = map();
    private int counter = 0;

    public String buildAlias(String expr) {
        String base = expr.toLowerCase().replaceAll("[^a-z]", "");
        if (StringUtils.isBlank(base)) {
            base = CmRandomUtils.randomId(4);
        }
        if (!base.startsWith("_")) {
            base = "_" + base;
        }
        String alias = base;
        while (uniqueAliases.contains(alias)) {
            alias = base + encodeString(String.valueOf(counter++));
        }
        uniqueAliases.add(alias);
        return alias;
    }

    public String buildAliasAndStore(String expr, Object... key) {
        String alias = buildAlias(expr);
        aliasStore.put(key(key), alias);
        return alias;
    }

    public List<String> getKey(String alias) {
        return unkey(ImmutableBiMap.copyOf(aliasStore).inverse().get(checkNotBlank(alias)));
    }

    public String getAlias(Object... key) {
        return checkNotBlank(aliasStore.get(key(key)), "alias not found for key = %s", key);
    }

    public String getAliasOrNull(Object... key) {
        return hasAlias(key) ? getAlias(key) : null;
    }

    public boolean hasAlias(Object... key) {
        return isNotBlank(aliasStore.get(key(key)));
    }

    public void addAliasesFrom(AliasBuilder source) {
        uniqueAliases.addAll(source.uniqueAliases);
        aliasStore.putAll(source.aliasStore);
        counter = source.counter;
    }

    public String addAlias(String alias) {
        checkNotBlank(alias);
        checkArgument(uniqueAliases.add(alias), "unable to add alias: alias already found in store =< %s >", alias);
        return alias;
    }

}
