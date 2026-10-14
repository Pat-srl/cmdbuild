/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.MoreCollectors.toOptional;
import jakarta.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.regex.Matcher;
import static java.util.regex.Pattern.compile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.IOUtils.readLines;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.dao.config.inner.PatchService.DEFAULT_CATEGORY;
import org.cmdbuild.exception.ORMException;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatchImpl implements Patch {

    private final String version, description, category, hash, content;
    private final boolean isApplied;
    private final ZonedDateTime applyDate;
    private final Map<String, String> params;

    public PatchImpl(PatchBuilder builder) {
        this.version = checkNotBlank(builder.version, "patch version is null");
        this.description = nullToEmpty(builder.description);
        this.category = firstNonNull(builder.category, DEFAULT_CATEGORY);
        this.hash = builder.hash;
        this.content = builder.content;
        this.isApplied = builder.isApplied;
        this.applyDate = builder.applyDate;
        this.params = builder.params == null ? emptyMap() : map(builder.params).immutable();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public boolean isApplied() {
        return isApplied;
    }

    @Override
    public ZonedDateTime getApplyDate() {
        return applyDate;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "Patch{" + "version=" + version + ", category=" + category + ", applied=" + isApplied + '}';
    }

    public static PatchBuilder builder() {
        return new PatchBuilder();
    }

    private static final String FILENAME_PATTERN = "(\\d\\.\\d\\.\\d-\\d{2}[a-z]*(_[a-z_0-9]+)?)\\.sql";
    private static final String FIRST_LINE_PATTERN = "--\\W*(.+)";

    public static class PatchBuilder implements Builder<Patch, PatchBuilder> {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private File file;
        private String category, version, description, content, hash;
        private boolean isApplied = false;
        private ZonedDateTime applyDate;
        private Map<String, String> params;

        public PatchBuilder appliedOn(ZonedDateTime dateTime) {
            this.isApplied = true;
            this.applyDate = checkNotNull(dateTime);
            return this;
        }

        public PatchBuilder withFile(File file) {
            this.file = checkNotNull(file);
            return this;
        }

        /**
         * null category means default ('core')
         *
         * @param category
         * @return
         */
        public PatchBuilder withCategory(@Nullable String category) {
            this.category = category;
            return this;
        }

        public PatchBuilder withVersion(String version) {
            this.version = checkNotNull(version);
            return this;
        }

        public PatchBuilder withDescription(String description) {
            this.description = checkNotNull(description);
            return this;
        }

        public PatchBuilder withContent(@Nullable String content) {
            this.content = content;
            return this;
        }

        public PatchBuilder withHash(@Nullable String hash) {
            this.hash = hash;
            return this;
        }

        @Override
        public Patch build() {
            try {
                if (isBlank(content) && file != null) {
                    content = FileUtils.readFileToString(file, Charset.defaultCharset());
                }
                if (isBlank(version) && file != null) {
                    logger.trace("extracting version from file name '{}'", file);
                    Matcher matcher = compile(FILENAME_PATTERN).matcher(file.getName());
                    if (!matcher.lookingAt()) {
                        logger.error("file name does not match expected pattern");
                        throw ORMException.ORMExceptionType.ORM_MALFORMED_PATCH.createException();
                    }
                    version = matcher.group(1);
                }
                if (isBlank(description) && isNotBlank(content)) {
                    logger.trace("extracting description from first line of content");
                    String line = checkNotNull(new BufferedReader(new StringReader(content)).readLine());
                    Matcher matcher = compile(FIRST_LINE_PATTERN).matcher(line);
                    if (!matcher.lookingAt()) {
                        logger.error("first line '{}' does not match expected pattern", line);
                        throw ORMException.ORMExceptionType.ORM_MALFORMED_PATCH.createException();
                    }
                    description = matcher.group(1);
                }
                if (isBlank(hash) && isNotBlank(content)) {
                    hash = DigestUtils.sha256Hex(content);
                }
                if (params == null && isNotBlank(content)) {
                    logger.trace("extracting params from content");
                    params = readLines(new StringReader(content)).stream().filter(l -> l.matches("^ *-- *PARAMS *:.*")).collect(toOptional()).map(l -> decodeUrlParams(l.replaceFirst("^ *-- *PARAMS *: *(.*)", "$1"))).orElse(emptyMap());
                }
                return new PatchImpl(this);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

}
