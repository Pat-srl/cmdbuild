/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysnotify;

import java.util.List;
import org.cmdbuild.fault.FaultEvent;

public interface SysnotifyService {

    void notifyJobErrors(String key, List<FaultEvent> collectedEvents);

}
