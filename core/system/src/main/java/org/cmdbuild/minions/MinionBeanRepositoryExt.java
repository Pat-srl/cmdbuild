/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import org.cmdbuild.minions.InnerBean;
import org.cmdbuild.minions.MinionBeanRepository;

public interface MinionBeanRepositoryExt extends MinionBeanRepository {

    void addMinionBean(InnerBean bean);

}
