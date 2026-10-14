/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked;

import org.cmdbuild.utils.sked.inner.SkedServiceImpl;

public class Sked {

    public static SkedService newSkedService() {
        return new SkedServiceImpl();
    }

    public static SkedService startSkedService() {
        return new SkedServiceImpl().start();
    }

    public static SkedService startSkedService(SkedEnv env) {
        return new SkedServiceImpl(env).start();
    }

}
