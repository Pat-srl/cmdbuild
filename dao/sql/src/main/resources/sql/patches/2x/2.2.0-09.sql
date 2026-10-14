-- Generate UUIDs for Menu entries

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
DECLARE
	menuItem record;
BEGIN
	RAISE INFO 'check consistency between code and type';
	FOR menuItem IN (
		SELECT "Id","Code","Type"
		FROM "Menu"
		WHERE "Status"='A' AND "Code" <> "Type"
	) LOOP
		RAISE EXCEPTION 'Inconsistency between Code ''%'' and Type ''%'' for row with Id ''%''',menuItem."Code",menuItem."Type",menuItem."Id";
	END LOOP;
	
	RAISE INFO 'clean Code column';
	RAISE INFO 'disable triggers';
	ALTER TABLE "Menu" DISABLE TRIGGER USER;
	
	UPDATE "Menu" 
	SET "Code" = null;
	RAISE INFO 'enable triggers';
	ALTER TABLE "Menu" ENABLE TRIGGER USER;
		
	RAISE INFO 'generate uuids for active rows';
	UPDATE "Menu"
	SET "Code"= uuid_in(md5("BeginDate"::text || random()::text)::cstring)
	WHERE "Status"='A';
	
	FOR menuItem IN (SELECT "Code"
			FROM "Menu"
			WHERE "Status"='A'
			GROUP BY "Code"
			HAVING COUNT(*)>1
	) LOOP
		RAISE EXCEPTION 'Duplicate UUID ''%'' was generated',menuItem."Code";
	END LOOP;
	
	
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();