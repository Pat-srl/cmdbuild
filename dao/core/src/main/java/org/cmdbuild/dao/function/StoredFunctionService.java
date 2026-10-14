/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.function;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.dao.driver.repository.StoredFunctionRepository;

public interface StoredFunctionService extends StoredFunctionRepository {

    Map<String, Object> callFunction(String functionId, Map<String, Object> functionParams);

    Map<String, Object> callFunction(StoredFunction function, Map<String, Object> functionParams);

    PreparedQuery selectFunction(StoredFunction function, List<Object> input, List<Attribute> outputParameters);

    default Map<String, Object> callFunction(String functionId) {
        return callFunction(functionId, emptyMap());
    }

    default PreparedQuery selectFunction(String function, List<Object> input, List<Attribute> outputParameters) {
        return selectFunction(getFunctionByName(function), input, outputParameters);
    }

    default PreparedQuery selectFunction(StoredFunction function, List input) {
        return selectFunction(function, input, function.getOutputParameters());
    }

    default PreparedQuery selectFunction(StoredFunction function, Map<String, ?> input) {
        checkArgument(equal(set(function.getInputParameterNames()), input.keySet()), "expected exactly these params = %s, found instead these = %s", function.getInputParameterNames(), input.keySet());
        return selectFunction(function, function.getInputParameterNames().stream().map(input::get).collect(toList()));
    }
}
