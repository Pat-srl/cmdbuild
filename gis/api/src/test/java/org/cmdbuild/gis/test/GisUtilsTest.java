/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.test;

import org.cmdbuild.gis.Linestring;
import org.cmdbuild.gis.Point;
import org.cmdbuild.gis.model.PointImpl;
import static org.cmdbuild.gis.utils.GisUtils.cmGeometryToPostgisSql;
import static org.cmdbuild.gis.utils.GisUtils.parseGeometry;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GisUtilsTest {

    @Test
    public void testGeometrySerialization() {
        assertEquals("POINT(13.2245340172543 46.1082230000902)", cmGeometryToPostgisSql(new PointImpl(13.224534017254335, 46.10822300009022)));
        assertEquals("POINT(13.2244618053538 46.1081516889815)", cmGeometryToPostgisSql(new PointImpl(13.224461805353847, 46.108151688981515)));
        assertEquals("POINT(13.2244474822752 46.1081811144386)", cmGeometryToPostgisSql(new PointImpl(13.224447482275199, 46.108181114438615)));
        assertEquals("POINT(13.2244331589926 46.1082105402186)", cmGeometryToPostgisSql(new PointImpl(13.224433158992625, 46.108210540218614)));
        assertEquals("POINT(13.2244188518932 46.108239933737)", cmGeometryToPostgisSql(new PointImpl(13.224418851893196, 46.10823993373704)));
    }

    @Test
    public void testCmGeometrySerialization() {
        Point point = (Point) parseGeometry("POINT(13.2245340172543 46.1082230000902)");
        assertEquals(46.10, point.getLat(), 0.01);
        assertEquals(13.22, point.getLng(), 0.01);
        Linestring linestring = (Linestring) parseGeometry("LINESTRING(982062.939407944 5723604.677994,1024867.67524764 5761517.44402344,1027201.46599328 5734788.89206188,1057776.27730735 5755579.76375545,1049215.33013941 5711552.03546319,1090797.07352655 5747018.81658751,1073675.17919067 5702991.08829525,1100581.01314705 5701768.09584269,1140939.76408162 5729896.92225163,1143385.74898675 5685869.19395937,1117702.90748293 5712775.02791575,1175183.55275338 5695653.13357987,1153169.68860725 5668747.29962349,1206981.35652002 5701768.09584269,1191082.4546367 5660186.35245555,1228995.22066615 5688315.1788645,1210650.33387771 5652848.39774018,1257124.04707509 5635726.5034043,1260793.02443278 5669970.29207605,1243671.1300969 5682200.21660168,1227772.22821358 5727450.93734651)");
        assertEquals(21, linestring.getPoints().size());
        assertEquals(5723604.67, linestring.getPoints().get(0).getLat(), 0.01);
        assertEquals(982062.93, linestring.getPoints().get(0).getLng(), 0.01);
        assertEquals(5727450.93, linestring.getPoints().get(20).getLat(), 0.01);
        assertEquals(1227772.22, linestring.getPoints().get(20).getLng(), 0.01);
    }

}
