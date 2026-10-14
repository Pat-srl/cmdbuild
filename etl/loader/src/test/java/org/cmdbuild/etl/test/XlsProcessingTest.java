/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.cmdbuild.etl.utils.XlsProcessingUtils.getRecordsFromXlsFile;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.junit.Assert.assertEquals;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;

public class XlsProcessingTest {

    private File generateXlsWithManyRow() {
        File file = tempFile("oom_test_file", "xlsx");
        String fileName = file.getAbsolutePath();
        int maxRows = 1048576; // 2^20
        int dataRows = 10;
        int columns = 20;

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet("StressTest");

            SXSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < columns; i++) {
                SXSSFCell cell = headerRow.createCell(i);
                cell.setCellValue("Header " + (i + 1));
            }

            for (int i = 1; i <= dataRows; i++) {
                SXSSFRow row = sheet.createRow(i);
                for (int j = 0; j < columns; j++) {
                    SXSSFCell cell = row.createCell(j);
                    cell.setCellValue(UUID.randomUUID().toString().substring(0, 8));
                }
            }

            for (int i = dataRows + 1; i < maxRows; i++) {
                SXSSFRow row = sheet.createRow(i);

                SXSSFCell cell = row.createCell(0);
                cell.setBlank();
            }

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
            }

            workbook.dispose();

            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testXlsWithManyRows() {
        List<List<Object>> records = getRecordsFromXlsFile(newDataSource(generateXlsWithManyRow()));

        assertEquals(11, records.size());
    }

    @Test
    public void testXlsWithFormulas() {
        List<List<Object>> records = getRecordsFromXlsFile(newDataSource(getClass().getResourceAsStream("/org/cmdbuild/etl/test/file_with_formulas.xlsx"), null, "file_with_formulas.xlsx"));

        assertEquals(3, records.size());

        assertEquals(15l, records.get(0).get(0));
        assertEquals(35l, records.get(0).get(2));
        assertEquals(32l, records.get(1).get(2));
        assertEquals(3l, records.get(2).get(2));
    }

}
