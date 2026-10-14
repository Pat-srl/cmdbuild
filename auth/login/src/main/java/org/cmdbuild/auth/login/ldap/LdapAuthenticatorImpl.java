package org.cmdbuild.auth.login.ldap;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.annotation.Nullable;
import java.io.IOException;
import static java.lang.String.format;
import static java.util.Collections.list;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.login.AuthenticationException;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordAuthenticator;
import org.cmdbuild.auth.login.PasswordCheckStatus;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_ACCESS_DENIED;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_HAS_VALID_PASSWORD;
import static org.cmdbuild.auth.utils.LdapUtils.encodeFilter;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LdapAuthenticatorImpl implements PasswordAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LdapAuthenticatorConfiguration configuration;

    public LdapAuthenticatorImpl(LdapAuthenticatorConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean isEnabled() {
        return configuration.isLdapEnabled();
    }

    @Override
    public PasswordCheckStatus verifyPassword(LoginUserIdentity login, String password) {
        try {
            logger.debug("trying to authenticate login = {} with ldap server = {}", login, getLdapUrl());
            String ldapUser = getLdapUserFullBindingName(login.getValue());
            if (isBlank(ldapUser)) {
                logger.debug("ldap user not found for login = {}", login);
                return PCR_ACCESS_DENIED;
            } else {
                logger.debug("found ldap user =< {} > for login = {}", ldapUser, login);
                boolean isPasswordValid = isLdapUserPasswordValid(ldapUser, password);
                if (isPasswordValid) {
                    logger.debug("successfully validated ldap user credentials for login = {}", login);
                    return PCR_HAS_VALID_PASSWORD;
                } else {
                    logger.debug("unable to validate ldap user credentials for login = {}", login);
                    return PCR_ACCESS_DENIED;
                }
            }
        } catch (Exception e) {
            logger.warn("ldap authentication error for login = {}", login, e);
            return PCR_ACCESS_DENIED;
        }
    }

    public void testLdapConnection() throws Exception {
        getLdapUserFullBindingName("admin");
    }

    @Nullable
    private String getLdapUserFullBindingName(String login) throws NamingException, IOException {
        LdapContext ctx = new InitialLdapContext(new Hashtable<>(getBaseCtxEnvParams().with(map().skipNullValues().with(
                Context.SECURITY_AUTHENTICATION, emptyToNull(configuration.getLdapAuthenticationMethod()),
                Context.SECURITY_PRINCIPAL, emptyToNull(configuration.getLdapPrincipal()),
                Context.SECURITY_CREDENTIALS, emptyToNull(configuration.getLdapPrincipalCredentials())).accept(m -> {
            if (configuration.enableLdapFollowReferrals()) {
                m.put(Context.REFERRAL, "follow");
            }
        }))), null);
        String searchFilter = null;
        try {
            handleStartTls(ctx);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            searchFilter = buildSearchFilterForUser(login);

            List<SearchResult> results = list(ctx.search(configuration.getLdapBaseDN(), searchFilter, searchControls));

            if (results.isEmpty()) {
                return null;
            } else {
                return getOnlyElement(results).getNameInNamespace();
            }
        } catch (Exception ex) {
            throw new AuthenticationException(ex, "error searching for user = %s with base ldn = %s and search filter = %s", login, configuration.getLdapBaseDN(), searchFilter);
        } finally {
            closeQuietly(ctx);
        }
    }

    private boolean isLdapUserPasswordValid(String ldapUserFullBindingName, String password) throws NamingException, IOException {
        LdapContext ctx = new InitialLdapContext(new Hashtable<>(getBaseCtxEnvParams().with(
                Context.SECURITY_AUTHENTICATION, "simple",
                Context.SECURITY_PRINCIPAL, ldapUserFullBindingName,
                Context.SECURITY_CREDENTIALS, password)), null);
        try {
            handleStartTls(ctx);
            try {
                ctx.getAttributes(ldapUserFullBindingName, null);
                return true;
            } catch (Exception e) {
                logger.debug("unable to validate ldap user psw for user = {}", ldapUserFullBindingName, e);
                return false;
            }
        } finally {
            closeQuietly(ctx);
        }
    }

    private String buildSearchFilterForUser(String login) {
        String bindAttr = checkNotBlank(configuration.getLdapBindAttribute(), "ldap bind attribute is null");
        if (isNotBlank(configuration.getLdapSearchFilter())) {
            return format("(&%s(%s=%s))", configuration.getLdapSearchFilter(), bindAttr, encodeFilter(login));
        } else {
            return format("(%s=%s)", bindAttr, encodeFilter(login));
        }
    }

    private FluentMap<String, String> getBaseCtxEnvParams() {
        return map(System.getProperties()).with(
                Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory",
                Context.PROVIDER_URL, checkNotBlank(getLdapUrl(), "missing ldap url config"));
    }

    private String getLdapUrl() {
        if (isNotBlank(configuration.getLdapServerUrl())) {
            return configuration.getLdapServerUrl();
        } else {
            return format("%s://%s:%s", configuration.enableLdapSsl() ? "ldaps" : "ldap", checkNotBlank(configuration.getLdapServerAddress()), checkNotNullAndGtZero(configuration.getLdapServerPort()));
        }
    }

    private void closeQuietly(DirContext ctx) {
        try {
            logger.trace("close ldap connection");
            ctx.close();
        } catch (Exception ex) {
            logger.debug("error closing ldap context", ex);
        }
    }

    private void handleStartTls(LdapContext ctx) throws NamingException, IOException {
        if (configuration.enableStartTls()) {
            logger.trace("start tls");
            StartTlsResponse tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
            tls.negotiate();
            logger.trace("tls ready");
        }
    }
}
