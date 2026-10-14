/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Consumer;
import static org.cmdbuild.chat.ChatMessage.CHAT_MESSAGE_ATTR_TYPE;
import static org.cmdbuild.chat.ChatMessageStatus.CMS_ARCHIVED;
import static org.cmdbuild.chat.ChatMessageStatus.CMS_NEW;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.config.ChatConfiguration;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatConfiguration configuration;
    private final DaoService dao;

    public ChatMessageRepositoryImpl(ChatConfiguration configuration, DaoService dao) {
        this.configuration = checkNotNull(configuration);
        this.dao = checkNotNull(dao);
    }

    @ScheduledJob(value = "0 0 6 * * ?", clusterMode = RUN_ON_SINGLE_NODE) //run every day
    public void chatMessagesCleanup() {
        if (configuration.getArchivedMessageTimeToLive() != null) {
            dao.getJdbcTemplate().execute("UPDATE \"_ChatMessage\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND \"MessageStatus\" = %s AND \"Timestamp\" < %s"
                    .formatted(systemToSqlExpr(serializeEnum(CMS_ARCHIVED)), systemToSqlExpr(now().minus(configuration.getArchivedMessageTimeToLive()))));
        }
        if (configuration.getUnreadMessageTimeToLive() != null) {
            dao.getJdbcTemplate().execute("UPDATE \"_ChatMessage\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND \"MessageStatus\" = %s AND \"Timestamp\" < %s"
                    .formatted(systemToSqlExpr(serializeEnum(CMS_NEW)), systemToSqlExpr(now().minus(configuration.getUnreadMessageTimeToLive()))));
        }
    }

    @Override
    public PagedElements<ChatMessage> getMessagesForUser(String username, DaoQueryOptions options) {
        if (options.isPaged()) {
            List<ChatMessage> messages = dao.selectAll().from(ChatMessage.class).accept(filterMessagesForUser(username)).withOptions(options).asList();
            long count = dao.selectCount().from(ChatMessage.class).accept(filterMessagesForUser(username)).where(options.getFilter()).getCount();
            return paged(messages, count);
        } else {
            return paged(dao.selectAll().from(ChatMessage.class).accept(filterMessagesForUser(username)).withOptions(options).asList(ChatMessage.class));
        }
    }

    @Override
    public ChatMessage createMessage(ChatMessage message) {
        return dao.create(message);
    }

    @Override
    public ChatMessage updateMessage(ChatMessage message) {
        return dao.update(message);
    }

    @Override
    public ChatMessage getMessageByRecordId(long id) {
        return dao.getById(ChatMessage.class, id);
    }

    @Override
    public ChatMessage getMessageByMessageIdAndType(String messageId, ChatMessageType type) {
        return dao.selectAll().from(ChatMessage.class).where(ATTR_CODE, EQ, checkNotBlank(messageId)).where(CHAT_MESSAGE_ATTR_TYPE, EQ, serializeEnum(checkNotNull(type))).getOne();
    }

    @Override
    public void deleteMessage(ChatMessage message) {
        dao.delete(message);
    }

    private static Consumer<QueryBuilder> filterMessagesForUser(String username) {
        checkNotBlank(username);
        return q -> q.whereExpr("( \"MessageType\" = 'incoming' AND \"Target\" = ? ) OR ( \"MessageType\" = 'outgoing' AND \"SourceName\" = ? AND \"SourceType\" = 'user' )", username, username);
    }

}
