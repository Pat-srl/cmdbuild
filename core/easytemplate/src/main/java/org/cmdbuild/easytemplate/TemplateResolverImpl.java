/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import com.google.common.base.Preconditions;
import java.util.function.Function;

public class TemplateResolverImpl implements TemplateResolver {

    private final Function<String, Object> function;
    private final boolean recursive;

    public TemplateResolverImpl(Function<String, Object> function, boolean recursive) {
        this.function = Preconditions.checkNotNull(function);
        this.recursive = recursive;
    }

    @Override
    public Function<String, Object> getFunction() {
        return function;
    }

    @Override
    public boolean isRecursive() {
        return recursive;
    }

}
