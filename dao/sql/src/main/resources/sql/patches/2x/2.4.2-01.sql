-- Adds a CHECK on "Status" attribute for historic tables

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
DECLARE
	table_name text;
	history_table regclass;
	statement text;
BEGIN
	FOR table_name IN
		SELECT _cm_cmtable(cid)
			FROM _cm_class_list() as cid
	LOOP
		BEGIN
			RAISE DEBUG 'examining table with name "%"', table_name;
			history_table = _cm_history_dbname(table_name);
			RAISE DEBUG 'altering table %', history_table;
			statement = 'ALTER TABLE ' || history_table
				|| ' ADD CONSTRAINT "_Check_Status" CHECK ("Status" = ''U''::bpchar);';
			RAISE DEBUG '> %', statement;
			EXECUTE statement;
		EXCEPTION WHEN OTHERS THEN
			RAISE WARNING 'table with name "%" has no history', table_name;
		END;
	END LOOP;
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();

CREATE OR REPLACE FUNCTION _cm_create_class_history(CMClassName text)
	RETURNS void AS $$
BEGIN
	EXECUTE '
		CREATE TABLE '|| _cm_history_dbname_unsafe(CMClassName) ||'
		(
			"CurrentId" int4 NOT NULL,
			"EndDate" timestamp NOT NULL DEFAULT now(),
			CONSTRAINT ' || quote_ident(_cm_historypk_name(CMClassName)) ||' PRIMARY KEY ("Id"),
			CONSTRAINT '|| quote_ident(_cm_historyfk_name(CMClassName, 'CurrentId')) ||' FOREIGN KEY ("CurrentId")
				REFERENCES '||_cm_table_dbname(CMClassName)||' ("Id") ON UPDATE RESTRICT ON DELETE SET NULL
		) INHERITS ('||_cm_table_dbname(CMClassName)||');
	';
	PERFORM _cm_create_index(_cm_history_id(CMClassName), 'CurrentId');
	EXECUTE 'ALTER TABLE '|| _cm_history_dbname_unsafe(CMClassName) ||' ADD CONSTRAINT "_Check_Status" CHECK ("Status" = ''U''::bpchar);';
END;
$$ LANGUAGE PLPGSQL;