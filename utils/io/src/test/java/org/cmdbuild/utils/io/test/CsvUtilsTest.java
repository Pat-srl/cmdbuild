/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.utils.io.test;

import static org.cmdbuild.utils.csv.CsvUtils.detectCsvSeparatorOrNull;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CsvUtilsTest {

    @Test
    public void testDetectCsvSeparatorOrNull() {
        assertEquals(null, detectCsvSeparatorOrNull(newDataSource("")));
        assertEquals(null, detectCsvSeparatorOrNull(newDataSource("wrgjner43f")));
        assertEquals(null, detectCsvSeparatorOrNull(newDataSource("wrgjner43f\nfdq3a5fey4\nxw2ww45y45ct\nxa2asd5ct")));
        assertEquals(";", detectCsvSeparatorOrNull(newDataSource("some;header;cols\n1;2;3\na;b;c\n\n\n")));
        assertEquals(",", detectCsvSeparatorOrNull(newDataSource("\n\nsome,header,cols\n1,2,3\na,b,c\n\n\n")));
        assertEquals("\t", detectCsvSeparatorOrNull(newDataSource("\n\nsome\theader\tcols\n1\t2\t3\na\tb\tc\n\n\n")));
    }

}
