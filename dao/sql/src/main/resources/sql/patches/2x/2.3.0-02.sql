-- Fixes wrong inheritance for some system classes

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
DECLARE
	entry record;
	query text;
BEGIN
	FOR entry IN (
		SELECT *
			FROM (
				SELECT unnest(ARRAY[
					'"_TaskParameter"'::regclass,
					'"_EmailAccount"'::regclass,
					'"_EmailTemplate"'::regclass
				]) as cid
			) as target
			LEFT JOIN pg_inherits ON cid::oid = inhrelid
			WHERE inhrelid IS NULL
	) LOOP
		RAISE NOTICE 'updating %...', entry.cid::regclass;

		query = 'ALTER TABLE ' || entry.cid::regclass || ' ALTER COLUMN "User" TYPE character varying(100)';
		RAISE NOTICE 'executing ''%''', query;
		EXECUTE query;

		query = 'ALTER TABLE ' || entry.cid::regclass || ' INHERIT "Class"';
		RAISE NOTICE 'executing ''%''', query;
		EXECUTE query;
	END LOOP;
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();