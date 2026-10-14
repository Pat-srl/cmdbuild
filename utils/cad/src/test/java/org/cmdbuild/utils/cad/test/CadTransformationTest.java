/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.test;

import org.cmdbuild.utils.cad.geo.CadPointTransformationHelper;
import org.cmdbuild.utils.cad.geo.PointTransformationRuleImpl;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CadTransformationTest {

    @Test
    public void testCadPointTransformation1() {
        new CadPointTransformationHelper(PointTransformationRuleImpl.rcs("EPSG:3003", "EPSG:4326")).cadPointToGeoPoint(point(1795941.70, 4709367.44));
        new CadPointTransformationHelper(PointTransformationRuleImpl.rcs("EPSG:3004", "EPSG:4326")).cadPointToGeoPoint(point(1795941.70, 4709367.44));
//        new CadPointTransformationHelper(PointTransformationRuleImpl.rcs("MonteMario_1.Italy-2", "EPSG:4326")).cadPointToGeoPoint(point(1795941.70, 4709367.44)); 
    }

    @Test
    public void testCadPointTransformation2() throws FactoryException {
        CRS.decode("EPSG:3003");
        CRS.decode("EPSG:3004");
        CRS.decode("EPSG:4326");
//        CRS.decode("MonteMario_1.Italy-2");
    }

    @Test
    public void testCadPointTransformation3() {
        assertEquals(point(15, 12), new CadPointTransformationHelper(PointTransformationRuleImpl.scaling(0.1, 0.1, 0, 0)).cadPointToGeoPoint(point(150, 120)));
        assertEquals(point(105, 102), new CadPointTransformationHelper(PointTransformationRuleImpl.scaling(0.1, 0.1, 100, 100)).cadPointToGeoPoint(point(150, 120)));
        assertEquals(point(105, 12), new CadPointTransformationHelper(PointTransformationRuleImpl.scaling(0.1, 0.1, 100, 0)).cadPointToGeoPoint(point(150, 120)));
    }

}
