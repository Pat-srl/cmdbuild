package org.cmdbuild.api.fluent;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableMap;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class FunctionCallImpl implements FunctionCall {

    private final FluentApiExecutor executor;
    private final String functionName;
    private final Map<String, Object> inputParameters = map();

    public FunctionCallImpl(FluentApiExecutor executor, String functionName) {
        this.executor = checkNotNull(executor);
        this.functionName = checkNotBlank(functionName);
    }

    @Override
    public String getFunctionName() {
        return functionName;
    }

    @Override
    public Map<String, Object> getInputs() {
        return unmodifiableMap(inputParameters);
    }

    @Override
    public FunctionCallImpl with(String name, Object value) {
        inputParameters.put(name, value);
        return this;
    }

    @Override
    public Map<String, Object> execute() {
        return executor.execute(this);
    }

}
