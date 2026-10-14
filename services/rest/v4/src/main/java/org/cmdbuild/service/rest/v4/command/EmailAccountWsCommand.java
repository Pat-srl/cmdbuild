/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Supplier;
import java.util.List;
import java.util.function.Function;
import org.cmdbuild.config.EmailConfigurationImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.*;
import static org.cmdbuild.email.EmailAccount.*;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.email.mta.*;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockScope;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.service.rest.v4.model.WsEmailAccountData;
import static org.cmdbuild.utils.crypto.PasswordBulletsUtils.handleBullets;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.springframework.stereotype.Component;

/**
 *
 * @author schursin
 */
@Component
public class EmailAccountWsCommand {

    private final EmailAccountService emailAccountService;

    public EmailAccountWsCommand(EmailAccountService emailAccountService) {
        this.emailAccountService = checkNotNull(emailAccountService);
    }

    public List<EmailAccount> doReadAll(Supplier<List<EmailAccount>> function) {
        return function.get();
    }

    public EmailAccount doRead(String idOrCode) {
        return emailAccountService.getAccountByIdOrCode(idOrCode);
    }

    public EmailAccount doCreate(WsEmailAccountData data) {
        return emailAccountService.create(data.toEmailAccount().build());
    }

    public EmailAccount doUpdate(Long id, WsEmailAccountData data) {
        return emailAccountService.update(data.toEmailAccount().withId(id).build());
    }

    public void doDelete(Long id) {
        emailAccountService.delete(id);
    }

    public Object doTestExistingAccount(String idOrCode, WsEmailAccountData data) {
        EmailAccount account = emailAccountService.getAccountByIdOrCode(idOrCode);
        if (data != null) {
            account = data.toEmailAccount().withPassword(handleBullets(data.getPassword(), account::getPassword)).build();
        }
        return doTestAccountConfig(account);
    }

    public Object doTestAccountConfig(EmailAccount emailAccount) {
        EmailProviderStrategy emailProviderStrategy = new EmailProviderStrategy();

        EmailProvider emailProvider;
        if (emailAccount.isSmtpConfigured()) {
            emailProvider = emailProviderStrategy.buildSender(emailAccount, new EmailConfigurationImpl(), new MockInstanceInfoService(), new MockEmailSignatureService());
        } else {
            emailProvider = emailProviderStrategy.buildReceiver(emailAccount, new MockLockServiceImpl(), new MockEmailRepositoryImpl());
        }

        return response(map().accept(m -> {
            switch (emailAccount.getAuthenticationType()) {
                case AUTHENTICATION_TYPE_MS_OAUTH2 -> {
                    emailProvider.testConnection();
                    m.put("msgraph", true);
                }
                case AUTHENTICATION_TYPE_DEFAULT, AUTHENTICATION_TYPE_GOOGLE_OAUTH2 -> {
                    if (emailAccount.isImapConfigured()) {
                        emailProvider.testConnection();
                        m.put("imap", true);
                    }
                    if (emailAccount.isSmtpConfigured()) {
                        emailProvider.testConnection();
                        m.put("smtp", true);
                    }
                }
            }
        }));
    }

    private static class MockEmailReceiveConfigImpl implements EmailReceiveConfig {

        public MockEmailReceiveConfigImpl() {
            super();
        }

        @Override
        public String getIncomingFolder() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getReceivedFolder() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getRejectedFolder() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Function<Email, EmailProcessedAction> getCallback() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getAccount() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailReceivedAction getReceivedEmailAction() {
            return EmailReceivedAction.ERA_DO_NOTHING;
        }
    } // end MockEmailReceiveConfigImpl class

    private static class MockLockServiceImpl implements LockService {

        public MockLockServiceImpl() {
            super();
        }

        @Override
        public LockResponse aquireLock(String itemId, LockScope lockScope) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public LockResponse aquireLockOrWait(String itemId, LockScope lockScope, long waitForMillis) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public ItemLock getLockOrNull(String itemId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void releaseLock(ItemLock itemLock) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void deleteLock(String lockId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void releaseAllLocks() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<ItemLock> getAllLocks() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void requireNotLockedByOthers(String itemId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void requireLockedByCurrent(String itemId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public LockResponse aquireLockOrWait(String itemId, LockScope lockScope) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public LockResponse aquireLockTimeToLiveSeconds(String itemId, LockScope lockScope, int timeToLiveSeconds) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    } // end MockLockServiceImpl class

    private static class MockInstanceInfoService implements InstanceInfoService {

        public MockInstanceInfoService() {
        }

        @Override
        public String getVersion() {
            return "aVersion";
        }

        @Override
        public String getRevision() {
            return "aRevision";
        }

        @Override
        public String getNodeId() {
            return "aNodeId";
        }

        @Override
        public String getInstanceName() {
            return "aInstanceName";
        }
    } // end MockInstanceInfoService class

    private static class MockEmailSignatureService implements EmailSignatureService {

        public MockEmailSignatureService() {
        }

        @Override
        public List<EmailSignature> getAll() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailSignature getOneByCode(String code) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailSignature getOne(long id) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailSignature create(EmailSignature emailSignature) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailSignature update(EmailSignature emailSignature) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void delete(long id) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getSignatureHtmlForCurrentUser(long id) {
            return "<i>aSignatureHtmlForCurrentUser<i>";
        }
    } // end MockEmailSignatureService class

    private static class MockEmailRepositoryImpl implements EmailRepository {

        @Override
        public Email create(Email email) {
            return email;
        }

        @Override
        public List<Email> getAllForCard(long reference, DaoQueryOptions queryOptions) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Email getOneOrNull(long emailId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Email update(Email email) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void delete(Email email) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<Email> getAllForOutgoingProcessing() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<Email> getAllForErrorProcessing() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<Email> getByMessageId(String messageId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Email getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(String from, String subject) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<Email> getAllForTemplate(long templateId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Email getLastReceivedEmail() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    } // end MockEmailRepositoryImpl class
}
