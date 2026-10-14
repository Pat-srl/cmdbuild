package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import java.util.Collection;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("sessions/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class SessionsWsV2 {

    private final SessionService sessionService;

    public SessionsWsV2(SessionService sessionService) {
        this.sessionService = checkNotNull(sessionService, "the session service channot be null");
    }

    @POST
    @Path(EMPTY)
    public Object create(WsSessionData sessionData) {
        String sessionId = sessionService.create(LoginDataImpl.builder()
                .withServiceUsersAllowed(true)
                .withLoginString(sessionData.getUsername())
                .withPassword(sessionData.getPassword())
                .withGroupName(sessionData.getRole())
                .build());
        Session session = sessionService.getSessionById(sessionId);
        return map("data", serializeSession(session));
    }

    @GET
    @Path("{id}/")
    public Object read(@PathParam("id") String id) {
        checkArgument(sessionService.exists(id), "session not found for id = %s", id);
        return map("data", serializeSession(sessionService.getSessionById(id)));
    }

    @PUT
    @Path("{id}/")
    public Object update(@PathParam("id") String id, WsSessionData session) {
        checkArgument(sessionService.exists(id), "session not found for id = %s", id);
        checkArgument(!isBlank(session.getRole()), "'role' param cannot be null");

        OperationUser currentOperationUser = sessionService.getUser(id);
        sessionService.update(id,
                LoginDataImpl.builder()
                        .withLoginString(currentOperationUser.getLoginUser().getUsername())
                        .withGroupName(session.getRole())
                        .withServiceUsersAllowed(true)
                        .build());

        return map("data", serializeSession(sessionService.getSessionById(id)));
    }

    @DELETE
    @Path("{id}/")
    public Object delete(@PathParam("id") String id) {
        checkArgument(sessionService.exists(id), "session not found for id = %s", id);
        sessionService.deleteSession(id);
        return success();
    }

    private CmMapUtils.FluentMap serializeSession(Session session) {
        OperationUser user = session.getOperationUser();
        return map(
                "_id", session.getSessionId(),
                "username", user.getUsername(),
                "role", user.getDefaultGroupNameOrNull(),
                "availableRoles", user.getGroupNames()
        );

    }

    public static class WsSessionData {

        public final String username, password, role;
        private Collection<String> availableRoles;

        public WsSessionData(@JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("role") String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        public Collection<String> getAvailableRoles() {
            return availableRoles;
        }

        void setAvailableRoles(final Collection<String> availableRoles) {
            this.availableRoles = availableRoles;
        }
    }

}
