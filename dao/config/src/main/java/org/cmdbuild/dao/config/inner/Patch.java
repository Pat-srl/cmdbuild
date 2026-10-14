/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import com.google.common.base.Objects;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;

public interface Patch {

    String getVersion();

    String getDescription();

    Map<String, String> getParams();

    String getCategory();

    boolean isApplied();

    @Nullable
    ZonedDateTime getApplyDate();

    @Nullable
    String getContent();

    @Nullable
    String getHash();

    /**
     * return composite key, from category+version
     *
     * @return
     */
    default String getKey() {
        return getCategory() + "-" + getVersion();
    }

    default ComparableVersion getComparableVersion() {
        return new ComparableVersion(getVersion());
    }

    default boolean hasContent() {
        return !StringUtils.isBlank(getContent());
    }

    default boolean hasHash() {
        return !StringUtils.isBlank(getHash());
    }

    default boolean isCore() {
        return Objects.equal(getCategory(), PatchService.DEFAULT_CATEGORY);
    }

    @Nullable
    default String getParam(String key) {
        return getParams().get(key);
    }

    default boolean hasParams() {
        return !getParams().isEmpty();
    }

    default boolean reloadConnectionAfter() {
        return toBooleanOrDefault(getParam("RELOAD_CONNECTION_AFTER"), false);
    }

    default boolean requiresSuperuser() {
        return toBooleanOrDefault(getParam("REQUIRE_SUPERUSER"), false);
    }

    default boolean forceApplyIfNotExists() {
        return toBooleanOrDefault(getParam("FORCE_IF_NOT_EXISTS"), false);
    }

    default boolean isNotSql() {
        return toBooleanOrDefault(getParam("NOT_SQL"), false);
    }

}
