/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.queue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import static java.lang.Integer.max;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailService.NewOutgoingEmailEvent;
import static org.cmdbuild.email.EmailStatus.ES_ERROR;
import static org.cmdbuild.email.EmailStatus.ES_SKIPPED;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.mta.EmailMtaService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.jobs.JobExecutorService;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_SYSTEM;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockScope;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import org.cmdbuild.minions.PostStartup;
import org.cmdbuild.scheduler.ScheduledJob;
import org.cmdbuild.syscommand.SysCommand;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExecutorUtils.waitFor;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailQueueServiceImpl implements EmailQueueService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LockService lockService;
    private final JobExecutorService jobExecutorService;
    private final EmailMtaService mtaService;
    private final EmailService emailService;
    private final EmailQueueConfiguration config;

    private final MinionHandler minionHandler;

    public EmailQueueServiceImpl(EmailMtaService mtaService, EmailService emailService, LockService lockService, JobExecutorService jobExecutorService, EmailQueueConfiguration emailConfiguration, EventBusService eventBusService) {
        this.mtaService = checkNotNull(mtaService);
        this.emailService = checkNotNull(emailService);
        this.lockService = checkNotNull(lockService);
        this.jobExecutorService = checkNotNull(jobExecutorService);
        this.config = checkNotNull(emailConfiguration);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Email Queue")
                .withConfigEnabler("org.cmdbuild.email.queue.enabled")
                .withEnabledChecker(config::isQueueEnabled)
                .withStatusChecker(() -> config.isQueueEnabled() ? MRS_READY : MRS_NOTRUNNING)
                .reloadOnConfigs(EmailQueueConfiguration.class)
                .build();
        emailService.getEventBus().register(new Object() {
            @Subscribe
            public void handleNewOutgoingEmailEvent(NewOutgoingEmailEvent event) {
                triggerEmailQueueIfEnabled();//TODO do not hard fail if queue lock is not available 
            }
        });
        eventBusService.getSysCommandEventBus().register(new Object() {

            private final static String EMAIL_QUEUE_TRIGGER = "email_queue_trigger";
            private final static String EMAIL_QUEUE_SEND_SINGLE = "email_queue_send_single";

            @Subscribe
            public void handleSysCommand(SysCommand command) {
                switch (command.getAction()) {
                    case EMAIL_QUEUE_TRIGGER -> {
                        logger.info("trigger email queue for sys command");
                        triggerEmailQueueIfEnabled();
                    }
                    case EMAIL_QUEUE_SEND_SINGLE -> {
                        long emailId = command.get("_email_id", Long.class);
                        logger.info("trigger email send for sys command, email = {}", emailId);
                        sendSingleEmailIfEnabled(emailId);
                    }
                }
            }
        });
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void triggerEmailQueue() {
        jobExecutorService.executeJobAs(() -> doProcessEmailQueue(), JOBUSER_SYSTEM);
    }

    @Override
    public void sendSingleEmail(long emailId) {
        Email email = emailService.getOne(emailId);
        checkArgument(email.isOutgoing(), "invalid email status = %s (expected outgoing)", serializeEnum(email.getStatus()));
        waitFor(jobExecutorService.executeJobAs(() -> doProcessEmailQueue(emailId), JOBUSER_SYSTEM));
    }

    @ScheduledJob(value = "0 */10 * * * ?", clusterMode = RUN_ON_SINGLE_NODE, user = JOBUSER_SYSTEM)
    @PostStartup(delay = "PT10s")
    public void processEmailQueue() {
        if (config.isQueueEnabled()) {
            doProcessEmailQueue();
        } else {
            logger.debug("email queue processing is disabled - skipping");
        }
    }

    private void triggerEmailQueueIfEnabled() {
        if (config.isQueueEnabled()) {
            triggerEmailQueue();
        } else {
            logger.info(marker(), "skip email processing (email queue processing is not enabled)");
        }
    }

    private void sendSingleEmailIfEnabled(long emailId) {
        if (config.isQueueEnabled()) {
            sendSingleEmail(emailId);
        } else {
            logger.info(marker(), "skip email processing (email queue processing is not enabled)");
        }
    }

    private void doProcessEmailQueue() {
        doProcessEmailQueue(null);
    }

    private synchronized void doProcessEmailQueue(@Nullable Long singleEmailId) {
        try {
            logger.debug("processing email queue");
            LockResponse lockResponse = aquireEmailQueueLock();
            if (lockResponse.isAquired()) {
                ItemLock lock = lockResponse.getLock();
                try {
                    List<Email> outgoing;
                    if (singleEmailId == null) {
                        outgoing = emailService.getAllForOutgoingProcessing();
                    } else {
                        Email email = emailService.getOneOrNull(singleEmailId);
                        if (email == null || !email.isOutgoing()) {
                            logger.debug("no outgoing email found for id = {}", singleEmailId);
                            outgoing = emptyList();
                        } else {
                            outgoing = singletonList(email);

                        }
                    }
                    if (!outgoing.isEmpty()) {
                        logger.info("processing {} outgoing email", outgoing.size());
                        outgoing.forEach(this::doSendEmailAndHandleErrors);
                    }
                } finally {
                    lockService.releaseLock(lock);
                }
            } else {
                logger.warn("unable to aquire queue lock, skip email queue processing");
            }
        } catch (Exception ex) {
            logger.error("error processing email queue", ex);
        }
    }

    private LockResponse aquireEmailQueueLock() {
        return lockService.aquireLockOrWait("org.cmdbuild.email.QUEUE", LockScope.LS_SESSION);//TODO expire this lock eventually
    }

    private void doSendEmailAndHandleErrors(Email email) {
        try {
            if (email.hasDestinationAddress()) {
//                email = cleanEmailContentBeforeSend(email);
                doSendEmail(email);
            } else {
                logger.warn(marker(), "email = {} has no destination addresses, skipping it", email);
                skipEmail(email);
            }
        } catch (Exception ex) {
            logger.error(marker(), "error sending email = {}", email, ex);
            int errors = email.getErrorCount() + 1;
            if (errors >= config.getMaxErrors()) {
                logger.info(marker(), "email = {} failed, setting email status to ERROR", email);
                emailService.update(EmailImpl.copyOf(email).withErrorCount(errors).withSentOrReceivedDate(now()).withStatus(ES_ERROR).build());
            } else {
                long delaySeconds = max(0, config.getMinRetryDelaySeconds());
                for (int i = 1; i < errors; i++) {
                    delaySeconds = (long) delaySeconds * 2;
                }
                delaySeconds = Long.min(delaySeconds, max(0, config.getMaxRetryDelaySeconds()));
                logger.info(marker(), "retrying email = {} after delay = {}", email, toUserDuration(delaySeconds * 1000));
                emailService.update(EmailImpl.copyOf(email).withErrorCount(errors).withDelay(delaySeconds).build());
            }
        }
    }

    private Email doSendEmail(Email email) {
        logger.debug("sending email = {}", email);
        email = mtaService.send(emailService.loadEmailAttachments(email));
        email = emailService.update(email);
        logger.info("sent email = {}", email);
        return email;
    }

//    private Email cleanEmailContentBeforeSend(Email email) {
//        if (email.getContent().contains(" data-type=\"signature\"")) {
//            return EmailImpl.copyOf(email).withContent(email.getContent().replaceAll(" data-type=\"signature\"", "")).build();
//        } else {
//            return email;
//        }
//    }
    private Email skipEmail(Email email) {
        logger.info("skip email = {}", email);
        return emailService.update(EmailImpl.copyOf(email).withSentOrReceivedDate(now()).withStatus(ES_SKIPPED).build());
    }

}
