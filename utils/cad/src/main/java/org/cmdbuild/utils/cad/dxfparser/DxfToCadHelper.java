/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import com.google.common.base.Joiner;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getBoundingBox;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getCenter;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getSurfaceArea;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import org.cmdbuild.utils.cad.model.CadEntity;
import org.cmdbuild.utils.cad.model.CadEntityImpl;
import org.cmdbuild.utils.cad.model.CadPolyline;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.utils.cad.geo.CadPointTransformationHelper;
import org.cmdbuild.utils.cad.model.CadPoint;
import org.cmdbuild.utils.cad.model.CadRectangle;

public class DxfToCadHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DxfDocument document;
    private final CadPointTransformationHelper helper;

    private final List<CadEntity> entities = list();

    public DxfToCadHelper(DxfDocument document, String targetCoordinateSystem, boolean enableAngleDisplacementProcessing) {
        this.document = document;
        helper = CadPointTransformationHelper.fromDocument(document, targetCoordinateSystem, enableAngleDisplacementProcessing);
    }

    public List<CadEntity> extractCadEntities() {
        logger.debug("extracting cad entities");
        document.getEntities().forEach(this::handleDxfEntity);
        logger.debug("found {} cad entities", entities.size());
        return entities;
    }

    private void handleDxfEntity(DxfEntity dxf) {
        try {
            logger.debug("processing dxf entity = {}", dxf);
            if (dxf.hasXdata()) {
                Map<String, String> metadata = map(dxf.getXdata().getXdata()).mapValues(l -> Joiner.on(",").join(l));
                CadPolyline cadPerimeter = dxf.getPerimeter();
                logger.debug("found cad entity = {} with perimeter = {}", dxf, cadPerimeter);
                CadPoint cadCenter = getCenter(cadPerimeter);
                CadPolyline gisPerimeter = new CadPolyline(list(cadPerimeter.getVertexes()).map(helper::cadPointToGeoPoint));
                CadPoint gisCenter = helper.cadPointToGeoPoint(cadCenter);
                CadRectangle gisBoundingBox = getBoundingBox(gisPerimeter);
                double surfaceArea = getSurfaceArea(list(cadPerimeter.getVertexes()).map(helper::scalePoint));
                CadEntity cad = CadEntityImpl.builder()
                        .withLayer(dxf.getLayer())
                        .withMetadata(metadata)
                        .withBoundingBox(gisBoundingBox)
                        .withPosition(gisCenter)
                        .withSurface(surfaceArea)
                        .withPerimeter(gisPerimeter)//TODO closed perimeter when polyline not closed ??
                        .withPolyline(gisPerimeter)
                        .withClosed(dxf.isClosedPerimeter())
                        .build();
                logger.debug("processed cad entity = {}", cad);
                entities.add(cad);
            }
        } catch (Exception ex) {
            logger.error(marker(), "error processing dxf entity = {}", dxf, ex);
        }
    }

}
