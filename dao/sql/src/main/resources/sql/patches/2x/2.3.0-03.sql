-- Removes unused class "_MdrScopedId"

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE NOTICE 'dropping table %', '"_MdrScopedId"'::regclass;
	DROP TABLE "_MdrScopedId" CASCADE;
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();