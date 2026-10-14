-- Create TranslationUuis column in LookUp table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	PERFORM cm_create_class_attribute('LookUp', 'TranslationUuid', 'text', null, false, false, 'MODE: write|DESCR: Translations Uuid|STATUS: active');

	UPDATE "LookUp"
	SET "TranslationUuid"= uuid_in(md5("BeginDate"::text || random()::text)::cstring)
	WHERE "Status" = 'A';

END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();