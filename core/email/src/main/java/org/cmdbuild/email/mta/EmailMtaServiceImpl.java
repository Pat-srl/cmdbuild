package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import jakarta.mail.MessagingException;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.email.event.EmailAccountCreateEvent;
import org.cmdbuild.email.event.EmailAccountDeleteEvent;
import org.cmdbuild.email.event.EmailAccountUpdateEvent;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class EmailMtaServiceImpl implements EmailMtaService {

    public static final String EMAIL_HEADER_MESSAGE_ID = "Message-ID",
            EMAIL_HEADER_REFERENCES = "References",
            EMAIL_HEADER_X_MAILER = "X-Mailer",
            EMAIL_HEADER_IN_REPLY_TO = "In-Reply-To";

    private final EmailAccountService emailAccountService;

    // Sender stuff
    private final EmailQueueConfiguration queueConfig;
    private final InstanceInfoService infoService;
    private final EmailSignatureService signatureService;

    // Receiver stuff
    private final LockService lockService;

    private final EmailRepository repository;

    // Strategy to build proper sender/receiver
    private final EmailProviderStrategy emailProviderStrategy;

    private final CmCache<EmailSenderProvider> emailSenderProviders;
    private final CmCache<EmailReceiverProvider> emailReceiverProviders;

    public EmailMtaServiceImpl(EmailAccountService emailAccountService, LockService lockService, EmailQueueConfiguration queueConfig, EmailRepository repository, EmailSignatureService signatureService, InstanceInfoService infoService, CacheService cacheService, EventBusService eventBusService) {
        this.emailAccountService = checkNotNull(emailAccountService);

        // Sender stuff
        this.queueConfig = checkNotNull(queueConfig);
        this.infoService = checkNotNull(infoService);
        this.signatureService = checkNotNull(signatureService);

        // Receiver stuff
        this.lockService = checkNotNull(lockService);
        this.repository = checkNotNull(repository);

        this.emailProviderStrategy = new EmailProviderStrategy();

        this.emailSenderProviders = cacheService.newCache("email_sender_providers_all");
        this.emailReceiverProviders = cacheService.newCache("email_receiver_providers_all");

        eventBusService.getEmailAccountEventBus().register(new Object() {
            @Subscribe
            public void handleEmailAccountCreate(EmailAccountCreateEvent event) {
                emailSenderProviders.invalidate(event.getEmailAccount().getName());
                emailReceiverProviders.invalidate(event.getEmailAccount().getName());
            }

            @Subscribe
            public void handleEmailAccountUpdate(EmailAccountUpdateEvent event) {
                emailSenderProviders.invalidate(event.getEmailAccount().getName());
                emailReceiverProviders.invalidate(event.getEmailAccount().getName());
            }

            @Subscribe
            public void handleEmailAccountDelete(EmailAccountDeleteEvent event) {
                emailSenderProviders.invalidate(event.getEmailAccount().getName());
                emailReceiverProviders.invalidate(event.getEmailAccount().getName());
            }
        }
        );
    }

    @Override
    public Email send(Email email) {
        try {
            EmailAccount emailAccount;
            if (email.getAccount() == null) {
                emailAccount = checkNotNull(emailAccountService.getDefaultOrNull(), "no account supplied for email, and no default account found");
            } else {
                emailAccount = emailAccountService.getAccount(email.getAccount());
            }

            EmailSenderProvider senderProvider = emailSenderProviders.get(emailAccount.getName(), () -> emailProviderStrategy.buildSender(emailAccount, queueConfig, infoService, signatureService));
            return senderProvider.sendEmail(email);
        } catch (MessagingException ex) {
            throw new EmailException(ex);
        }
    }

    @Override
    public void receive(EmailReceiveConfig config) {
        EmailAccount emailAccount = emailAccountService.getAccount(checkNotBlank(config.getAccount(), "missing account param"));
        receive(emailAccount, config);
    }

    @Override
    public void receive(EmailAccount emailAccount, EmailReceiveConfig receiveConfig) {
        try {
            EmailReceiverProvider receiverProvider = emailReceiverProviders.get(emailAccount.getName(), () -> emailProviderStrategy.buildReceiver(emailAccount, lockService, repository));
            receiverProvider.receiveMails(receiveConfig);
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error receiving email for account = %s with folder = %s", emailAccount, receiveConfig.getIncomingFolder());
        }
    }

}
