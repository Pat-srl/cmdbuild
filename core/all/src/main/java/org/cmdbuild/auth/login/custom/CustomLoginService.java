/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.custom;

import org.cmdbuild.auth.login.AuthRequestInfo;

public interface CustomLoginService {

    void handleCustomLoginRequestAndCreateAndSetSession(AuthRequestInfo authRequestInfo);

}
