package org.cmdbuild.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.auth.login.LoginType.LT_AUTO;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class LoginUserIdentity {

    private final String value;
    private final LoginType type;

    private LoginUserIdentity(LoginCredentialsBuilder builder) {
        this.value = checkNotNull(builder.value);
        this.type = firstNotNull(builder.type, LT_AUTO);
    }

    public String getValue() {
        return value;
    }

    public LoginType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Login{" + "value=<" + value + ">, type=" + serializeEnum(type) + '}';
    }

    public LoginUserIdentity withType(LoginType loginType) {
        return builder().withValue(value).withType(type).build();
    }

    public static LoginCredentialsBuilder builder() {
        return new LoginCredentialsBuilder();
    }

    public static LoginUserIdentity build(String value) {
        return builder().withValue(value).build();
    }

    public static class LoginCredentialsBuilder implements Builder<LoginUserIdentity, LoginCredentialsBuilder> {

        private String value;
        private LoginType type;

        @Override
        public LoginUserIdentity build() {
            return new LoginUserIdentity(this);
        }

        public LoginCredentialsBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public LoginCredentialsBuilder withType(LoginType value) {
            this.type = value;
            return this;
        }

    }

}
