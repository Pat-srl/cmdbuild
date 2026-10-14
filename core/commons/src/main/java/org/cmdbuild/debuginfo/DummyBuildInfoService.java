/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import jakarta.annotation.Nullable;

public enum DummyBuildInfoService implements BuildInfoService {

    INSTANCE;

    @Override
    public BuildInfoExt getBuildInfo() {
        return new BuildInfoExt() {

            @Override
            public String getCommitInfo() {
                return "unknown";
            }

            @Override
            public String getVersionNumber() {
                return "unknown";
            }

            @Override
            @Nullable
            public String getEmbeddedVertName() {
                return null;
            }

            @Override
            @Nullable
            public String getEmbeddedVertVersionNumber() {
                return null;
            }

            @Override
            @Nullable
            public String getDbVertVersionNumber() {
                return null;
            }

            @Override
            @Nullable
            public String getDbVertName() {
                return null;
            }

        };
    }

}
