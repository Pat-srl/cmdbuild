package org.cmdbuild.config;

import org.cmdbuild.auth.login.PasswordManagementConfiguration;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.password")
public class PasswordManagementConfigurationImpl implements PasswordManagementConfiguration {

    @ConfigValue(key = "validation.enabled", description = "enable password management rules, expiration, etc", defaultValue = FALSE)
    private boolean passwordValidationEnabled;

    @ConfigValue(key = "change.enabled", description = "allow users to change their own password", defaultValue = TRUE)
    private boolean passwordChangeEnabled;

    @ConfigValue(key = "validation.differentFromUsername", defaultValue = TRUE)
    private boolean differentFromUsername;

    @ConfigValue(key = "validation.differentFromPrevious", defaultValue = TRUE)
    private boolean differentFromPrevious;

    @ConfigValue(key = "validation.differentFromPreviousCount", defaultValue = "3")
    private int differentFromPreviousCount;

    @ConfigValue(key = "validation.requireDigit", defaultValue = FALSE)
    private boolean requireDigit;

    @ConfigValue(key = "validation.requireLowercase", defaultValue = FALSE)
    private boolean requireLowercase;

    @ConfigValue(key = "validation.requireUppercase", defaultValue = FALSE)
    private boolean requireUppercase;

    @ConfigValue(key = "validation.minLength", defaultValue = "6")
    private int passwordMinLength;

    @ConfigValue(key = "maxAgeDays", defaultValue = "365")
    private int maxPasswordAgeDays;

    @ConfigValue(key = "forewarningDays", defaultValue = "7")
    private int forewarningDays;

    @ConfigValue(key = "expireServiceUserPassword", description = "if true, password expiration affects `service` users", defaultValue = FALSE)
    private boolean expireServiceUserPassword;

    @Override
    public boolean isPasswordManagementEnabled() {
        return passwordValidationEnabled;
    }

    @Override
    public boolean isPasswordChangeEnabled() {
        return passwordChangeEnabled;
    }

    @Override
    public boolean isServiceUsersPasswordExpirationEnabled() {
        return expireServiceUserPassword;
    }

    @Override
    public boolean getDifferentFromUsername() {
        return differentFromUsername;
    }

    @Override
    public boolean getDifferentFromPrevious() {
        return differentFromPrevious;
    }

    @Override
    public int getDifferentFromPreviousCount() {
        return differentFromPreviousCount;
    }

    @Override
    public boolean requireDigit() {
        return requireDigit;
    }

    @Override
    public boolean requireLowercase() {
        return requireLowercase;
    }

    @Override
    public boolean requireUppercase() {
        return requireUppercase;
    }

    @Override
    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    @Override
    public int getMaxPasswordAgeDays() {
        return maxPasswordAgeDays;
    }

    @Override
    public int getForewarningDays() {
        return forewarningDays;
    }

}
