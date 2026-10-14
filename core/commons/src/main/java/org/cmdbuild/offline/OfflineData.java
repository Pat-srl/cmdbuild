/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.offline;

import javax.annotation.Nullable;

public interface OfflineData {

    @Nullable
    Long getId();

    String getCode();

    String getDescription();

    String getMetadata();

    Boolean isEnabled();
}
