/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import static com.google.common.base.Objects.equal;
import java.util.List;
import org.cmdbuild.client.rest.core.RestServiceClient;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface LookupApi extends RestServiceClient {

    List<LookupValue> getValues(String lookupTypeId);

    default LookupValue getValueByCode(String lookupTypeId, String code) {
        checkNotBlank(code);
        return getValues(lookupTypeId).stream().filter(l -> equal(l.getCode(), code)).collect(onlyElement("lookup value not found for type =< %s > code =< %s >", lookupTypeId, code));
    }

    interface LookupValue {

        long getId();

        String getType();

        String getCode();

        String getDescription();
    }
}
