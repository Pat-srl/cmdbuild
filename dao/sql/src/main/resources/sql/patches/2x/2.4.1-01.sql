-- Adds missing "TranslationUuid" values to "LookUp" table and related trigger.

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

CREATE OR REPLACE FUNCTION cm_set_translationuuid() RETURNS trigger AS $$
BEGIN
	IF(coalesce(NEW."TranslationUuid",'') = '') THEN 
		IF(TG_OP='INSERT') THEN
			NEW."TranslationUuid" = md5(random()::text)::uuid;
		ELSE
			NEW."TranslationUuid" = OLD."TranslationUuid";	
		END IF;
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER cm_set_translationuuid
	BEFORE INSERT OR UPDATE
	ON "LookUp"
	FOR EACH ROW
	EXECUTE PROCEDURE cm_set_translationuuid();