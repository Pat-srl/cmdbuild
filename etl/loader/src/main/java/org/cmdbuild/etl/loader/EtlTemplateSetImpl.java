/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class EtlTemplateSetImpl implements EtlTemplateSet {

    private final List<EtlTemplate> templates;

    public EtlTemplateSetImpl(Iterable<EtlTemplate> templates) {
        this.templates = ImmutableList.copyOf(templates);
    }

    @Override
    public List<EtlTemplate> getTemplates() {
        return templates;
    }

}
