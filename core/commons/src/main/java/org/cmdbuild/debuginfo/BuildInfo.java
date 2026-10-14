/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import jakarta.annotation.Nullable;

public interface BuildInfo {

    String getCommitInfo();

    String getVersionNumber();

    @Nullable
    String getEmbeddedVertName();

    @Nullable
    String getEmbeddedVertVersionNumber();

}
