/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Map;

public interface AttributeGroupInfo {

    String getName();

    String getDescription();

    Map<String, String> getConfig();

}
