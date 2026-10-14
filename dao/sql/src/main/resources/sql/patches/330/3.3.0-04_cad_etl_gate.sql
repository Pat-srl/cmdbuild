-- add cad etl template

ALTER TABLE "_ImportExportTemplate" DROP CONSTRAINT "_cm3_Config_format_check";
ALTER TABLE "_ImportExportTemplate" ADD CONSTRAINT "_cm3_Config_format_check" CHECK ("Config"->>'format' IN ('csv','xls','xlsx','other','ifc','cad','database'));