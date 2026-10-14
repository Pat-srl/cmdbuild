/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.geoserver;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.gis.GeoserverLayer;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.gis.GeoserverLayer.GEOSERVER_LAYER_ATTR_ATTRIBUTE_NAME;
import static org.cmdbuild.gis.GeoserverLayer.GEOSERVER_LAYER_ATTR_OWNER_CARD;
import static org.cmdbuild.gis.GeoserverLayer.GEOSERVER_LAYER_ATTR_OWNER_CLASS;

@Component
public class GisGeoserverLayerRepositoryImpl implements GisGeoserverLayerRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;

    public GisGeoserverLayerRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public GeoserverLayer create(GeoserverLayer layer) {
        logger.info("create layer = {}", layer);
        return dao.create(layer);
    }

    @Override
    public GeoserverLayer get(String name) {
        return dao.selectAll().from(GeoserverLayerImpl.class).where(ATTR_CODE, EQ, checkNotBlank(name)).getOne();
    }

    @Override
    public GeoserverLayer getByCodeOrNull(String classId, String attrName, long cardId) {
        return dao.selectAll().from(GeoserverLayerImpl.class)
                .where(GEOSERVER_LAYER_ATTR_OWNER_CLASS, EQ, checkNotBlank(classId))
                .where(GEOSERVER_LAYER_ATTR_ATTRIBUTE_NAME, EQ, checkNotBlank(attrName))
                .where(GEOSERVER_LAYER_ATTR_OWNER_CARD, EQ, cardId)
                .getOneOrNull();
    }

    @Override
    public GeoserverLayer getByIdOrNull(String classId, Long layerId, long cardId) {
        return dao.selectAll().from(GeoserverLayerImpl.class)
                .where(GEOSERVER_LAYER_ATTR_OWNER_CLASS, EQ, checkNotBlank(classId))
                .where("Id", EQ, checkNotBlank(layerId))
                .where(GEOSERVER_LAYER_ATTR_OWNER_CARD, EQ, cardId)
                .getOneOrNull();
    }

    @Override
    public void delete(long id) {
        dao.delete(GeoserverLayer.class, id);
    }

    @Override
    public GeoserverLayer get(long id) {
        return dao.getById(GeoserverLayer.class, id);
    }

    @Override
    public void delete(String name) {
        logger.info("delete layer = {}", name);
        dao.delete(get(name));
    }

    @Override
    public List<GeoserverLayer> getAll() {
        return dao.selectAll().from(GeoserverLayerImpl.class).asList();
    }

    @Override
    public List<GeoserverLayer> getForCard(Classe ownerClass, long cardId) {
        return dao.selectAll()
                .from(GeoserverLayerImpl.class)
                .where(GEOSERVER_LAYER_ATTR_OWNER_CLASS, EQ, ownerClass.getName())
                .where(GEOSERVER_LAYER_ATTR_OWNER_CARD, EQ, cardId)
                .asList();
    }

    @Override
    public GeoserverLayer update(GeoserverLayer geoserverLayer) {
        return dao.update(geoserverLayer);
    }

}
