/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.mobile;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Consumer;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.config.MobileConfiguration;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_ARCHIVED;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.springframework.stereotype.Component;

@Component
public class MobileAppMessageRepositoryImpl implements MobileAppMessageRepository {

    private final MobileConfiguration configuration;
    private final DaoService dao;

    public MobileAppMessageRepositoryImpl(MobileConfiguration configuration, DaoService dao) {
        this.configuration = checkNotNull(configuration);
        this.dao = checkNotNull(dao);
    }

    @ScheduledJob(value = "0 0 6 * * ?", clusterMode = RUN_ON_SINGLE_NODE) //run every day
    public void mobileAppMessagesCleanup() {
        if (configuration.getArchivedMessageTimeToLive() != null) {
            dao.getJdbcTemplate().execute("UPDATE \"_MobileAppMessage\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND \"MessageStatus\" = %s AND \"Timestamp\" < %s"
                    .formatted(systemToSqlExpr(serializeEnum(MAMS_ARCHIVED)), systemToSqlExpr(now().minus(configuration.getArchivedMessageTimeToLive()))));
        }
    }

    @Override
    public PagedElements<MobileAppMessage> getMessagesForUser(String username, DaoQueryOptions options) {
        if (options.isPaged()) {
            List<MobileAppMessage> messages = dao.selectAll().from(MobileAppMessage.class).accept(filterMessagesForUser(username)).withOptions(options).asList();
            long count = dao.selectCount().from(MobileAppMessage.class).accept(filterMessagesForUser(username)).where(options.getFilter()).getCount();
            return paged(messages, options.getOffset(), count);
        } else {
            return paged(dao.selectAll().from(MobileAppMessage.class).accept(filterMessagesForUser(username)).withOptions(options).asList(MobileAppMessage.class));
        }
    }

    @Override
    public MobileAppMessage createMessage(MobileAppMessage message) {
        return dao.create(message);
    }

    @Override
    public MobileAppMessage updateMessage(MobileAppMessage message) {
        return dao.update(message);
    }

    @Override
    public MobileAppMessage getMessageByRecordId(long id) {
        return dao.getById(MobileAppMessage.class, id);
    }

    @Override
    public MobileAppMessage getMessageByMessageId(String messageId) {
        return dao.selectAll().from(MobileAppMessage.class).where(ATTR_CODE, EQ, checkNotBlank(messageId)).getOne();
    }

    @Override
    public void deleteMessage(MobileAppMessage message) {
        dao.delete(message);
    }

    private static Consumer<QueryBuilder> filterMessagesForUser(String username) {
        checkNotBlank(username);
        return q -> q.whereExpr("\"Target\" = ?", username);
    }

}
