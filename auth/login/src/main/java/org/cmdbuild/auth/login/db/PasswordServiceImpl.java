/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.db;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static java.lang.Long.max;
import java.util.EnumSet;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordCheckStatus;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_HAS_VALID_PASSWORD;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_HAS_VALID_RECOVERY_TOKEN;
import org.cmdbuild.auth.login.PasswordEncodingService;
import org.cmdbuild.auth.login.PasswordManagementConfiguration;
import org.cmdbuild.auth.login.PasswordRecoveryService;
import org.cmdbuild.auth.login.PasswordService;
import org.cmdbuild.auth.user.UserData;
import static org.cmdbuild.auth.user.UserData.USER_ATTR_USERNAME;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PasswordServiceImpl implements PasswordService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PasswordManagementConfiguration pswConfig;
    private final PasswordEncodingService encodingService;
    private final DaoService dao;
    private final PasswordRecoveryService recoveryService;
    private final DbAuthenticator authenticator;
    private final UserRepository repository;

    public PasswordServiceImpl(PasswordManagementConfiguration pswConfig, PasswordEncodingService encodingService, DaoService dao, PasswordRecoveryService recoveryService, DbAuthenticator authenticator, UserRepository repository) {
        this.pswConfig = checkNotNull(pswConfig);
        this.encodingService = checkNotNull(encodingService);
        this.dao = checkNotNull(dao);
        this.recoveryService = checkNotNull(recoveryService);
        this.authenticator = checkNotNull(authenticator);
        this.repository = checkNotNull(repository);
    }

    @Override
    public String encryptPassword(String plaintextPassword) {
        return encodingService.encryptPassword(plaintextPassword);
    }

    @Override
    public boolean verifyPassword(String plaintextPassword, String storedEncryptedPassword) {
        return encodingService.verifyPassword(plaintextPassword, storedEncryptedPassword);
    }

    @Override
    public String decryptPasswordIfPossible(String password) {
        return encodingService.decryptPasswordIfPossible(password);
    }

    @Override
    public void verifyAndUpdatePasswordForUser(String username, String oldpassword, String password) {
        checkArgument(pswConfig.isPasswordChangeEnabled(), "CMO 100: password change is not enabled");
        PasswordCheckStatus result = checkPasswordForUser(LoginUserIdentity.build(username), oldpassword);
        checkArgument(EnumSet.of(PCR_HAS_VALID_PASSWORD, PCR_HAS_VALID_RECOVERY_TOKEN).contains(result), "CMO 100: invalid old password");
        verifyNewPasswordForPasswordUpdate(username, password);
        repository.update(UserDataImpl.copyOf(repository.getActiveUserData(LoginUserIdentity.build(username))).withPassword(encryptPassword(password)).withRecoveryToken(null).build());
    }

    @Override
    public void verifyNewPasswordForPasswordUpdate(@Nullable String username, String password) {
        if (pswConfig.isPasswordManagementEnabled()) {
            logger.debug("validate password =< %s > for user =< %s >", password, username);
            checkNotBlank(password, "CMO 100: new password cannot be blank");
            if (isNotBlank(username)) {
                checkArgument(pswConfig.getDifferentFromUsername() == false || !equal(username, password), "CMO 100: new password cannot be equal to username");
                if (pswConfig.getDifferentFromPrevious()) {
                    List<String> previousPasswords = dao.selectAll().from(UserData.class).where(USER_ATTR_USERNAME, EQ, username).includeHistory().orderBy(ATTR_BEGINDATE, DESC).asList(UserData.class).stream()
                            .map(UserData::getPassword).filter(StringUtils::isNotBlank).distinct().collect(toList());
                    previousPasswords.stream().limit(max(pswConfig.getDifferentFromPreviousCount(), 1))
                            .forEach(previousPassword -> checkArgument(!verifyPassword(password, previousPassword), "CMO 100: new password cannot be equal to one of the last %s previous passwords", pswConfig.getDifferentFromPreviousCount()));
                }
            }
            checkArgument(pswConfig.requireDigit() == false || password.matches(".*[0-9].*"), "CMO 100: new password must contain at least one digit");
            checkArgument(pswConfig.requireLowercase() == false || password.matches(".*[a-z].*"), "CMO 100: new password must contain at least one lowercase character");
            checkArgument(pswConfig.requireUppercase() == false || password.matches(".*[A-Z].*"), "CMO 100: new password must contain at least one uppercase character");
            checkArgument(isNullOrLtEqZero(pswConfig.getPasswordMinLength()) || password.trim().length() >= pswConfig.getPasswordMinLength(), "CMO 100: password must be at least %s characters long", pswConfig.getPasswordMinLength());
        }
    }

    @Override
    public void requirePasswordRecovery(String username) {
        recoveryService.requirePasswordRecovery(username);
    }

    @Override
    public PasswordCheckStatus checkPasswordForUser(LoginUserIdentity login, String password) {
        return authenticator.verifyPassword(login, password);
    }

}
