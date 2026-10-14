/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.DaoQueryOptionsImplBuilder;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class UserCardQueryOptionsImpl implements UserCardQueryOptions {

    private final DaoQueryOptions queryOptions;
    private final UserCardQueryForDomain forDomain;
    private final String functionValue;

    private UserCardQueryOptionsImpl(UserCardQueryOptionsImplBuilder builder) {
        this.queryOptions = firstNotNull(builder.queryOptions, emptyOptions());
        this.forDomain = builder.forDomain;
        this.functionValue = builder.functionValue;
    }

    @Override
    public DaoQueryOptions getQueryOptions() {
        return queryOptions;
    }

    @Override
    @Nullable
    public UserCardQueryForDomain getForDomain() {
        return forDomain;
    }

    @Override
    @Nullable
    public String getFunctionValue() {
        return functionValue;
    }

    public static UserCardQueryOptionsImplBuilder builder() {
        return new UserCardQueryOptionsImplBuilder();
    }

    public static UserCardQueryOptionsImplBuilder copyOf(UserCardQueryOptions source) {
        return new UserCardQueryOptionsImplBuilder()
                .withQueryOptions(source.getQueryOptions())
                .withForDomain(source.getForDomain())
                .withFunctionValue(source.getFunctionValue());
    }

    public static class UserCardQueryOptionsImplBuilder implements Builder<UserCardQueryOptionsImpl, UserCardQueryOptionsImplBuilder> {

        private DaoQueryOptions queryOptions;
        private UserCardQueryForDomain forDomain;
        private String functionValue;

        public UserCardQueryOptionsImplBuilder withQueryOptions(DaoQueryOptions queryOptions) {
            this.queryOptions = queryOptions;
            return this;
        }

        public UserCardQueryOptionsImplBuilder withQueryOptions(Consumer<DaoQueryOptionsImplBuilder> b) {
            return this.withQueryOptions(DaoQueryOptionsImpl.builder().accept(b).build());
        }

        public UserCardQueryOptionsImplBuilder withForDomain(UserCardQueryForDomain forDomain) {
            this.forDomain = forDomain;
            return this;
        }

        public UserCardQueryOptionsImplBuilder withForDomain(@Nullable String domainName, @Nullable RelationDirection direction, @Nullable Long originId) {
            this.forDomain = isBlank(domainName) ? null : UserCardQueryForDomainImpl.builder().withDomainName(domainName).withDirection(direction).withOriginId(originId).build();
            return this;
        }

        public UserCardQueryOptionsImplBuilder withFunctionValue(String functionValue) {
            this.functionValue = functionValue;
            return this;
        }

        @Override
        public UserCardQueryOptionsImpl build() {
            return new UserCardQueryOptionsImpl(this);
        }

    }
}
