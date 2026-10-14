package org.cmdbuild.service.rest.common.beans;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ONLY_GRID_ATTRS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF_GOTOPAGE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;

/**
 * OpenAPI documentation of the query parameters bound by the
 * {@link WsQueryOptions} constructor.
 * <p>
 * The getters are never called: swagger-jaxrs2 expands a {@code @BeanParam}
 * argument from annotated public accessors only (constructor parameters are
 * ignored), so they exist just to publish these parameters in the generated
 * spec. Keep them in sync with the {@link WsQueryOptions} constructor.
 */
abstract class WsQueryOptionsParams {

    @QueryParam("attrs")
    @Parameter(description = "Comma-separated list of attributes to return")
    public List<String> getAttrsParam() {
        return null;
    }

    @QueryParam(ONLY_GRID_ATTRS)
    @DefaultValue(FALSE)
    @Parameter(description = "If true, return only the attributes shown in the grid")
    public Boolean getOnlyGridAttrsParam() {
        return null;
    }

    @QueryParam(FILTER)
    @Parameter(description = "CMDBuild filter, as a JSON string")
    public String getFilterParam() {
        return null;
    }

    @QueryParam("query")
    @Parameter(description = "Fulltext query")
    public String getQueryParam() {
        return null;
    }

    @QueryParam(SORT)
    @Parameter(description = "Sorter, as a JSON string")
    public String getSortParam() {
        return null;
    }

    @QueryParam(LIMIT)
    @Parameter(description = "Maximum number of items to return")
    public Long getLimitParam() {
        return null;
    }

    @QueryParam(START)
    @Parameter(description = "Number of items to skip (offset)")
    public Long getStartParam() {
        return null;
    }

    @QueryParam(DETAILED)
    @DefaultValue(FALSE)
    @Parameter(description = "If true, include full details in the response")
    public Boolean getDetailedParam() {
        return null;
    }

    @QueryParam(POSITION_OF)
    @Parameter(description = "Id of an item whose position in the result set is requested")
    public Long getPositionOfParam() {
        return null;
    }

    @QueryParam(POSITION_OF_GOTOPAGE)
    @DefaultValue(TRUE)
    @Parameter(description = "If true, move to the page containing the item given by positionOf")
    public Boolean getPositionOfGoToPageParam() {
        return null;
    }
}
