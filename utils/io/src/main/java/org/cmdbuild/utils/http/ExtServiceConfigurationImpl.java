/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.http;

import org.cmdbuild.utils.lang.Builder;

/**
 *
 * @author ldare
 */
public class ExtServiceConfigurationImpl implements ExtServiceConfiguration {

    private final String url;
    private final String username;
    private final String password;

    private ExtServiceConfigurationImpl(ExtServiceConfigurationBuilder builder) {
        this.url = builder.url;
        this.username = builder.username;
        this.password = builder.password;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Long getTimeout() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static ExtServiceConfigurationBuilder builder() {
        return new ExtServiceConfigurationBuilder();
    }

    public static class ExtServiceConfigurationBuilder implements Builder<ExtServiceConfigurationImpl, ExtServiceConfigurationBuilder> {

        private String url;
        private String username;
        private String password;

        public ExtServiceConfigurationBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public ExtServiceConfigurationBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public ExtServiceConfigurationBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        @Override
        public ExtServiceConfigurationImpl build() {
            return new ExtServiceConfigurationImpl(this);
        }
    }

}
