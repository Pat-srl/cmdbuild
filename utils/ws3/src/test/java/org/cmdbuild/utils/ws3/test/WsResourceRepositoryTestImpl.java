/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.test;

import java.util.List;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.ws3.api.WsResourceRepository;
import org.cmdbuild.utils.ws3.inner.WsResourceBeanWithInterface;

public class WsResourceRepositoryTestImpl implements WsResourceRepository {

    private final List<WsResourceBeanWithInterface> resources;

    public WsResourceRepositoryTestImpl(Object... resources) {
        this.resources = list(resources).map(r -> new WsResourceBeanWithInterface(r.getClass(), r));
    }

    @Override
    public Iterable<WsResourceBeanWithInterface> getResources() {
        return resources;
    }

}
