-- Fixes the values within "Delay" columns of tables "Email" and "_EmailTemplate"

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	ALTER TABLE "Email" DISABLE TRIGGER USER;
	UPDATE "Email"
	SET "Delay" =
		CASE
			WHEN "Delay" > 0 THEN "Delay" / 1000
			WHEN "Delay" < 0 THEN 2592000
		END
	WHERE "Status" = 'A';
	ALTER TABLE "Email" ENABLE TRIGGER USER;

	ALTER TABLE "_EmailTemplate" DISABLE TRIGGER USER;
	UPDATE "_EmailTemplate"
	SET "Delay" =
		CASE
			WHEN "Delay" > 0 THEN "Delay" / 1000
			WHEN "Delay" < 0 THEN 2592000
		END
	WHERE "Status" = 'A';
	ALTER TABLE "_EmailTemplate" ENABLE TRIGGER USER;
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();