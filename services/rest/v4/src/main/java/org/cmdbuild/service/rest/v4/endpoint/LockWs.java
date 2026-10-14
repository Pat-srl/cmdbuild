/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.service.rest.common.serializationhelpers.ItemLockSerializationHelper;
import org.cmdbuild.service.rest.v4.command.LockWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.serializationhelpers.ItemLockSerializationHelper.serializeLockData;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("administration/locks/")
@Tag(name = "Locks", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete a lock")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ACCESS_AUTHORITY)
@Component
public class LockWs {

    private final LockWsCommand command;

    public LockWs(LockWsCommand command) {
        this.command = checkNotNull(command);
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
        List<ItemLock> locks = command.doGetLocks();
        return response(locks.stream().map(ItemLockSerializationHelper::serializeLockData).collect(toList()));
    }

    @GET
    @Path("{lockId}")
    @Operation(
            summary = "Get lock",
            description = "Get lock",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lock data"),
                    @ApiResponse(responseCode = "404", description = "Lock not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getLock(
            @PathParam("lockId") @Parameter(description = "Id of lock to query") String lockId
    ) {
        ItemLock lock = command.doGetLock(lockId);
        return response(serializeLockData(lock));
    }

    @DELETE
    @Path("{lockId}")
    @Operation(
            summary = "Delete lock",
            description = "Delete lock",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of lock"),
                    @ApiResponse(responseCode = "404", description = "Lock not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteLock(
            @PathParam("lockId") @Parameter(description = "Id of lock to delete") String lockId
    ) {
        command.doDeleteLock(lockId);
        return success();
    }

    @DELETE
    @Path("_ANY")
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
        command.doDeleteAllLocks();
    }

}
