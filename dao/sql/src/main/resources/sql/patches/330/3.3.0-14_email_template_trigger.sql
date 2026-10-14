-- added email template trigger to avoid deletion when used as notification template

CREATE TRIGGER "_cm3_card_enforce_fk__ImportTemplate_Config_notificationTemplate" BEFORE DELETE OR UPDATE ON "_EmailTemplate" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_target('"_ImportExportTemplate"', 'Config', 'bigint', 'notificationTemplate');

