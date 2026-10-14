package org.cmdbuild.email.mta;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface EmailMtaService {

    Email send(Email email);

    void receive(EmailReceiveConfig config);

    void receive(EmailAccount account, EmailReceiveConfig config);

    default Email receiveOne(EmailAccount account) {
        return getOnlyElement(receiveList(account, EmailReceiveConfigImpl.builder().build()));
    }

    default List<Email> receiveList(EmailAccount account) {
        return receiveList(account, EmailReceiveConfigImpl.builder().build());
    }

    default List<Email> receiveList(EmailAccount account, EmailReceiveConfig config) {
        List<Email> list = list();
        receive(account, EmailReceiveConfigImpl.copyOf(config).withCallback((Consumer< Email>) list::add).build());
        return list;
    }

    default void receive(EmailAccount account, String incomingFolder, @Nullable String receivedFolder, @Nullable String rejectedFolder, Consumer<Email> callback) {
        receive(account, EmailReceiveConfigImpl.builder().withIncomingFolder(incomingFolder).withReceivedFolder(receivedFolder).withRejectedFolder(rejectedFolder).withCallback(callback).build());
    }

    default List<Email> receive(EmailAccount account, String incomingFolder) {
        return receive(account, incomingFolder, null);
    }

    default List<Email> receive(EmailAccount account, String incomingFolder, @Nullable String receivedFolder) {
        return receiveList(account, EmailReceiveConfigImpl.builder().withIncomingFolder(incomingFolder).withReceivedFolder(receivedFolder).build());
    }

}
