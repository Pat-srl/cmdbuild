-- upgrade email tabl, add fk

SELECT _cm3_class_triggers_disable('"Email"');

CREATE TABLE _patch_aux_email_accounts AS SELECT "Id", "Account" FROM "Email" WHERE "Status" = 'A' AND NULLIF("Account", '') IS NOT NULL;
CREATE TABLE _patch_aux_email_templates AS SELECT "Id", "Template" FROM "Email" WHERE "Status" = 'A' AND NULLIF("Template", '') IS NOT NULL;
CREATE TABLE _patch_aux_email_template_notifywith AS SELECT "Id", "NotifyWith" FROM "Email" WHERE "Status" = 'A' AND NULLIF("NotifyWith", '') IS NOT NULL;

UPDATE "Email" SET "Template" = NULL, "NotifyWith" = NULL, "Account" = NULL;

SELECT _cm3_attribute_delete('"Email"', 'Account');
SELECT _cm3_attribute_delete('"Email"', 'Template');
SELECT _cm3_attribute_delete('"Email"', 'NotifyWith');

SELECT _cm3_attribute_create('"Email"', 'Account', 'bigint', 'FKTARGETCLASS: _EmailAccount');
SELECT _cm3_attribute_create('"Email"', 'Template', 'bigint', 'FKTARGETCLASS: _EmailTemplate');
SELECT _cm3_attribute_create('"Email"', 'NotifyWith', 'bigint', 'FKTARGETCLASS: _EmailTemplate');


DO $$ DECLARE
	_record record;
	_id bigint;
BEGIN
	FOR _record IN SELECT * FROM _patch_aux_email_accounts LOOP
		SELECT INTO _id "Id" FROM "_EmailAccount" WHERE "Code" = _record."Account" AND "Status" = 'A';
		IF _id IS NULL THEN
			RAISE WARNING 'account not found for code = % email = %: will set to null', _record."Account", _record."Id";
		ELSE
			UPDATE "Email" SET "Account" = _id WHERE "Id" = _record."Id" AND "Status" = 'A';
		END IF;
	END LOOP;
	FOR _record IN SELECT * FROM _patch_aux_email_templates LOOP
		SELECT INTO _id "Id" FROM "_EmailTemplate" WHERE "Code" = _record."Template" AND "Status" = 'A';
		IF _id IS NULL THEN
			IF _record."Template" !~ '^([1-9]|implicitTemplateName)$' THEN
				RAISE WARNING 'template not found for code = % email = %: will set to null', _record."Template", _record."Id";
			END IF;
		ELSE
			UPDATE "Email" SET "Template" = _id WHERE "Id" = _record."Id" AND "Status" = 'A';
		END IF;
	END LOOP;
	FOR _record IN SELECT * FROM _patch_aux_email_template_notifywith LOOP
		SELECT INTO _id "Id" FROM "_EmailTemplate" WHERE "Code" = _record."NotifyWith" AND "Status" = 'A';
		IF _id IS NULL THEN
			RAISE WARNING 'template not found for code = % email = %: will set to null', _record."NotifyWith", _record."Id";
		ELSE
			UPDATE "Email" SET "NotifyWith" = _id WHERE "Id" = _record."Id" AND "Status" = 'A';
		END IF;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP TABLE _patch_aux_email_accounts;
DROP TABLE _patch_aux_email_templates;
DROP TABLE _patch_aux_email_template_notifywith;

SELECT _cm3_class_triggers_enable('"Email"');
