/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.geoserver;

import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.gis.GeoserverLayer;

public interface GisGeoserverLayerRepository {

    GeoserverLayer create(GeoserverLayer layer);

    GeoserverLayer get(String name);

    void delete(String name);

    List<GeoserverLayer> getAll();

    List<GeoserverLayer> getForCard(Classe classe, long cardId);

    void delete(long id);

    GeoserverLayer get(long id);

    GeoserverLayer update(GeoserverLayer geoserverLayer);

    @Nullable
    GeoserverLayer getByCodeOrNull(String classId, String attrName, long cardId);

    @Nullable
    GeoserverLayer getByIdOrNull(String classId, Long attrName, long cardId);

}
