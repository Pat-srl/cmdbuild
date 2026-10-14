package org.cmdbuild.api.fluent;

import java.util.Map;

public interface FunctionCall {

    FunctionCall with(String name, Object value);

    Map<String, Object> execute();

    String getFunctionName();

    Map<String, Object> getInputs();
}
