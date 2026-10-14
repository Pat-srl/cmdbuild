/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.maven.MavenUtils.getMajorMinorVersion;

public interface BuildInfoService {

    BuildInfoExt getBuildInfo();

    default String getVersionNumber() {
        return getBuildInfo().getVersionNumber();
    }

    default String getPatchVersionNumber() {
        return getMajorMinorVersion(getVersionNumber());
    }

    default String getVertVersionNumber() {
        return getBuildInfo().getVertVersionNumber();
    }

    default String getVertName() {
        return getBuildInfo().getVertName();
    }

    default String getCommitInfo() {
        return getBuildInfo().getCommitInfo();
    }

    default String getCompleteVersionNumber() {
        return trimToEmpty(format("%s %s", nullToEmpty(getVertVersionNumber()), getVersionNumber())).replaceAll(" ", "-");
    }

    default String getCompleteVersionNumberWithVertName() {
        return trimToEmpty(format("%s %s", nullToEmpty(getVertName()), getCompleteVersionNumber()));
    }

    interface BuildInfoExt extends BuildInfo {

        @Nullable
        String getDbVertVersionNumber();

        @Nullable
        String getDbVertName();

        @Nullable
        default String getVertName() {
            return firstNotBlankOrNull(getDbVertName(), getEmbeddedVertName());
        }

        @Nullable
        default String getVertVersionNumber() {
            return firstNotBlankOrNull(getDbVertVersionNumber(), getEmbeddedVertVersionNumber());
        }
    }

}
