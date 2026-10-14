-- improve etl template constraint

DROP INDEX "_cm3_ImportExportTemplate_Code_Target";

SELECT _cm3_attribute_index_unique_create('"_ImportExportTemplate"', 'Code'); 
