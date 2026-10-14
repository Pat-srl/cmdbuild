-- improve validation of role table data

SELECT _cm3_class_triggers_disable('"Role"');
 
UPDATE "Role" SET "Type" = LOWER("Type") WHERE "Type" IS NOT NULL;
SELECT _cm3_attribute_notnull_set('"Role"', 'Code', true); 
ALTER TABLE "Role" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('admin','admin_limited','admin_readonly','default') );

SELECT _cm3_class_triggers_enable('"Role"');

