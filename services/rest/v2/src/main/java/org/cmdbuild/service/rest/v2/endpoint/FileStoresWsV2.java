package org.cmdbuild.service.rest.v2.endpoint;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import jakarta.activation.DataHandler;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;

@Path("filestores/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class FileStoresWsV2 {

    String DATASTORE_ID = "datastoreId";
    String FOLDER_ID = "folderId";
    String FILE_ID = "fileId";

    @GET
    @Path("{datastoreId}/folders/")
    public Object readFolders(@PathParam("datastoreId") String datastoreId) {
        return null;
    }

    @GET
    @Path("{datastoreId}/folders/{folderId}/")
    public Object readFolder(@PathParam("datastoreId") String datastoreId, @PathParam("folderId") String folderId) {
        return null;
    }

    @POST
    @Path("{datastoreId}/folders/{folderId}/files/")
    @Consumes(MULTIPART_FORM_DATA)
    public Object uploadFile(
            @PathParam("datastoreId") String datastoreId,
            @PathParam("folderId") String folderId,
            @Multipart(FILE) DataHandler dataHandler
    ) {
        return null;
    }

    @GET
    @Path("{datastoreId}/folders/{folderId}/files/")
    public Object readFiles(@PathParam("datastoreId") String datastoreId, @PathParam("folderId") String folderId) {
        return null;
    }

    @GET
    @Path("{datastoreId}/folders/{folderId}/files/{fileId}/")
    public Object readFile(@PathParam("datastoreId") String datastoreId,
            @PathParam("folderId") String folderId,
            @PathParam("fileId") String fileId
    ) {
        return null;
    }

    @GET
    @Path("{datastoreId}/folders/{folderId}/files/{fileId}/download")
    @Produces(APPLICATION_OCTET_STREAM)
    public Object downloadFile( //
            @PathParam("datastorId") String datastoreId,
            @PathParam("folderId") String folderId,
            @PathParam("fileId") String fileId
    ) {
        return null;
    }

    @DELETE
    @Path("{datastoreId}/folders/{folderId}/files/{fileId}/")
    public Object deleteFile(
            @PathParam("datastoreId") String datastoreId,
            @PathParam("folderId") String folderId,
            @PathParam("fileId") String fileId
    ) {
        return null;
    }

}
