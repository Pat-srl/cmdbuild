-- Adds missing "TranslationUuid" values to "LookUp" table.

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	UPDATE "LookUp" 
		SET "TranslationUuid" = md5(random()::text)::uuid
		WHERE coalesce("TranslationUuid",'') = '';
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();