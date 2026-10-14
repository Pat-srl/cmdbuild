/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import com.google.common.collect.ImmutableList;
import static java.util.Collections.singletonList;
import java.util.List;
import org.cmdbuild.data.filter.FunctionFilter;
import org.cmdbuild.data.filter.FunctionFilterEntry;

public class FunctionFilterImpl implements FunctionFilter {

    private final List<FunctionFilterEntry> functions;

    public FunctionFilterImpl(String function) {
        this(singletonList(new FunctionFilterEntryImpl(function)));
    }

    public FunctionFilterImpl(List<FunctionFilterEntry> functions) {
        this.functions = ImmutableList.copyOf(functions);
    }

    @Override
    public List<FunctionFilterEntry> getFunctions() {
        return functions;
    }

}
