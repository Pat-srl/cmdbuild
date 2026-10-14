-- Update EmailStatus lookups

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	UPDATE "LookUp" SET "Code" = "Description" WHERE "Type" = 'EmailStatus';
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();