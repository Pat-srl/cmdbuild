/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.test;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.cmdbuild.utils.cad.CadUtils.parseDxfFile;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.geo.DxfToShapefileHelper;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShapeUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testDxfToShape() {
        DxfDocument dxfDocument = parseDxfFile(getClass().getResourceAsStream("/test_file_2.dxf"));
        BigByteArray shapeFile = DxfToShapefileHelper.toShapeFile(dxfDocument);

        logger.debug("built shape file = {} {}", byteCountToDisplaySize(shapeFile.length()), getContentType(shapeFile.toInputStream()));
        assertTrue(shapeFile.length() > 0);
    }

}
