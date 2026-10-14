/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import org.cmdbuild.data.filter.beans.CmdbFilterImpl;

public interface EcqlFilter {

    String getEcqlId();

    String getJsContext();

    default CmdbFilter toCmdbFilter() {
        return CmdbFilterImpl.builder().withEcqlFilter(this).build();
    }
}
