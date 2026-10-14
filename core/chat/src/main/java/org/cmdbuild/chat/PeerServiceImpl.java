package org.cmdbuild.chat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import jakarta.annotation.Nullable;
import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.codec.binary.Base64.isBase64;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import static org.cmdbuild.auth.role.RolePrivilege.RP_CHAT_ACCESS;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.url.CmUrlUtils.isDataUrl;
import static org.cmdbuild.utils.url.CmUrlUtils.toDataUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PeerServiceImpl implements PeerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration coreConfiguration;
    private final ChatMessageRepository chatMessageRepository;
    private final OperationUserSupplier operationUser;
    private final UserRoleService userRepository;
    private final MultitenantService multitenantService;
    private final UserConfigService userConfigService;

    public PeerServiceImpl(CoreConfiguration coreConfiguration, ChatMessageRepository chatMessageRepository, OperationUserSupplier operationUser, UserRoleService userRepository, MultitenantService multitenantService, UserConfigService userConfigService) {
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.chatMessageRepository = checkNotNull(chatMessageRepository);
        this.operationUser = checkNotNull(operationUser);
        this.userRepository = checkNotNull(userRepository);
        this.multitenantService = checkNotNull(multitenantService);
        this.userConfigService = checkNotNull(userConfigService);
    }

    @Override
    public List<ChatPeer> getPeersForCurrentSession() {
        OperationUser currentUser = operationUser.getUser();

        Map<String, UserData> peers = currentUser.hasPrivileges(RP_CHAT_ACCESS) ? list(userRepository.getAllGroups()).filter(r -> r.hasPrivileges(RP_CHAT_ACCESS)).flatMap(r -> userRepository.getUserDataByRoleId(r.getId())).distinct(UserData::getUsername).collect(toMap(UserData::getUsername, identity())) : map();

        Multimap<String, ChatMessage> messagesByPeer = LinkedHashMultimap.create();
        chatMessageRepository.getMessagesForUser(currentUser.getUsername(), emptyOptions()).forEach(m -> {
            if (m.isIncoming()) {
                messagesByPeer.put(m.getSourceName(), m);
            } else {
                messagesByPeer.put(m.getTarget(), m);
            }
        });
        messagesByPeer.keys().stream().filter(not(peers::containsKey)).forEach(u -> Optional.ofNullable(userRepository.getUserDataByUsernameOrNull(u)).ifPresent(us -> peers.put(us.getUsername(), us)));
        peers.values().removeIf(u -> !u.isActive() || u.isService());
        peers.remove(currentUser.getUsername());
        if (multitenantService.isEnabled() && !currentUser.ignoreTenantPolicies()) {
            switch (coreConfiguration.getChatMultitenantMode()) {
                case CMM_TENANT -> {
                    Set<Long> availableTenants = currentUser.getLoginUser().getAvailableTenantContext().getAvailableTenantIds();
                    peers.values().removeIf(u -> {
                        UserAvailableTenantContext otherTenantContext = multitenantService.getAvailableTenantContextForUser(u.getId());
                        return !otherTenantContext.ignoreTenantPolicies() && Sets.intersection(otherTenantContext.getAvailableTenantIds(), availableTenants).isEmpty();
                    });
                }
            }
        }
        return list(peers.values()).sorted(UserData::getUsername).map(u -> {
            boolean hasMessages = !messagesByPeer.get(u.getUsername()).isEmpty();
            long count = messagesByPeer.get(u.getUsername()).stream().filter(ChatMessage::isNewMessage).count();
            ZonedDateTime lastMessageTimestamp = messagesByPeer.get(u.getUsername()).stream().sorted(Ordering.natural().onResultOf(ChatMessage::getTimestamp).reversed()).findFirst().map(ChatMessage::getTimestamp).orElse(null);
            return new ChatPeerImpl(u, hasMessages, count, lastMessageTimestamp);
        });
    }

    @Override
    @Nullable
    public String getIconUrl(ChatPeer peer) {
        try {
            String userIcon = userConfigService.getByUsernameOrNull(peer.getUsername(), "icon");
            if (isBlank(userIcon)) {
                return null;
            } else {
                checkArgument(isBase64(userIcon) || isDataUrl(userIcon), "invalid user icon for user =< %s > icon =< %s >", peer.getUsername(), abbreviate(userIcon));
                return isDataUrl(userIcon) ? userIcon : toDataUrl(Base64.decodeBase64(userIcon));
            }
        } catch (Exception ex) {
            logger.warn(marker(), "error processing icon for user = %s", peer, ex);
            return null;
        }
    }

    private static class ChatPeerImpl implements ChatPeer {

        private final UserData user;
        private final boolean hasMessages;
        private final long newMessageCount;
        private final ZonedDateTime lastMessageTimestamp;

        public ChatPeerImpl(UserData user, Boolean hasMessages, Long newMessageCount, @Nullable ZonedDateTime lastMessageTimestamp) {
            this.user = checkNotNull(user);
            this.hasMessages = hasMessages;
            this.newMessageCount = newMessageCount;
            this.lastMessageTimestamp = lastMessageTimestamp;
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public String getDescription() {
            return firstNotBlank(user.getDescription(), getUsername());
        }

        @Override
        public boolean hasMessages() {
            return hasMessages;
        }

        @Override
        public long getNewMessageCount() {
            return newMessageCount;
        }

        @Override
        @Nullable
        public ZonedDateTime getLastMessageTimestamp() {
            return lastMessageTimestamp;
        }

        @Override
        public String toString() {
            return "ChatPeer{" + "user=" + user + '}';
        }

    }

}
