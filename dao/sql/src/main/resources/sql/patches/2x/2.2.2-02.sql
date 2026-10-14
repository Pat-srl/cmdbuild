-- Migrates task manager's mapper keys and values

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	UPDATE "_TaskParameter"
		SET "Value" = '<' || "Value" || '>'
		WHERE "Status" = 'A' AND "Key" LIKE 'mapper.%.init';
	UPDATE "_TaskParameter"
		SET "Value" = '</' || "Value" || '>'
		WHERE "Status" = 'A' AND "Key" LIKE 'mapper.%.end';
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();