/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.cmdbuild.email.Email;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_EMAIL;
import static org.cmdbuild.email.EmailStatus.ES_ERROR;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import static org.cmdbuild.email.EmailStatus.ES_RECEIVED;
import static org.cmdbuild.email.EmailStatus.serializeEmailStatus;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class EmailRepositoryImpl implements EmailRepository {

    private final DaoService dao;

    public EmailRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public List<Email> getAllForCard(long reference, DaoQueryOptions queryOptions) {
        queryOptions = queryOptions.mapAttrNames(map("status", "EmailStatus", "date", "EmailDate"));
        return dao.selectAll()
                .from(EmailImpl.class)
                .orderBy(queryOptions.getSorter())
                .where(queryOptions.getFilter())
                .where("Card", EQ, reference).asList();
    }

    @Override
    @Nullable
    public Email getOneOrNull(long emailId) {
        return dao.getByIdOrNull(EmailImpl.class, emailId);
    }

    @Override
    public List<Email> getAllForTemplate(long templateId) {
        return dao.selectAll().from(Email.class).where("Template", EQ, templateId).asList();
    }

    @Override
    public List<Email> getByMessageId(String messageId) {
        return dao.selectAll().from(Email.class).where("MessageId", EQ, checkNotBlank(messageId)).asList();
    }

    @Override
    public Email create(Email email) {
        checkArgument(email.hasNotificationProvider(NOTIFICATION_PROVIDER_EMAIL));
        return dao.create(email);
    }

    @Override
    public Email update(Email email) {
        checkArgument(email.hasNotificationProvider(NOTIFICATION_PROVIDER_EMAIL));
        return dao.update(email);
    }

    @Override
    public void delete(Email email) {
        dao.delete(email);
    }

    @Override
    public List<Email> getAllForOutgoingProcessing() {
        return dao.selectAll().from(Email.class)
                .where("EmailStatus", EQ, serializeEmailStatus(ES_OUTGOING))
                .whereExpr("\"Delay\" IS NULL OR \"Delay\" <= 0 OR \"BeginDate\" < ( NOW() - ( \"Delay\"::varchar || ' seconds' )::interval )")
                .orderBy(ATTR_BEGINDATE, ASC)
                .asList();
    }

    @Override
    public List<Email> getAllForErrorProcessing() {
        return dao.selectAll().from(Email.class)
                .where("EmailStatus", EQ, serializeEmailStatus(ES_ERROR))
                .orderBy(ATTR_BEGINDATE, ASC)
                .asList();
    }

    @Override
    @Nullable
    public Email getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(String email, String subject) {
        if (isBlank(subject) || isBlank(email)) {
            return null;
        }
        String pattern = "(?i)^ *((RE|FWD?|R|RIF|I) *: *)*";
        return dao.selectAll().from(Email.class)
                .whereExpr("lower(trim(regexp_replace(\"Subject\", ?, ''))) = lower(trim(regexp_replace(?, ?, '')))", pattern, subject, pattern)
                .whereExpr("_cm3_email_utils_field_contains_value(\"ToAddresses\", ?) OR _cm3_email_utils_field_contains_value(\"CcAddresses\", ?) OR _cm3_email_utils_field_contains_value(\"BccAddresses\", ?)", email, email, email)
                .orderBy(ATTR_BEGINDATE, DESC).limit(1).getOneOrNull();
    }

    @Override
    public Email getLastReceivedEmail() {
        return dao.selectAll().from(Email.class).where("EmailStatus", EQ, serializeEmailStatus(ES_RECEIVED)).orderBy(ATTR_BEGINDATE, DESC).limit(1).getOne();
    }

}
