/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.inner;

import org.cmdbuild.requestcontext.RequestContextHolder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface CurrentSessionHolder extends RequestContextHolder<String> {

    default String getCurrentSessionIdNotNull() {
        return checkNotBlank(get());
    }

}
