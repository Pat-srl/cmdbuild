/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.function.Consumer;
import java.util.function.Function;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.email.Email;
import static org.cmdbuild.email.mta.EmailProcessedAction.EPA_DEFAULT;
import static org.cmdbuild.email.mta.EmailReceivedAction.ERA_DO_NOTHING;
import static org.cmdbuild.email.mta.EmailReceivedAction.ERA_MOVE_TO_RECEIVED;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class EmailReceiveConfigImpl implements EmailReceiveConfig {

    private final String incomingFolder, receivedFolder, rejectedFolder, account;
    private final Function<Email, EmailProcessedAction> callback;
    private final EmailReceivedAction action;

    private EmailReceiveConfigImpl(EmailReceiveConfigImplBuilder builder) {
        this.incomingFolder = firstNotBlank(builder.incomingFolder, INCOMING_FOLDER_DEFAULT);
        this.rejectedFolder = builder.rejectedFolder;
        this.account = builder.account;
        this.callback = checkNotNull(builder.callback);
        this.action = firstNotNull(builder.action, isBlank(builder.receivedFolder) ? ERA_DO_NOTHING : ERA_MOVE_TO_RECEIVED);
        switch (action) {
            case ERA_DELETE:
            case ERA_DO_NOTHING:
                this.receivedFolder = null;
                break;
            case ERA_MOVE_TO_RECEIVED:
                this.receivedFolder = checkNotBlank(builder.receivedFolder);
                break;
            default:
                throw new IllegalArgumentException("unsupported email post-receive action = " + action);
        }
    }

    @Override
    public String getIncomingFolder() {
        return incomingFolder;
    }

    @Override
    @Nullable
    public String getReceivedFolder() {
        return receivedFolder;
    }

    @Override
    @Nullable
    public String getRejectedFolder() {
        return rejectedFolder;
    }

    @Override
    @Nullable
    public String getAccount() {
        return account;
    }

    @Override
    public Function<Email, EmailProcessedAction> getCallback() {
        return callback;
    }

    @Override
    public EmailReceivedAction getReceivedEmailAction() {
        return action;
    }

    public static EmailReceiveConfigImplBuilder builder() {
        return new EmailReceiveConfigImplBuilder();
    }

    public static EmailReceiveConfigImplBuilder copyOf(EmailReceiveConfig source) {
        return new EmailReceiveConfigImplBuilder()
                .withIncomingFolder(source.getIncomingFolder())
                .withReceivedFolder(source.getReceivedFolder())
                .withRejectedFolder(source.getRejectedFolder())
                .withCallback(source.getCallback())
                .withAction(source.getReceivedEmailAction())
                .withAccount(source.getAccount());
    }

    public static class EmailReceiveConfigImplBuilder implements Builder<EmailReceiveConfigImpl, EmailReceiveConfigImplBuilder> {

        private String incomingFolder;
        private String receivedFolder;
        private Function<Email, EmailProcessedAction> callback = (e) -> EPA_DEFAULT;
        private String rejectedFolder, account;
        private EmailReceivedAction action;

        public EmailReceiveConfigImplBuilder withIncomingFolder(String incomingFolder) {
            this.incomingFolder = incomingFolder;
            return this;
        }

        public EmailReceiveConfigImplBuilder withReceivedFolder(String receivedFolder) {
            this.receivedFolder = receivedFolder;
            return this;
        }

        public EmailReceiveConfigImplBuilder withRejectedFolder(String rejectedFolder) {
            this.rejectedFolder = rejectedFolder;
            return this;
        }

        public EmailReceiveConfigImplBuilder withAccount(String account) {
            this.account = account;
            return this;
        }

        public EmailReceiveConfigImplBuilder withCallback(Function<Email, EmailProcessedAction> callback) {
            this.callback = callback;
            return this;
        }

        public EmailReceiveConfigImplBuilder withCallback(Consumer<Email> callback) {
            this.callback = (e) -> {
                callback.accept(e);
                return EPA_DEFAULT;
            };
            return this;
        }

        public EmailReceiveConfigImplBuilder withAction(EmailReceivedAction action) {
            this.action = action;
            return this;
        }

        @Override
        public EmailReceiveConfigImpl build() {
            return new EmailReceiveConfigImpl(this);
        }

    }
}
