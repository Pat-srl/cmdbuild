/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.lock;

public class NotAquiredLockResponseImpl implements RiverLockResponse {

    @Override
    public boolean isAquired() {
        return false;
    }

    @Override
    public AquiredLock aquired() {
        throw new IllegalStateException("this lock was not aquired");
    }

    private static final NotAquiredLockResponseImpl INSTANCE = new NotAquiredLockResponseImpl();

    public static RiverLockResponse notAquired() {
        return INSTANCE;
    }

}
