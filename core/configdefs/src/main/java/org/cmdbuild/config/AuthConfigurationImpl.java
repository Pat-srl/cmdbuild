package org.cmdbuild.config;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.login.LoginModuleConfiguration;
import org.cmdbuild.auth.login.PasswordAlgo;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.cmdbuild.script.ScriptType;
import org.cmdbuild.utils.date.Interval;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;

@Component
@ConfigComponent("org.cmdbuild.auth")
public final class AuthConfigurationImpl implements AuthenticationConfiguration {

    @ConfigValue(key = "case.insensitive", description = "", defaultValue = FALSE)
    private boolean authCaseInsensitive;

    @ConfigValue(key = "loginServiceReturnSessionId", description = "login service return session id, one of `auto` or `always`", defaultValue = "auto")
    private LoginServiceReturnId loginServiceReturnId;

    @ConfigValue(key = "users.expireInactiveAfterPeriod", description = "optional, if set all users that does not log in (or have an active session) for specified time period will be inactivated (active=false); value is a iso 8601 period es: `P60D` or `P6M`; note: remember to set an eventLog retention time bigger than user inactivation time")
    private String expireInactiveUsersAfterPeriod;

    @ConfigValue(key = "loginAttributeMode", description = "login attribute selection mode, one of `auto_detect_email` (use euristics to detect if login is email, in which case use `Email` attribute), `username` or `email`", defaultValue = "username")
    private UserRepoLoginAttributeMode loginAttributeMode;

    @ConfigValue(key = "preferredPasswordAlgorythm", description = "preferred password algorythm, one of `legacy` (legacy algo), `cm3easy` (modern but symmetric encryption algo, AES), `cm3` (state-of-the-art, secure algo, PBKDF2)", defaultValue = "cm3")
    private PasswordAlgo preferredPasswordAlgorythm;

    @ConfigValue(key = "maxLoginAttempts.count", description = "max login attempts", defaultValue = "5")
    private Integer maxLoginAttempts;

    @ConfigValue(key = "maxLoginAttempts.window", description = "max login attempts window (seconds)", defaultValue = "60")
    private Integer maxLoginAttemptsWindowSeconds;

    @ConfigValue(key = "sso.redirect.enabled", description = "enable auto redirect to sso login page, when only a single sso module is enabled", defaultValue = TRUE, category = CC_ENV)
    private Boolean autoSsoRedirectEnabled;

    @ConfigValue(key = "default.enabled", description = "enable default user/password repository (db)", defaultValue = TRUE, category = CC_ENV)
    private Boolean defaultEnabled;

    @ConfigValue(key = "helpText", description = "login help message, to be included in login form/screen", category = CC_ENV)
    private String loginHelp;

    @ConfigValue(key = "loginInputSource", description = "list of custom login input sources, such as `qrcode` or `rfid`", category = CC_ENV)
    private List<String> loginInputSource;

    @ConfigValue(key = "loginMobileDisableUsername", description = "disable username for mobile", defaultValue = FALSE, category = CC_ENV)
    private Boolean loginMobileDisableUsername;

    @ConfigValue(key = "file.enabled", description = "enable file authenticator (used for cli local filesystem authentication)", defaultValue = TRUE, category = CC_ENV)
    private Boolean fileEnabled;

    @ConfigValue(key = "rsa.enabled", description = "enable rsa authenticator", defaultValue = TRUE, category = CC_ENV)
    private Boolean rsaEnabled;

    @ConfigValue(key = "header.enabled", description = "enable header authenticator", defaultValue = FALSE, category = CC_ENV)
    private Boolean headerEnabled;

    @ConfigValue(key = "header.attribute.name", description = "", defaultValue = "username")
    private String headerAttributeName;

    @ConfigValue(key = "ldap.enabled", description = "enable ldap user repository", defaultValue = FALSE, category = CC_ENV)
    private Boolean ldapEnabled;

    @ConfigValue(key = "ldap.basedn", description = "ldap base dn for user query (such as dc=example,dc=com)", defaultValue = "", category = CC_ENV)
    private String ldapBaseDn;

    @ConfigValue(key = "ldap.server.address", description = "ldap server host address", defaultValue = "localhost", category = CC_ENV)
    private String ldapServerAddress;

    @ConfigValue(key = "ldap.server.port", description = "ldap server port", defaultValue = "389", category = CC_ENV)
    private int ldapServerPort;

    @ConfigValue(key = "ldap.server.url", description = "ldap server url (if set, will override server host, port and ssl config); you may specify multiple urls separated by one space (see java docs for ldap url param)", category = CC_ENV)
    private String ldapServerUrl;

    @ConfigValue(key = "ldap.bind.attribute", description = "ldap user bind attribute (used for searching users on ldap directory)", defaultValue = "cn", category = CC_ENV)
    private String ldapBindAttribute;

    @ConfigValue(key = "ldap.search.filter", description = "ldap search filter (used in addition to bind attribute, to further refine user query", defaultValue = "", category = CC_ENV)
    private String ldapSearchFilter;

    @ConfigValue(key = "ldap.search.auth.method", description = "ldap auth method (optional, one of 'none', 'simple' or 'strong')", defaultValue = "simple", category = CC_ENV)
    private String ldapAuthenticationMethod;

    @ConfigValue(key = "ldap.search.auth.principal", description = "ldap auth principal, such as uid=admin,ou=system", defaultValue = "", category = CC_ENV)
    private String ldapAuthenticationPrincipal;

    @ConfigValue(key = "ldap.search.auth.password", description = "ldap auth password (or other credentials)", defaultValue = "", category = CC_ENV)
    private String ldapAuthenticationPassword;

    @ConfigValue(key = "ldap.use.ssl", description = "enable ldaps", defaultValue = FALSE, category = CC_ENV)
    private boolean ldapUseSsl;

    @ConfigValue(key = "ldap.use.tls", description = "enable StartTLS (ldap+tls protocol)", defaultValue = FALSE, category = CC_ENV)
    private boolean ldapStartTls;

    @ConfigValue(key = "ldap.followReferrals", description = "enable referrals = follow", defaultValue = FALSE, category = CC_ENV)
    private boolean ldapFollowReferrals;

    @ConfigValue(key = "customlogin.enabled", description = "enable custom login (custom login request processing, to integrate with proprietary ssl/login frameworks)", defaultValue = FALSE)
    private boolean customLoginEnabled;

    @ConfigValue(key = "customlogin.handler", description = "custom login handler script (optionally encoded with base 64 or PACK)")
    private String customLoginHandlerScript;

    @ConfigValue(key = "customlogin.language", description = "custom login handler script language", defaultValue = "groovy")
    private ScriptType customLoginHandlerLanguage;

    @ConfigValue(key = "customlogin.classpath", description = "custom login handler script classpath")
    private String customLoginHandlerScriptClasspath;

    @ConfigValue(key = "logoutRedirect", description = "logount redirect url (es: `http://my.sso/some/path` or `https://cas-test:9443/cas/logout?service=http%3A%2F%2Fcmdbuild%3A8080%2Fcmdbuild`); if set, after logout browser will redirect here", category = CC_ENV)
    private String logoutRedirectUrl;//TODO check this: multiple logout redirect options based on login module!

    @ConfigValue(key = "modules", description = "list of login modules", defaultValue = "default", modular = "module", category = CC_ENV)
    private List<LoginModuleConfiguration> loginModules;

    @Override
    public String getLoginHelp() {
        return loginHelp;
    }

    @Override
    public boolean isAutoSsoRedirectEnabled() {
        return autoSsoRedirectEnabled;
    }

    @Override
    public List<String> getLoginInputSource() {
        return loginInputSource;
    }

    @Override
    public boolean isLoginMobileUsernameEnabled() {
        return loginMobileDisableUsername;
    }

    @Override
    public List<LoginModuleConfiguration> getLoginModules() {
        return loginModules;
    }

    @Override
    public boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    @Override
    public boolean isFileEnabled() {
        return fileEnabled;
    }

    @Override
    public boolean isRsaEnabled() {
        return rsaEnabled;
    }

    @Override
    public boolean isHeaderEnabled() {
        return headerEnabled;
    }

    @Override
    public boolean isLdapEnabled() {
        return ldapEnabled;
    }

    @Override
    public LoginServiceReturnId getLoginServiceReturnIdMode() {
        return loginServiceReturnId;
    }

    @Override
    @Nullable
    public Interval getExpireUnusedUsersAfterDuration() {
        return isBlank(expireInactiveUsersAfterPeriod) ? null : Interval.valueOf(expireInactiveUsersAfterPeriod);
    }

    @Override
    @Nullable
    public Integer getMaxLoginAttempts() {
        return maxLoginAttempts;
    }

    @Override
    @Nullable
    public Integer getMaxLoginAttemptsWindowSeconds() {
        return maxLoginAttemptsWindowSeconds;
    }

    @Override
    @Nullable
    public String getLogoutRedirectUrl() {
        return logoutRedirectUrl;
    }

    @Override
    public boolean enableLdapFollowReferrals() {
        return ldapFollowReferrals;
    }

    @Override
    public boolean isCaseInsensitive() {
        return authCaseInsensitive;
    }

    @Override
    public UserRepoLoginAttributeMode getLoginAttributeMode() {
        return loginAttributeMode;
    }

    @Override
    public String getHeaderAttributeName() {
        return headerAttributeName;
    }

    @Override
    public String getLdapServerAddress() {
        return ldapServerAddress;
    }

    @Override
    public int getLdapServerPort() {
        return ldapServerPort;
    }

    @Override
    public String getLdapServerUrl() {
        return ldapServerUrl;
    }

    @Override
    public boolean enableLdapSsl() {
        return ldapUseSsl;
    }

    @Override
    public boolean enableStartTls() {
        return ldapStartTls;
    }

    @Override
    public String getLdapBaseDN() {
        return ldapBaseDn;
    }

    @Override
    public String getLdapBindAttribute() {
        return ldapBindAttribute;
    }

    @Override
    public String getLdapSearchFilter() {
        return ldapSearchFilter;
    }

    @Override
    public String getLdapAuthenticationMethod() {
        return ldapAuthenticationMethod;
    }

    @Override
    public String getLdapPrincipal() {
        return ldapAuthenticationPrincipal;
    }

    @Override
    public String getLdapPrincipalCredentials() {
        return ldapAuthenticationPassword;
    }

    @Override
    public boolean isCustomLoginEnabled() {
        return customLoginEnabled;
    }

    @Override
    public String getCustomLoginHandlerScript() {
        return customLoginHandlerScript;
    }

    @Nullable
    @Override
    public ScriptType getCustomLoginHandlerLanguage() {
        return customLoginHandlerLanguage;
    }

    @Override
    @Nullable
    public String getCustomLoginHandlerScriptClasspath() {
        return customLoginHandlerScriptClasspath;
    }

    @Override
    public PasswordAlgo getPreferredPasswordAlgorythm() {
        return checkNotNull(preferredPasswordAlgorythm);
    }

}
