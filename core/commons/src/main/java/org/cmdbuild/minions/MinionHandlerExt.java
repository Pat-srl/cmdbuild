package org.cmdbuild.minions;

import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;

public interface MinionHandlerExt extends MinionHandler {

    void setStatus(MinionRuntimeStatus status);
    
    default void setErrorIfReady(){
        if(isReady()){
            setStatus(MRS_ERROR);
        }
    }

}
