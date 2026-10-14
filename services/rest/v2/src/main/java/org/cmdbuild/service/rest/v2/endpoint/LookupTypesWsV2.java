package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("lookup_types/")
@Produces(APPLICATION_JSON)
public class LookupTypesWsV2 {

    private final LookupService lookupService;

    public LookupTypesWsV2(LookupService lookupLogic) {
        this.lookupService = checkNotNull(lookupLogic);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filter) {
        PagedElements<LookupType> lookupTypes = lookupService.getAllTypes(offset, limit, filter);
        return map("data", lookupTypes.stream().map(this::serializeResponse).collect(toList()), "meta", map("total", lookupTypes.size()));
    }

    @GET
    @Path("{lookupTypeId}/")
    public Object readOne(@PathParam("lookupTypeId") String lookupTypeId) {
        return map("data", serializeResponse(lookupService.getLookupType(decodeIfHex(lookupTypeId))), "meta", map());
    }

    private Object serializeResponse(LookupType lookupType) {
        return map(
                "name", lookupType.getName(),
                "parent", Optional.ofNullable(lookupType.getParent()).map(l->lookupService.getLookupType(l).getName()).orElse(null),
                "_id", lookupType.getName());
    }

    public static String decodeIfHex(String value) {
        if (nullToEmpty(value).matches("(0x|HEX)[0-9a-fA-F]*")) {
            try {
                return new String(Hex.decodeHex(value.replaceFirst("^(0x|HEX)", "")), "UTF8");
            } catch (DecoderException | UnsupportedEncodingException ex) {
                throw runtime(ex, "error decoding hex value = %s", value);
            }
        } else {
            return value;
        }
    }

}
