-- add import export template target column
 

SELECT _cm3_attribute_create('OWNER: _ImportExportTemplate|NAME: Target|TYPE: regclass');

SELECT _cm3_class_triggers_disable('"_ImportExportTemplate"');

UPDATE "_ImportExportTemplate" SET "Target" = CASE ("Config"->>'targetType')
    WHEN 'domain' THEN _cm3_utils_domain_name_to_regclass("Config"->>'targetName')
    ELSE _cm3_utils_name_to_regclass("Config"->>'targetName') END;

SELECT _cm3_class_triggers_enable('"_ImportExportTemplate"');

SELECT _cm3_attribute_notnull_set('"_ImportExportTemplate"','Target');
