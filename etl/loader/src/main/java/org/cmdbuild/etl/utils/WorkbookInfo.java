/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface WorkbookInfo {

    Workbook getWorkbook();

    Sheet getSheet();

    String getFileExt();

    String getContentType();

}
