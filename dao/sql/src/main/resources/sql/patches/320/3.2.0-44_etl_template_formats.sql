-- etl templat format upgrade
 
SELECT _cm3_class_triggers_disable('"_ImportExportTemplate"');
UPDATE "_ImportExportTemplate" SET "Config" = "Config" || jsonb_build_object('format','other') WHERE "Config"->>'format' = 'java';
SELECT _cm3_class_triggers_enable('"_ImportExportTemplate"');

ALTER TABLE "_ImportExportTemplate" ADD CONSTRAINT "_cm3_Config_format_check" CHECK ("Config"->>'format' IN ('csv','xls','xlsx','other','ifc','database'));
