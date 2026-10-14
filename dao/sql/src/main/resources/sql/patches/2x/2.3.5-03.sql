-- Normalizes the grants of processes

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	UPDATE "Grant" SET "Mode" = 'r' WHERE _cm_is_process("IdGrantedClass") IS TRUE;
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();