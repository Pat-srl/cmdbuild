/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.sql.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.slf4j.LoggerFactory;

public class SqlFunctionImpl implements SqlFunction {

    private final String signature, hash, content, requiredPatchVersion, comment;

    private SqlFunctionImpl(SqlFunctionImplBuilder builder) {
        this.signature = checkNotBlank(builder.signature);
        this.content = checkNotBlank(builder.content);
        this.comment = builder.comment;
        this.requiredPatchVersion = firstNotBlank(builder.requiredPatchVersion, "3.0.0-00");
        this.hash = hash(content + nullToEmpty(comment));
        if (isNotBlank(builder.hash) && !equal(this.hash, builder.hash)) {
            LoggerFactory.getLogger(getClass()).warn("hash mismatch for function record =< {} >", signature);
        }
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public String getFunctionDefinition() {
        return content;
    }

    @Override
    public String getRequiredPatchVersion() {
        return requiredPatchVersion;
    }

    @Override
    @Nullable
    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "SqlFunction{" + "signature=" + signature + '}';
    }

    public static SqlFunctionImplBuilder builder() {
        return new SqlFunctionImplBuilder();
    }

    public static SqlFunctionImplBuilder copyOf(SqlFunction source) {
        return new SqlFunctionImplBuilder()
                .withSignature(source.getSignature())
                .withHash(source.getHash())
                .withFunctionDefinition(source.getFunctionDefinition())
                .withRequiredPatchVersion(source.getRequiredPatchVersion())
                .withComment(source.getComment());
    }

    public static class SqlFunctionImplBuilder implements Builder<SqlFunctionImpl, SqlFunctionImplBuilder> {

        private String signature, hash, content, requiredPatchVersion, comment;

        public SqlFunctionImplBuilder withSignature(String signature) {
            this.signature = signature;
            return this;
        }

        public SqlFunctionImplBuilder withHash(String hash) {
            this.hash = hash;
            return this;
        }

        public SqlFunctionImplBuilder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public SqlFunctionImplBuilder withFunctionDefinition(String content) {
            this.content = content;
            return this;
        }

        public SqlFunctionImplBuilder withRequiredPatchVersion(String requiredPatchVersion) {
            this.requiredPatchVersion = requiredPatchVersion;
            return this;
        }

        @Override
        public SqlFunctionImpl build() {
            return new SqlFunctionImpl(this);
        }

    }
}
