/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked;

public class DefaultSkedEnv implements SkedEnv {

    @Override
    public boolean isMasterNode() {
        return true;
    }

}
