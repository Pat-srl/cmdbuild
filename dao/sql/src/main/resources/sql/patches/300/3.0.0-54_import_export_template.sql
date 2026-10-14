--  add table for import/export templates

SELECT _cm3_class_create('_ImportExportTemplate', '"Class"', 'MODE: reserved|DESCR: Import/Export Templates');

SELECT _cm3_attribute_create('"_ImportExportTemplate"', 'Active', 'boolean', 'DEFAULT: true|NOTNULL: true');
SELECT _cm3_attribute_create('"_ImportExportTemplate"', 'Config', 'jsonb', 'DEFAULT: ''{}''::jsonb|NOTNULL: true');
SELECT _cm3_attribute_create('"_ImportExportTemplate"', 'Account', 'bigint', 'FKTARGETCLASS: _EmailAccount');
SELECT _cm3_attribute_create('"_ImportExportTemplate"', 'Template', 'bigint', 'FKTARGETCLASS: _EmailTemplate');

SELECT _cm3_attribute_notnull_set('"_ImportExportTemplate"', 'Code');

CREATE UNIQUE INDEX "_cm3_ImportExportTemplate_Code_Target" ON "_ImportExportTemplate" ("Code", ("Config"->>'targetType'), ("Config"->>'targetName')) WHERE "Status" = 'A';
