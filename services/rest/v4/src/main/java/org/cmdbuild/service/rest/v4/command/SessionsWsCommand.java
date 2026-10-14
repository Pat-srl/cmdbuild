/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.collect.Ordering;
import jakarta.servlet.http.HttpServletRequest;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import org.cmdbuild.auth.session.OperatorService;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.SessionType;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.fault.FaultLevel;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardSerializer;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.model.WsSessionData;
import org.cmdbuild.service.rest.v4.wshelpers.SessionWsCommons;
import org.cmdbuild.services.rest.v4.config.WsSessionConfig;
import org.cmdbuild.services.serialization.CompositeDataSerializer;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.fault.FaultUtils.buildMessageListForResponse;
import static org.cmdbuild.fault.FaultUtils.exceptionToUserMessage;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Component
public class SessionsWsCommand extends SessionWsCommons {

    public static final String SESSION_NOT_FOUND_FOR_ID_S = "session not found for id = %s";
    public static final String MESSAGES = "messages";
    private final CoreConfiguration coreConfiguration;
    private final CardWsSerializationHelperv3 cardWsSerializationHelper;
    private final OperatorService operatorService;
    private final UserCardService userCardService;

    public SessionsWsCommand(
            SessionService sessionService,
            CoreConfiguration coreConfiguration,
            CardWsSerializationHelperv3 cardWsSerializationHelper,
            OperatorService operatorService,
            UserCardService userCardService
    ) {
        super(sessionService);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.cardWsSerializationHelper = checkNotNull(cardWsSerializationHelper);
        this.operatorService = checkNotNull(operatorService);
        this.userCardService = checkNotNull(userCardService);
    }

    public String doCreateSessionId(WsSessionData wsSessionData, SessionType sessionType, Boolean serviceUsersAllowed) {
        return sessionService.create(LoginDataImpl.builder()
                .withLoginString(wsSessionData.getUsername())
                .withPassword(wsSessionData.getPassword())
                .withGroupName(wsSessionData.getRole())
                .withServiceUsersAllowed(serviceUsersAllowed)
                .withIgnoreTenantPolicies(wsSessionData.ignoreTenants)
                .withTargetDevice(wsSessionData.device)
                .withSessionType(sessionType)
                .build());
    }

    public Session doReadOne(String sessionId) {
        return sessionService.getSessionById(sessionId);
    }

    public Set<Map.Entry<String, UserPrivilegesForObject>> doReadPrivileges(String sessionId) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), SESSION_NOT_FOUND_FOR_ID_S, sessionId);
        OperationUser user = sessionService.getUser(sessionId);
        return user.getPrivilegeContext().getAllPrivileges().entrySet();
    }

    public List<Session> doReadAll() {
        return sessionService.getAllSessions().stream().sorted(Ordering.natural().onResultOf(Session::getLastActiveDate).reversed()).toList();
    }

    public Session doUpdate(String sessionId, WsSessionData sessionData) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), SESSION_NOT_FOUND_FOR_ID_S, sessionId);
        checkArgument(!isBlank(sessionData.getRole()), "'group' param cannot be null");
        OperationUser currentOperationUser = sessionService.getUser(sessionId);
        sessionService.update(sessionId, LoginDataImpl.builder()
                .withLoginString(currentOperationUser.getLoginUser().getUsername())
                .withGroupName(sessionData.getRole())
                .withDefaultTenant(sessionData.getDefaultTenant())
                .withActiveTenants(sessionData.getActiveTenants())
                .withIgnoreTenantPolicies(sessionData.ignoreTenants)
                .withTargetDevice(sessionData.device)
                .withServiceUsersAllowed(true)//TODO use scope
                .build());
        return sessionService.getSessionById(sessionId);
    }

    public RequestAuthenticatorResponse<Void> doDelete(HttpServletRequest request, String sessionId) {
        sessionId = sessionIdOrCurrent(sessionId);
        checkArgument(sessionService.exists(sessionId), SESSION_NOT_FOUND_FOR_ID_S, sessionId);
        return sessionService.deleteSession(sessionId, request);
    }

    public void doDeleteAll() {
        logger.info("delete all sessions");
        sessionService.deleteAll();
    }

    public CmMapUtils.FluentMap<String, Object> doKeepAlive() {
        return map("timeToLiveSeconds", coreConfiguration.getSessionTimeoutOrDefault(), "recommendedKeepaliveIntervalSeconds", coreConfiguration.getSessionTimeoutOrDefault() / 3);
    }

    public void doConfigureSession(WsSessionConfig sessionConfig) {
        checkCanEdit();
        switch (sessionConfig.sessionUserMappingMode) {
            case SUM_DISABLED -> sessionService.disableUserMappingMode();
            case SUM_CMDBUILD_CLASS -> sessionService.enableUserMappingClassMode(sessionConfig.userMappingClass, sessionConfig.userMappingAttribute);
            case SUM_DB_FUNCTION -> sessionService.enableUserMappingFunctionMode();
            default -> throw new IllegalArgumentException("invalid sessionUserMappingMode = " + sessionConfig.sessionUserMappingMode);
        }
    }

    public CmMapUtils.FluentMap<String, Object> doReadOperatorData() {
        OperationUser user = sessionService.getCurrentSession().getOperationUser();
        CardSerializer cardSerializer = new CardSerializer(cardWsSerializationHelper);
        CmMapUtils.FluentMap<String, Object> m = map();
        try {
            Map<String, Object> operatorData = operatorService.getProperties(LoginUserIdentity.build(user.getUsername()));
            if ((operatorData != null) && (!operatorData.isEmpty())) {
                String userCardClassId = operatorData.getOrDefault(ATTR_IDCLASS, "").toString().replaceAll("^\"|\"$", "");
                long userCardId = Long.parseLong(operatorData.getOrDefault(ATTR_ID, "").toString());
                Card userCard = userCardService.getUserCard(userCardClassId, userCardId);
                CmMapUtils.FluentMap<String, Object> serialization = new CompositeDataSerializer(cardSerializer).serialize(userCard);
                m.put("operatorDetailedData", serialization);
            } else {
                String reason = operatorService.getReasonForUnsuccessfulSearch();
                m.with(MESSAGES, buildMessageListForResponse(FaultLevel.FL_WARNING, true, reason));
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            m.with(MESSAGES, buildMessageListForResponse(FaultLevel.FL_WARNING, true, "User card id is not a valid number: " + illegalArgumentException.getMessage()));
        } catch (Exception e) {
            String reason = operatorService.getReasonForUnsuccessfulSearch();
            m.with(MESSAGES, buildMessageListForResponse(FaultLevel.FL_WARNING, true, reason));
        }
        return m;
    }

    private void checkCanEdit() {
        checkArgument(coreConfiguration.allowConfigUpdateViaWs(), "CM_CUSTOM_EXCEPTION: system configuration update is disabled for this instance (demo mode)");//TODO check message
    }

}
