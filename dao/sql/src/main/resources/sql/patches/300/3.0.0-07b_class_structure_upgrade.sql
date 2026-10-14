-- class structure upgrade
 

--- UTILITY FUNCTIONS FOR STRUCTURE UPGRADE ---

CREATE OR REPLACE FUNCTION _cm3_utils_drop_class_triggers(_table regclass) RETURNS VOID AS $$ DECLARE
	_trigger varchar;
BEGIN
	FOR _trigger IN SELECT t.tgname FROM pg_trigger t WHERE t.tgrelid = _table AND NOT tgisinternal LOOP
		EXECUTE format('DROP TRIGGER %I ON %s',_trigger,_table);
	END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_prepare_class_triggers_query(_table regclass) RETURNS varchar AS $$
	SELECT coalesce((SELECT string_agg(pg_get_triggerdef(t.oid),';') FROM pg_trigger t WHERE t.tgrelid = _table AND NOT tgisinternal),'SELECT 1') || ';';
$$ LANGUAGE SQL;


--- CLASS STRUCTURE UPGRADE ---

DO $$ DECLARE
	_class regclass;
BEGIN
	PERFORM _cm3_utils_store_and_drop_dependant_views('"Class"'::regclass);
	RAISE NOTICE 'alter class id from int to bigint';
	ALTER TABLE "Class" ALTER COLUMN "Id" TYPE bigint; 
	RAISE NOTICE 'fix current id and other core attrs';
	FOR _class IN SELECT x FROM _cm3_class_list() x WHERE _cm3_class_has_history(x) LOOP 
		EXECUTE format('ALTER TABLE %s ALTER COLUMN "CurrentId" TYPE bigint', _cm3_utils_regclass_to_history(_class));
	END LOOP;
	PERFORM _cm3_utils_restore_dependant_views();
	FOR _class IN SELECT _cm3_class_list() LOOP
		EXECUTE format('ALTER TABLE %s ALTER COLUMN "Id" SET DEFAULT _cm3_utils_new_card_id()', _class); --TODO modify only if column uses sequence
	END LOOP;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "Class" ADD COLUMN "EndDate" timestamp without time zone, 
    ADD COLUMN "CurrentId" bigint, 
    ADD COLUMN "IdTenant" bigint;

COMMENT ON COLUMN "Class"."EndDate" IS 'MODE: reserved';
COMMENT ON COLUMN "Class"."CurrentId" IS 'MODE: reserved';
COMMENT ON COLUMN "Class"."IdTenant" IS 'MODE: reserved';
 
DO $$ DECLARE
	_class regclass;
BEGIN
	FOR _class IN SELECT _cm3_class_list_standard() LOOP
        PERFORM _cm3_class_triggers_disable(_class);
	END LOOP;
	UPDATE "Class" SET "CurrentId" = "Id" WHERE "CurrentId" IS NULL;
	FOR _class IN SELECT _cm3_class_list_standard() LOOP
        PERFORM _cm3_class_triggers_enable(_class);
	END LOOP;
	FOR _class IN SELECT _cm3_class_list_standard() LOOP		
		EXECUTE format('COMMENT ON COLUMN %s."CurrentId" IS %L', _class, 'MODE: reserved');
		EXECUTE format('COMMENT ON COLUMN %s."EndDate" IS %L', _class, 'MODE: reserved');
		EXECUTE format('COMMENT ON COLUMN %s."IdTenant" IS %L', _class, 'MODE: reserved');
	END LOOP;	
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "Class" ALTER COLUMN "CurrentId" SET NOT NULL;


--- UPGRADE TRIGGERS (required here for patch manager operations) ---

DO $$ DECLARE
	_class regclass; 
	_otherclass regclass; 
	_trigger varchar;
	_attr varchar;
	_args varchar[];
BEGIN
	FOR _class IN SELECT _cm3_domain_list() UNION SELECT c FROM _cm3_class_list() c WHERE NOT _cm3_class_is_superclass(c) LOOP
		EXECUTE format('DROP TRIGGER IF EXISTS "_SanityCheck" ON %s', _class);
		IF _cm3_class_is_simple(_class) THEN
			EXECUTE format('CREATE TRIGGER "_cm3_card_prepare_record" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_prepare_record()', _class);
		ELSE
			EXECUTE format('CREATE TRIGGER "_cm3_card_prepare_record" BEFORE INSERT OR UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_prepare_record()', _class);
		END IF;
	END LOOP;
END $$ LANGUAGE PLPGSQL;
