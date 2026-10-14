-- sql import job


ALTER TABLE "_Job" DROP CONSTRAINT "_cm3_Type_check";
ALTER TABLE "_Job" ADD CONSTRAINT "_cm3_Type_check" CHECK ("Status" <> 'A' OR "Type" IN ('script','emailService','export_file','import_file','stored_function','workflow','etl','import_database') );

SELECT _cm3_class_triggers_disable('"_ImportExportTemplate"');
UPDATE "_ImportExportTemplate" t SET "Config" = "Config" || jsonb_build_object('errorTemplate', t."Template") WHERE "Template" IS NOT NULL;
UPDATE "_ImportExportTemplate" t SET "Config" = "Config" || jsonb_build_object('errorAccount', t."Account") WHERE "Account" IS NOT NULL;        
SELECT _cm3_class_triggers_enable('"_ImportExportTemplate"');

ALTER TABLE "_ImportExportTemplate" DROP COLUMN "Account";
ALTER TABLE "_ImportExportTemplate" DROP COLUMN "Template";
ALTER TABLE "_ImportExportTemplate" DROP COLUMN "Target";

DROP TRIGGER "_cm3_card_enforce_fk_Account" ON "_ImportExportTemplate";
DROP TRIGGER "_cm3_card_enforce_fk_Template" ON "_ImportExportTemplate";
DROP TRIGGER "_cm3_card_enforce_fk__Impor2d3a3306mplate_Template" ON "_EmailTemplate";
DROP TRIGGER "_cm3_card_enforce_fk__Impor2d3a3306mplate_Account" ON "_EmailAccount";

CREATE TRIGGER "_cm3_card_enforce_fk_Config_errorAccount" BEFORE INSERT OR UPDATE ON "_ImportExportTemplate" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_source('Config', '"_EmailAccount"', 'bigint', 'errorAccount'); 
CREATE TRIGGER "_cm3_card_enforce_fk_Config_errorTemplate" BEFORE INSERT OR UPDATE ON "_ImportExportTemplate" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_source('Config', '"_EmailTemplate"', 'bigint', 'errorTemplate'); 

CREATE TRIGGER "_cm3_card_enforce_fk__Impor2d3a3306mplate_Config_errorTemplate" BEFORE DELETE OR UPDATE ON "_EmailTemplate" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_target('"_ImportExportTemplate"', 'Config', 'bigint', 'errorTemplate');
CREATE TRIGGER "_cm3_card_enforce_fk__Impor2d3a3306mplate_Config_errorAccount" BEFORE DELETE OR UPDATE ON "_EmailAccount" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_target('"_ImportExportTemplate"', 'Config', 'bigint', 'errorAccount');
