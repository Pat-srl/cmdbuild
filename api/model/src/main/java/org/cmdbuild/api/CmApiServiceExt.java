/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import java.util.Map;

public interface CmApiServiceExt extends CmApiService {

    Map<String, Object> getCmApiAsDataMap();

    @Override
    ExtendedApi getCmApi();
}
