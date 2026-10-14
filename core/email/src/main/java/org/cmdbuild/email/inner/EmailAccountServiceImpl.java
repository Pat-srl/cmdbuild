/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import java.util.Optional;

import com.google.common.eventbus.EventBus;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import org.cmdbuild.email.event.EmailAccountCreateEvent;
import org.cmdbuild.email.event.EmailAccountDeleteEvent;
import org.cmdbuild.email.event.EmailAccountUpdateEvent;
import org.cmdbuild.eventbus.EventBusService;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.email.beans.EmailAccountImpl;
import static org.cmdbuild.utils.crypto.PasswordBulletsUtils.handleBullets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EmailAccountServiceImpl implements EmailAccountService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailQueueConfiguration config;
    private final DaoService dao;
    private final Holder<List<EmailAccount>> allAccountCache;
    private final CmCache<Optional<EmailAccount>> accountByNameCache;
    private final CmCache<EmailAccount> accountByIdCache;
    private final EventBus emailAccountEventBus;

    public EmailAccountServiceImpl(EmailQueueConfiguration config, DaoService dao, CacheService cacheService, EventBusService eventBusService) {
        this.dao = checkNotNull(dao);
        this.config = checkNotNull(config);
        this.allAccountCache = cacheService.newHolder("email_account_all", CacheConfig.SYSTEM_OBJECTS);
        this.accountByNameCache = cacheService.newCache("email_account_by_name", CacheConfig.SYSTEM_OBJECTS);
        this.accountByIdCache = cacheService.newCache("email_account_by_id", CacheConfig.SYSTEM_OBJECTS);
        this.emailAccountEventBus = eventBusService.getEmailAccountEventBus();

    }

    private void invalidateCache() {
        allAccountCache.invalidate();
        accountByNameCache.invalidateAll();
        accountByIdCache.invalidateAll();
    }

    @Override
    public List<EmailAccount> getAll() {
        return allAccountCache.get(this::doGetAll);
    }

    @Override
    @Nullable
    public EmailAccount getAccountOrNull(String name) {
        return accountByNameCache.get(checkNotBlank(name), () -> Optional.ofNullable(doGetAccountOrNull(name))).orElse(null);
    }

    @Override
    public EmailAccount getAccount(long id) {
        return accountByIdCache.get(Long.toString(id), () -> doGetAccount(id));
    }

    @Override
    @Nullable
    public EmailAccount getDefaultOrNull() {
        if (isBlank(config.getDefaultEmailAccountCode())) {
            return null;
        } else {
            EmailAccount account = getAccountOrNull(config.getDefaultEmailAccountCode());
            if (account == null) {
                logger.warn(marker(), "default email account not found for config code =< {} >", config.getDefaultEmailAccountCode());
            }
            return account;
        }
    }

    @Override
    public EmailAccount create(EmailAccount account) {
        account = dao.create(account);
        invalidateCache();
        emailAccountEventBus.post(new EmailAccountCreateEvent(account));
        return account;
    }

    @Override
    public EmailAccount update(EmailAccount newAccount) {
        EmailAccount account = dao.update(EmailAccountImpl.copyOf(newAccount)
                .withPassword(handleBullets(newAccount.getPassword(), () -> dao.getById(EmailAccount.class, newAccount.getId()).getPassword())).build());
        invalidateCache();
        emailAccountEventBus.post(new EmailAccountUpdateEvent(account));
        return account;
    }

    @Override
    public void delete(long accountId) {
        EmailAccount account = getAccount(accountId);
        dao.delete(EmailAccount.class, accountId);
        invalidateCache();
        emailAccountEventBus.post(new EmailAccountDeleteEvent(account));
    }

    private List<EmailAccount> doGetAll() {
        return dao.selectAll().from(EmailAccount.class).asList();
    }

    private @Nullable
    EmailAccount doGetAccountOrNull(String name) {
        return getAll().stream().filter((a) -> equal(a.getName(), name)).collect(toOptional()).orElse(null);
    }

    private EmailAccount doGetAccount(long id) {
        return checkNotNull(getAll().stream().filter((a) -> equal(a.getId(), id)).collect(toOptional()).orElse(null), "account not found for id = %s", id);
    }

}
