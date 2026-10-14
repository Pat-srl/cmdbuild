/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cmdbuild.etl.EtlException;
import static org.cmdbuild.etl.loader.EtlFileFormat.EFF_XLS;
import static org.cmdbuild.etl.loader.EtlFileFormat.EFF_XLSX;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateTarget;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.supercsv.prefs.CsvPreference;

public class EtlTemplateUtils {

    public static String serializeImportExportTemplateTarget(EtlTemplateTarget target) {
        return target.name().replaceFirst("IET_", "").toLowerCase();
    }

    public static EtlTemplateTarget parseImportExportTemplateTarget(String value) {
        return parseEnum(value, EtlTemplateTarget.class);
    }

    public static CsvPreference getCsvPreference(EtlTemplate template) {
        String separator = firstNotNull(template.getCsvSeparator(), ",");
        checkArgument(separator.length() == 1, "invalid csv separator =< %s >", separator);
        CsvPreference csvPreference = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE.getQuoteChar(), separator.charAt(0), CsvPreference.STANDARD_PREFERENCE.getEndOfLineSymbols()).build();
        return csvPreference;
    }

    public static WorkbookInfo buildWorkbook(EtlTemplate template, String sheetName) {
        checkArgument(set(EFF_XLS, EFF_XLSX).contains(template.getFileFormat()));
        return switch (template.getFileFormat()) {
            case EFF_XLS -> {
                Workbook workbook = new HSSFWorkbook();
                yield new WorkbookInfoImpl(workbook, workbook.createSheet(sheetName), "xls", "application/vnd.ms-excel");
            }
            case EFF_XLSX -> {
                Workbook workbook = new XSSFWorkbook();
                yield new WorkbookInfoImpl(workbook, workbook.createSheet(sheetName), "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            }
            default ->
                throw new EtlException("unsupported template file format = %s", template.getFileFormat());
        };
    }

    private static class WorkbookInfoImpl implements WorkbookInfo {

        final Workbook workbook;
        final Sheet sheet;
        final String fileExt;
        private final String contentType;

        public WorkbookInfoImpl(Workbook workbook, Sheet sheet, String fileExt, String contentType) {
            this.workbook = checkNotNull(workbook);
            this.sheet = checkNotNull(sheet);
            this.fileExt = checkNotBlank(fileExt);
            this.contentType = checkNotBlank(contentType);
        }

        @Override
        public Workbook getWorkbook() {
            return workbook;
        }

        @Override
        public Sheet getSheet() {
            return sheet;
        }

        @Override
        public String getFileExt() {
            return fileExt;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

    }

}
