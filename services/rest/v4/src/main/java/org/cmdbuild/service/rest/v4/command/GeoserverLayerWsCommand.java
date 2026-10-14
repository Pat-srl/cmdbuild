/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import jakarta.activation.DataHandler;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.geoserver.GeoserverLayerImpl;
import org.cmdbuild.service.rest.v4.model.WsLayerData;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_WRITE;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class GeoserverLayerWsCommand {

    private final OperationUserSupplier operationUserSupplier;
    private final GisService gisService;
    private final DaoService daoService;
    private final UserClassService userClassService;

    public GeoserverLayerWsCommand(OperationUserSupplier operationUserSupplier, GisService gisService, DaoService daoService, UserClassService userClassService) {
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.gisService = checkNotNull(gisService);
        this.daoService = checkNotNull(daoService);
        this.userClassService = checkNotNull(userClassService);
    }

    public GeoserverLayer doGetOneForCard(String classId, Long cardId, String attrName) {
        gisService.checkAttributePermission(classId, attrName, GAP_READ);
        return gisService.getGeoserverLayer(classId, attrName, cardId);//TODO add access control
    }

    public List<GeoserverLayer> doGetMany(String classId, String cardId) {
        List<GeoserverLayer> geoserverLayerList;
        if (equal(classId, "_ANY") && equal(cardId, "_ANY")) {
            //TODO improve this
            List<GeoserverLayer> geoServers = gisService.getGeoServerLayers().stream().filter(l -> operationUserSupplier.hasPrivileges((p) -> p.hasReadAccess(daoService.getClasse(l.getOwnerClass())))).collect(toList());
            if (!geoServers.isEmpty()) {
                List<Long> cardIds = daoService.select(ATTR_ID).from(BASE_CLASS_NAME).where(ATTR_ID, WhereOperator.IN, list(geoServers).map(GeoserverLayer::getOwnerCard)).run().stream().map(r -> r.get(ATTR_ID, Long.class)).collect(toList());
                geoserverLayerList = geoServers.stream().filter(l -> cardIds.contains(l.getOwnerCard())).collect(toList());
            } else {
                geoserverLayerList = emptyList();
            }
        } else {
            //TODO add access control
            geoserverLayerList = gisService.getGeoServerLayersForCard(classId, Long.valueOf(cardId));
        }

        return geoserverLayerList;
    }

    public GeoserverLayer doUpdate(String classId, Long cardId, String attrName, DataHandler dataHandler, WsLayerData data) {
        gisService.checkAttributePermission(classId, attrName, GAP_WRITE);
        GeoserverLayer layer = gisService.getGeoserverLayerByCodeOrNull(classId, attrName, cardId);
        if (dataHandler != null) {
            layer = gisService.setGeoserverLayer(classId, attrName, cardId, dataHandler);
        }
        if (data != null) {
            layer = gisService.updateGeoserverLayer(GeoserverLayerImpl.copyOf(layer).withActive(data.getActive()).build());
        }
        return layer;
    }

    public void doDelete(String classId, Long cardId, String attrName) {
        gisService.checkAttributePermission(classId, attrName, GAP_WRITE);
        gisService.deleteGeoServerLayer(classId, attrName, cardId);
    }

    public List<GeoserverLayer> filterForManagement(List<GeoserverLayer> geoserverLayerList) {

        return list(geoserverLayerList).filter(GeoserverLayer::isActive)
                .filter(l -> gisService.getGisAttributeIncludeInherited(l.getOwnerClass(), l.getAttributeName()).isActive()
                        && userClassService.getUserClass(l.getOwnerClass()).hasGisAttributeReadPermission(l.getAttributeName()));
    }
}
