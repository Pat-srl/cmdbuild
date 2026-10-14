/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.gis.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;

/**
 * @author ldare
 */
@Component
public class GeoValueWsCommand {


    private final GisService gisService;

    public GeoValueWsCommand(GisService gisService) {
        this.gisService = checkNotNull(gisService);
    }

    public List<GisValue> doQuery(
            Set<Long> attrs,
            String area,
            String filterStr,
            String forOwner,
            boolean attachNavTree,
            java.util.concurrent.atomic.AtomicReference<List<GisNavTreeNode>> navTreeOut
    ) {
        CmdbFilter filter = parseFilter(filterStr);
        if (attachNavTree) {
            GisValuesAndNavTree geoValuesAndNavTree = gisService.getGisValuesAndNavTree(attrs, area, filter, forOwner);
            navTreeOut.set(geoValuesAndNavTree.getNavTree());
            return geoValuesAndNavTree.getGisValues();
        } else {
            return gisService.getGisValues(attrs, area, filter, forOwner);
        }
    }

    public Area doQueryArea(Set<Long> attrs, String filterStr, String forOwner) {
        CmdbFilter filter = parseFilter(filterStr);
        return gisService.getAreaForValues(attrs, filter, forOwner);
    }


}
