/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;

@Path("/locks")
@Tag(name = "Locks", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete a lock")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ACCESS_AUTHORITY)
public class LockWs {

    private final org.cmdbuild.service.rest.v4.endpoint.LockWs lockWs;

    public LockWs(org.cmdbuild.service.rest.v4.endpoint.LockWs lockWs) {
        this.lockWs = checkNotNull(lockWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get locks",
            description = "Get locks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of locks data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getLocks() {
        return lockWs.getLocks();
    }

    @GET
    @Path("/{lockId}")
    @Operation(
            summary = "Get lock",
            description = "Get lock",
            parameters = {@Parameter(name = "lockId", in = ParameterIn.PATH, description = "Identifier of the lock to retrieve", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lock data"),
                    @ApiResponse(responseCode = "404", description = "Lock not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getLock(
            @PathParam("lockId") String lockId
    ) {
        return lockWs.getLock(lockId);
    }

    @DELETE
    @Path("/{lockId}")
    @Operation(
            summary = "Delete lock",
            description = "Delete lock",
            parameters = {@Parameter(name = "lockId", in = ParameterIn.PATH, description = "Identifier of the lock to delete", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of lock"),
                    @ApiResponse(responseCode = "404", description = "Lock not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteLock(
            @PathParam("lockId") String lockId
    ) {
        return lockWs.deleteLock(lockId);
    }

    @DELETE
    @Path("/_ANY")
    @Operation(
            summary = "Delete all locks",
            description = "Delete all locks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of all locks"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public void deleteAllLocks() {
        lockWs.deleteAllLocks();
    }

}
