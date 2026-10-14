/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.client.rest.impl;

import java.util.Map;
import java.util.stream.Collectors;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;

import org.cmdbuild.api.SchemaCollectorApi;
import org.cmdbuild.client.rest.core.RestWsClient;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Used by <i>CMDBuild CLI</i>, implementation that invokes the related WS rest.
 *
 * @author afelice
 */
public class SchemaCollectorApiImpl extends AbstractServiceClientImpl implements SchemaCollectorApi {

    protected static String WS_SERVICE = "administration/schema/collect";

    public SchemaCollectorApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public String test(String any) {
        checkNotBlank(any);

        return get("%s/test?%s".formatted(WS_SERVICE, toQueryParams(map(
                   "msg", encodeUrlQuery(any))
                ))
        ).asString();
    }

    @Override
    public String collectSchema(String curSystemMnemonicName, String curSystemId) {
        checkNotBlank(curSystemMnemonicName);
        if (isBlank(curSystemId)) {
            curSystemId = "0";
        }

        return put(
                "%s/collectSchema?%s".formatted(WS_SERVICE, toQueryParams(map(
                        "name", curSystemMnemonicName,
                        "id", curSystemId
                ))),
                map()
        ).asString();
    }

    @Override
    public String compareSchema(String otherSchemaSerialization, String curSystemMnemonicName) {
        return put(
                "%s/compareSchema?%s".formatted(WS_SERVICE, toQueryParams(map(
                        "other", otherSchemaSerialization,
                        "name", curSystemMnemonicName
                ))),
                map()
        ).asString();
    }

    @Override
    public String compareSchemaBetween(String newSchemaSerialization, String aSchemaSerialization) {
        return put(
                "%s/compareSchemaBetween?%s".formatted(WS_SERVICE, toQueryParams(map(
                        "new", newSchemaSerialization,
                        "a", aSchemaSerialization
                ))),
                map()
        ).asString();
    }

    @Override
    public String applySchemaDiff(String diffSchemaSerialization) {
        return put(
                "%s/applySchemaDiff?%s".formatted(WS_SERVICE, toQueryParams(map(
                        "diff", diffSchemaSerialization
                ))),
                map()
        ).asString();
    }

    /**
     * Generates the query string part for an URL:
     *  
     * @param params a map like <code>("name", foo, "id", 12)</code>
     * @return the related query string <code>"name=foo&id=12"</code>
     */
    private String toQueryParams(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + encodeUrlQuery(entry.getValue()))
                .collect(Collectors.joining("&"));        
    }
    
}
