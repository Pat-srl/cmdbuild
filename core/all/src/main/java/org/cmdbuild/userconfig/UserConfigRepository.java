/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import java.util.Map;

public interface UserConfigRepository {

    Map<String, String> getByUsername(String username);

    void setByUsername(String username, Map<String, String> data);

}
