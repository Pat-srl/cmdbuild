/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.test;

import java.io.File;
import static java.lang.Math.PI;
import java.util.List;
import static org.cmdbuild.utils.cad.CadGeometryUtils.arcFromBulge;
import static org.cmdbuild.utils.cad.CadGeometryUtils.arcToPolyline;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getCenter;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getDirection;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getSurfaceArea;
import static org.cmdbuild.utils.cad.CadGeometryUtils.plotToPng;
import org.cmdbuild.utils.cad.dxfparser.DxfArcImpl;
import org.cmdbuild.utils.cad.dxfparser.DxfVertexImpl;
import org.cmdbuild.utils.cad.dxfparser.model.DxfExtendedData;
import org.cmdbuild.utils.cad.dxfparser.model.DxfVertex;
import static org.cmdbuild.utils.cad.geo.GeoUtils.translateCoordinates;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import org.cmdbuild.utils.cad.model.CadPolyline;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class CadGeometryTest {

    private static final double EXPECTED_COORDS_PRECISION = 0.00000001d;

    @Test
    public void testDirection() {
//        list(new DxfVertexImpl(20, 10), new DxfVertexImpl(20, 11), new DxfVertexImpl(20, 15), new DxfVertexImpl(20, 20), new DxfVertexImpl(20, 30), new DxfVertexImpl(15, 30), new DxfVertexImpl(11, 20), new DxfVertexImpl(10, 20),
//                new DxfVertexImpl(9, 20), new DxfVertexImpl(5, 20), new DxfVertexImpl(5, 11), new DxfVertexImpl(5, 10),
//                new DxfVertexImpl(5, 9), new DxfVertexImpl(5, 5), new DxfVertexImpl(9, 5), new DxfVertexImpl(10, 0),
//                new DxfVertexImpl(11, 5), new DxfVertexImpl(15, 5), new DxfVertexImpl(15, 9)
//        ).forEach(to -> {
//            LoggerFactory.getLogger(getClass()).info("{} -> {} dir = {}", new DxfVertexImpl(10, 10), to, getDirection(new DxfVertexImpl(10, 10), to));
//        });

        assertEquals(0, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(20, 10)), 0.01);
        assertEquals(0.0996, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(20, 11)), 0.01);
        assertEquals(PI / 4, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(20, 20)), 0.01);
        assertEquals(1.4711, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(11, 20)), 0.01);
        assertEquals(PI / 2, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(10, 20)), 0.01);

        assertEquals(1.6704, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(9, 20)), 0.01);
        assertEquals(PI * 3 / 4, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(0, 20)), 0.01);
        assertEquals(2.9441, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(5, 11)), 0.01);
        assertEquals(PI, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(5, 10)), 0.01);

        assertEquals(3.3389, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(5, 9)), 0.01);
        assertEquals(PI * 5 / 4, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(5, 5)), 0.01);
        assertEquals(4.51499, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(9, 5)), 0.01);
        assertEquals(PI * 3 / 2, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(10, 5)), 0.01);

        assertEquals(4.9097, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(11, 5)), 0.01);
        assertEquals(PI * 7 / 4, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(20, 0)), 0.01);
        assertEquals(6.08578, getDirection(new DxfVertexImpl(10, 10), new DxfVertexImpl(15, 9)), 0.01);
    }

    @Test
    public void testArcFromBulge() {
        List<DxfVertex> list = arcFromBulge(new DxfVertexImpl(-6605817.270420535, 12388.12561731972), new DxfVertexImpl(-6658166.896915255, -49307.93662000261), -0.4362876874774092);
        assertPointEquals(-6605817.270420535, 12388.12561731972, list.get(0));
        assertPointEquals(-6605552.480076779, 9562.740745128303, list.get(1));
        assertPointEquals(-6618533.4675079025, -29879.654243185345, list.get(16));
        assertPointEquals(-6652519.26151615, -48765.18611954334, list.get(30));
        assertPointEquals(-6655336.100069727, -49109.18345272453, list.get(31));

        list = arcFromBulge(new DxfVertexImpl(-6665947.865480496, -59746.4835564699), new DxfVertexImpl(-6700868.946694596, -76457.6095632147), 0.1575340226879982);
        assertPointEquals(-6665947.865480496, -59746.48355646992, list.get(0));
        assertPointEquals(-6667164.906975105, -59922.459868295846, list.get(1));
        assertPointEquals(-6684724.69153929, -65351.41735970658, list.get(16));
        assertPointEquals(-6699051.687862483, -74800.61705828534, list.get(30));
        assertPointEquals(-6699968.408315187, -75620.23969921295, list.get(31));
    }

    @Test
    public void testArcToPolilyne1() {
        CadPolyline polyline = arcToPolyline(new DxfArcImpl(point(10, 10), 90d, 270d, 5d, "_", mock(DxfExtendedData.class)), 8);
        assertEquals(9, polyline.getVertexes().size());
        assertPointEquals(15, 10, polyline.getVertexes().get(0));
        assertPointEquals(10, 5, polyline.getVertexes().get(4));
        assertPointEquals(5, 10, polyline.getVertexes().get(8));
    }

    @Test
    public void testArcToPolilyne2() {
        CadPolyline polyline = arcToPolyline(new DxfArcImpl(point(10, 10), 90d, 270d, 5d, "_", mock(DxfExtendedData.class)));
        assertEquals(33, polyline.getVertexes().size());
        assertPointEquals(15, 10, polyline.getVertexes().get(0));
        assertPointEquals(10, 5, polyline.getVertexes().get(16));
        assertPointEquals(5, 10, polyline.getVertexes().get(32));
    }

    @Test
    public void testArcToPolilyne3() {
        CadPolyline polyline = arcToPolyline(new DxfArcImpl(point(100, 200), 135d, 225d, 50d, "_", mock(DxfExtendedData.class)), 8);
        assertEquals(9, polyline.getVertexes().size());
        assertPointEquals(135.35533905932738, 164.64466094067262, polyline.getVertexes().get(0));
        assertPointEquals(100, 150, polyline.getVertexes().get(4));
        assertPointEquals(64.64466094067262, 164.64466094067262, polyline.getVertexes().get(8));
    }

    @Test
    public void testSurfaceArea() {
        assertEquals(4d, getSurfaceArea(list(point(0, 0), point(2, 0), point(2, 2), point(0, 2))), 0d);
        assertEquals(4d, getSurfaceArea(list(point(0, 0), point(2, 0), point(2, -2), point(0, -2))), 0d);
        assertEquals(2d, getSurfaceArea(list(point(1, 1), point(3, 1), point(2, 3))), 0d);
        assertEquals(2d, getSurfaceArea(list(point(-1, -1), point(-3, -1), point(-2, -3))), 0d);
    }

    @Test
    public void testSurfaceAreaWithHoles() {
        assertEquals(600d, getSurfaceArea(list(point(10, 10), point(40, 10), point(40, 30), point(10, 30))), 0d);
        assertEquals(500d, getSurfaceArea(list(point(10, 10), point(20, 10), point(20, 20), point(30, 20), point(30, 10), point(40, 10), point(40, 30), point(10, 30))), 0d);
        assertEquals(1200d, getSurfaceArea(list(point(10, 10), point(50, 10), point(50, 40), point(10, 40))), 0d);
        assertEquals(1000d, getSurfaceArea(list(point(10, 10), point(30, 10), point(30, 20), point(20, 20), point(20, 30), point(40, 30), point(40, 20), point(30, 20), point(30, 10), point(50, 10), point(50, 40), point(10, 40))), 0d);
//        LoggerFactory.getLogger(getClass()).info("area = {}", getSurfaceArea(list(point(2365784.09628484, 5112727.80641387), point(2365807.91520625, 5112718.30474051), point(2365805.11626371, 5112711.28831404), point(2365801.75586509, 5112712.52180753), point(2365795.97283537, 5112699.09787436), point(2365782.75221003, 5112704.58577671), point(2365785.4046966, 5112711.25112245), point(2365781.85415635, 5112715.57839769), point(2365784.50190076, 5112720.86936683), point(2365790.8020535, 5112721.51006605), point(2365793.35094163, 5112717.94541022), point(2365792.16191168, 5112713.93370185), point(2365789.5710999, 5112711.40118898), point(2365785.4046966, 5112711.25112245), point(2365782.75221003, 5112704.58577671), point(2365769.08864272, 5112710.03634902), point(2365775.09314343, 5112723.4789472), point(2365781.44465507, 5112721.159273), point(2365784.09628484, 5112727.80641387))));
//        writeToFile(new File("/tmp/file.png"), plotToPng(1000, 1000, false, new CadPolyline(point(2365784.09628484, 5112727.80641387), point(2365807.91520625, 5112718.30474051), point(2365805.11626371, 5112711.28831404), point(2365801.75586509, 5112712.52180753), point(2365795.97283537, 5112699.09787436), point(2365782.75221003, 5112704.58577671), point(2365785.4046966, 5112711.25112245), point(2365781.85415635, 5112715.57839769), point(2365784.50190076, 5112720.86936683), point(2365790.8020535, 5112721.51006605), point(2365793.35094163, 5112717.94541022), point(2365792.16191168, 5112713.93370185), point(2365789.5710999, 5112711.40118898), point(2365785.4046966, 5112711.25112245), point(2365782.75221003, 5112704.58577671), point(2365769.08864272, 5112710.03634902), point(2365775.09314343, 5112723.4789472), point(2365781.44465507, 5112721.159273), point(2365784.09628484, 5112727.80641387))));
    }

    @Test
    public void testSurfaceCenter() {
        assertEquals(point(1, 1), getCenter(point(0, 0), point(2, 0), point(2, 2), point(0, 2)));
        assertEquals(point(1, -1), getCenter(point(0, 0), point(2, 0), point(2, -2), point(0, -2)));
        assertEquals(point(1, 1), getCenter(point(1, 0), point(2, 1), point(1, 2), point(0, 1)));
        assertEquals(point(5, 2), getCenter(point(0, 0), point(10, 0), point(10, 4), point(0, 4)));
        assertEquals(point(5, 2), getCenter(point(0, 0), point(5, 0), point(10, 0), point(10, 4), point(0, 4)));
        assertEquals(point(5, 2), getCenter(point(0, 0), point(5, -1), point(10, 0), point(10, 4), point(5, 5), point(0, 4)));
        assertEquals(point(1, 1), getCenter(point(0, 0), point(2, 2)));
        assertEquals(point(0, 1), getCenter(point(0, 0), point(0, 2)));
    }

    @Test
    public void testSurfaceCenter2() {
        assertEquals(point(497.0, 769.5), getCenter(list(point(494, 770), point(499, 772), point(500, 769), point(495, 767), point(494, 770))));
        assertEquals(point(1826497.0, 5114769.5), getCenter(list(point(1826494, 5114770), point(1826499, 5114772), point(1826500, 5114769), point(1826495, 5114767), point(1826494, 5114770))));
        assertEquals(point(1826497.6443894939, 5114770.004723317), getCenter(list(point(1826494.411900759, 5114770.752614592), point(1826499.869885937, 5114772.465519025), point(1826500.876914242, 5114769.256731966), point(1826495.418718099, 5114767.544013962), point(1826494.411900759, 5114770.752614592))));
    }

    @Test
    public void testLatLgtTranslation() {
        CadPoint geoPoint = translateCoordinates(point(1826509.784953265, 5114776.001876457), "EPSG:3003", "EPSG:4326");
        assertPointEquals(13.22466120611643, 46.10824249099204, geoPoint);
        geoPoint = translateCoordinates(point(2382803.698433392, 5107621.183275111), "EPSG:3004", "EPSG:4326");
        assertPointEquals(13.22461708981794, 46.108208077422404, geoPoint);

//        geoPoint = translateCoordinates(point(1826505.319974108, 5114760.736766426), "EPSG:3003", "EPSG:4326");
    }

    @Test
    public void testContains1() {
        assertTrue(new CadPolyline(point(0, 0), point(2, 0), point(2, 2), point(0, 2)).contains(point(1, 1)));
        assertFalse(new CadPolyline(point(0, 0), point(2, 0), point(2, 2), point(0, 2)).contains(point(1, 3)));
        assertFalse(new CadPolyline(point(0, 0), point(2, 0), point(2, 2), point(0, 2)).contains(point(3, 3)));
        assertFalse(new CadPolyline(point(0, 0), point(2, 0), point(2, 2), point(0, 2)).contains(point(3, 1)));
        assertFalse(new CadPolyline(point(0, 0), point(2, 0), point(2, 2), point(0, 2)).contains(point(-1, -1)));
        assertFalse(new CadPolyline(point(0, 0), point(2, 0), point(2, 2), point(0, 2)).contains(point(-1, 1)));
        assertFalse(new CadPolyline(point(0, 0), point(2, 0), point(2, 2), point(0, 2)).contains(point(1, -1)));
    }

    @Test
    @Ignore //TODO fix this
    public void testContains2() {
        assertTrue(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(1, 1)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(0, 0)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(0, 2)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(2, 0)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(2, 2)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(1, 3)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(3, 1)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(-1, 1)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(1, -1)));
        assertFalse(new CadPolyline(point(1, 0), point(2, 1), point(1, 2), point(0, 1)).contains(point(-1, -1)));
    }

    @Test
    @Ignore
    public void testPlot() {
        writeToFile(new File("/tmp/file.png"), plotToPng(1000, 1000, false, list(CadPolyline.fromText("POLYGON((1024755.84657976 5703966.70526817,1024786.74895611 5715781.52179563,1024798.03478547 5720316.30526359,1024834.3188223 5734136.83303943,1024832.45852947 5733202.11439838,1024863.65895932 5745055.36261998,1024751.46782874 5698403.09121032,1024645.24465813 5657525.65816821,1024755.84657976 5703966.70526817))"),
                CadPolyline.fromText("POLYGON((1024775.15043998 5702751.54814332,1024664.70230516 5660303.59041653,1024666.17522827 5660924.53494697,1024496.51942529 5594952.82473881,1024303.15236429 5511339.43881738,1024946.76348789 5760949.51423636,1025047.06307841 5802013.22336115,1024733.37918644 5683095.29949629,1024720.47925626 5679054.05867147,1024798.68245222 5711744.98387036,1024775.15043998 5702751.54814332))"),
                CadPolyline.fromText("POLYGON((1024808.15162055 5711429.03844872,1024896.65115383 5745082.86503912,1024835.45049941 5719734.0027997,1024747.6622233 5686174.47679237,1024808.15162055 5711429.03844872))"),
                CadPolyline.fromText("POLYGON((1024899.82996781 5746287.1183341,1025033.51185677 5796648.95059443,1024944.83218186 5760317.37500989,1024812.68444358 5710158.3207767,1024899.82996781 5746287.1183341))"))
        ));
//                .map(l -> new CadPolyline(list(l.getVertexes()).map(p -> translateCoordinates(p, "EPSG:4326", "EPSG:3004"))))));
//        writeToFile(new File("/tmp/file.png"), plotToPng(1000, 1000, CadPolyline.fromText("POLYGON((10 10, 10 40, 20 40, 10 10))")));
    }

    @Test
    @Ignore
    public void testContains3() {
        CadPolyline floorUnit = CadPolyline.fromText("POLYGON((1024775.15043998 5702751.54814332,1024664.70230516 5660303.59041653,1024666.17522827 5660924.53494697,1024496.51942529 5594952.82473881,1024303.15236429 5511339.43881738,1024946.76348789 5760949.51423636,1025047.06307841 5802013.22336115,1024733.37918644 5683095.29949629,1024720.47925626 5679054.05867147,1024798.68245222 5711744.98387036,1024775.15043998 5702751.54814332))");
        assertTrue(floorUnit.contains(CadPolyline.fromText("POLYGON((1024808.15162055 5711429.03844872,1024896.65115383 5745082.86503912,1024835.45049941 5719734.0027997,1024747.6622233 5686174.47679237,1024808.15162055 5711429.03844872))")));
        assertTrue(floorUnit.contains(CadPolyline.fromText("POLYGON((1024899.82996781 5746287.1183341,1025033.51185677 5796648.95059443,1024944.83218186 5760317.37500989,1024812.68444358 5710158.3207767,1024899.82996781 5746287.1183341))")));
        assertTrue(floorUnit.contains(CadPolyline.fromText("POLYGON((1024755.84657976 5703966.70526817,1024786.74895611 5715781.52179563,1024798.03478547 5720316.30526359,1024834.3188223 5734136.83303943,1024832.45852947 5733202.11439838,1024863.65895932 5745055.36261998,1024751.46782874 5698403.09121032,1024645.24465813 5657525.65816821,1024755.84657976 5703966.70526817))")));
    }
//    @Test
//    public void testGeoreferencePoint1() {
//        CadPoint cadPoint = point(1826494.5439573862, 5114779.883680188);//R01
//        DxfGeoreferenceInfo georeference = new DxfGeoreferenceInfoImpl(1826509.784953265, 5114776.001876457, 2382803.698433392, 5107621.183275111, PI / 2, 1, "EPSG:3004");
//        GeoReferenceHelper helper = new GeoReferenceHelper(georeference);
//        CadPoint gisPoint = helper.cadPointToGeoPoint(cadPoint);
//        logger.info("translate point {} -> {}", cadPoint, gisPoint);
//        assertPointEquals(13.224419953868079, 46.108243000184075, gisPoint);
//    }
//
//    @Test
//    public void testGeoreferencePoint2() {
//        CadPoint cadPoint = point(1826497.6443894939, 5114770.004723317);//  [R05] 
//        DxfGeoreferenceInfo georeference = new DxfGeoreferenceInfoImpl(1826509.784953265, 5114776.001876457, 2382803.698433392, 5107621.183275111, PI / 2, 1, "EPSG:3004");
//        GeoReferenceHelper helper = new GeoReferenceHelper(georeference);
//        CadPoint gisPoint = helper.cadPointToGeoPoint(cadPoint);
//        logger.info("translate point {} -> {}", cadPoint, gisPoint);
//        assertPointEquals(13.224460056921714, 46.10815412348654, gisPoint);
//    }
//    @Test
//    public void testLatLgtTranslation() {
////        DxfValue{groupCode=10, value=1826509.784953265}
////	DxfValue{groupCode=20, value=5114776.001876457}
////	DxfValue{groupCode=30, value=0.0}
////	DxfValue{groupCode=11, value=2382803.698433392}
////	DxfValue{groupCode=21, value=5107621.183275111}
////DxfValue{groupCode=302, value=<georss:point>46.1083 13.2247</georss:point>}		
//
////$LATITUDE                 (org.cmdbuild.utils.cad.dxfparser.model.DxfVariableImpl) = DxfVariable{key=$LATITUDE, values=[DxfValue{groupCode=40, value=46.10826}]} 
////		$LONGITUDE                (org.cmdbuild.utils.cad.dxfparser.model.DxfVariableImpl) = DxfVariable{key=$LONGITUDE, values=[DxfValue{groupCode=40, value=13.224656}]} 
////        CadPoint cadPoint = point(1826509.784953265, 5114776.001876457);
////[R01] position = (1826497.8336046669, 5114789.096052096)
////        CadPoint cadPoint = point(1826497.8336046669, 5114789.096052096);
//        CadPoint cadPoint = point(2382803.698433392, 5107621.183275111);
////        CadPoint cadPoint = point(5107621.183275111, 2382803.698433392);
//
//        CadPoint geoPoint = translateCoordinates(cadPoint, "EPSG:3004", "EPSG:4326");
////        CadPoint geoPoint = translateCoordinates(cadPoint, "EPSG:MonteMario_1.Italy-2", "EPSG:4326");
//        assertPointEquals(13.22461708981794, 46.108208077422404, geoPoint);
//    }

    private static void assertPointEquals(double expectedX, double expectedY, DxfVertex point) {
        assertEquals(expectedX, point.getX(), EXPECTED_COORDS_PRECISION);
        assertEquals(expectedY, point.getY(), EXPECTED_COORDS_PRECISION);
    }

    private static void assertPointEquals(double expectedX, double expectedY, CadPoint point) {
        assertEquals(expectedX, point.getX(), EXPECTED_COORDS_PRECISION);
        assertEquals(expectedY, point.getY(), EXPECTED_COORDS_PRECISION);
    }

//    private String lat
}
