-- Creates the new attributes needed for smart e-mail regeneration within ManageEmail widget

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE NOTICE 'adding attributes to %', '"_EmailTemplate"'::regclass;
	PERFORM cm_create_class_attribute('_EmailTemplate', 'KeepSynchronization', 'boolean', null, false, false, 'MODE: write|FIELDMODE: write|DESCR: Keep synchronization|INDEX: 9|BASEDSP: false|STATUS: active');
	PERFORM cm_create_class_attribute('_EmailTemplate', 'PromptSynchronization', 'boolean', null, false, false, 'MODE: write|FIELDMODE: write|DESCR: Prompt synchronization|INDEX: 10|BASEDSP: false|STATUS: active');

	RAISE NOTICE 'setting defaults for new attributes of %', '"_EmailTemplate"'::regclass;
	ALTER TABLE "_EmailTemplate" DISABLE TRIGGER USER;
	UPDATE "_EmailTemplate"
		SET "KeepSynchronization" = true, "PromptSynchronization" = false
		WHERE "Status" = 'A';
	ALTER TABLE "_EmailTemplate" ENABLE TRIGGER USER;

	RAISE NOTICE 'adding attributes to %', '"Email"'::regclass;
	PERFORM cm_create_class_attribute('Email', 'BccAddresses', 'text', null, false, false, 'MODE: user|FIELDMODE: write|DESCR: BCC|INDEX: 14|BASEDSP: false|STATUS: active');
	PERFORM cm_create_class_attribute('Email', 'Template', 'text', null, false, false, 'MODE: user|FIELDMODE: write|DESCR: Template|INDEX: 15|BASEDSP: false|STATUS: active');
	PERFORM cm_create_class_attribute('Email', 'KeepSynchronization', 'boolean', null, false, false, 'MODE: write|FIELDMODE: write|DESCR: Keep synchronization|INDEX: 16|BASEDSP: false|STATUS: active');
	PERFORM cm_create_class_attribute('Email', 'PromptSynchronization', 'boolean', null, false, false, 'MODE: write|FIELDMODE: write|DESCR: Prompt synchronization|INDEX: 17|BASEDSP: false|STATUS: active');
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();