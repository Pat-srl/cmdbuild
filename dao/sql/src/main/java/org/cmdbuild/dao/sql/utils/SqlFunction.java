/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.sql.utils;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.artifact.versioning.ComparableVersion;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SqlFunction {

    String getSignature();

    String getRequiredPatchVersion();

    String getFunctionDefinition();

    String getHash();

    @Nullable
    String getComment();

    default String getName() {
        Matcher matcher = Pattern.compile("^[^(]+").matcher(getSignature());
        checkArgument(matcher.find());
        return checkNotBlank(matcher.group());
    }

    default boolean hasComment() {
        return isNotBlank(getComment());
    }

    default ComparableVersion getRequiredPatchVersionAsComparableVersion() {
        return new ComparableVersion(getRequiredPatchVersion());
    }

}
