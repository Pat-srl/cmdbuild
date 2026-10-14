/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

public interface ResultRow {

    <T> T toModel(Class<T> type);

    Map<String, Object> asMap();

    <T> T toModel();

    Card toCard();

    CMRelation toRelation();

    @Nullable
    <T> T get(Attribute outputParameter);

    @Nullable
    default <T> T getFunctionOutput(StoredFunction function) {
        return get(function.getOnlyOutputParameter());
    }

    @Nullable
    default <T> T get(String key, Class<T> type) {
        Map<String, Object> map = asMap();
        checkArgument(map.containsKey(key), "output value not found for key = %s", key);
        return convert(map.get(key), type);
    }

    @Nullable
    default <T> T get(String key) {
        return (T) asMap().get(key);
    }

}
