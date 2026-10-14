/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import org.cmdbuild.data.filter.AttachmentFilter;
import org.cmdbuild.data.filter.CmdbFilter;

public class AttachmentFilterImpl implements AttachmentFilter {

    private final CmdbFilter innerFilter;

    public AttachmentFilterImpl(CmdbFilter innerFilter) {
        this.innerFilter = innerFilter;
    }

    @Override
    public CmdbFilter getInnerFilter() {
        return innerFilter;
    }

    @Override
    public boolean hasAttributeFilter() {
        return innerFilter.hasAttributeFilter();
    }

    @Override
    public boolean hasFulltextFilter() {
        return innerFilter.hasFulltextFilter();
    }

}
