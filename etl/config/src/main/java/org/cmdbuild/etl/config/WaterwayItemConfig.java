/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import java.util.List;

public interface WaterwayItemConfig extends WaterwayItemBaseExt {

    List<WaterwayItemConfig> getItems();
}
