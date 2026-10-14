-- Fix _EmailAccount and _EmailTemplate inheritance and create domain between Class and Metadata

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
DECLARE
	cid oid := '"Class"'::regclass::oid;
	entry record;
	target regclass;
	query text;
BEGIN
	FOR entry IN (
		SELECT cid, 'Code' as name, _cm_get_attribute_sqltype(cid, 'Code') AS type
		UNION ALL
		SELECT cid, 'Description' as name, _cm_get_attribute_sqltype(cid, 'Description') AS type
		UNION ALL
		SELECT cid, 'User' as name, _cm_get_attribute_sqltype(cid, 'User') AS type
	) LOOP
		query = 'ALTER TABLE ' || '"_EmailAccount"'::regclass || ' ALTER COLUMN "' || entry.name || '" TYPE ' || entry.type;
		RAISE NOTICE 'executing ''%''', query;
		EXECUTE query;

		query = 'ALTER TABLE ' || '"_EmailTemplate"'::regclass || ' ALTER COLUMN "' || entry.name || '" TYPE ' || entry.type;
		RAISE NOTICE 'executing ''%''', query;
		EXECUTE query;
	END LOOP;
	
	ALTER TABLE "_EmailAccount" INHERIT "Class";
	ALTER TABLE "_EmailTemplate" INHERIT "Class";

	PERFORM cm_create_domain('ClassMetadata', 'MODE: reserved|TYPE: domain|CLASS1: Class|CLASS2: Metadata|DESCRDIR: |DESCRINV: |CARDIN: 1:N|STATUS: active');
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();