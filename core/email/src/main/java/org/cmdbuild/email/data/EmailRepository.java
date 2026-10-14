package org.cmdbuild.email.data;

import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.email.Email;

public interface EmailRepository {

    Email create(Email email);

    List<Email> getAllForCard(long reference, DaoQueryOptions queryOptions);

    @Nullable
    Email getOneOrNull(long emailId);

    Email update(Email email);

    void delete(Email email);

    List<Email> getAllForOutgoingProcessing();

    List<Email> getAllForErrorProcessing();

    List<Email> getByMessageId(String messageId);

    @Nullable
    Email getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(String from, String subject);

    List<Email> getAllForTemplate(long templateId);

    Email getLastReceivedEmail();
}
