-- domain structure upgrade

DO $$ DECLARE
	class_table varchar;
	_domain regclass;
	_record record;
	_orphans int;
	_inv_count int;
BEGIN
	PERFORM _cm3_utils_store_and_drop_dependant_views('"Map"'::regclass);
    RAISE NOTICE 'upgrade "Map" columns';
	ALTER TABLE "Map" ALTER COLUMN "Id" TYPE bigint;
	ALTER TABLE "Map" ALTER COLUMN "Id" SET DEFAULT _cm3_utils_new_card_id();
	ALTER TABLE "Map" ADD COLUMN "CurrentId" bigint;
	COMMENT ON COLUMN "Map"."CurrentId" IS 'MODE: reserved';
	ALTER TABLE "Map" ALTER COLUMN "IdObj1" TYPE bigint;
	ALTER TABLE "Map" ALTER COLUMN "IdObj2" TYPE bigint; 
    PERFORM _cm3_utils_restore_dependant_views();
	FOR _domain IN SELECT _cm3_domain_list() LOOP	
        PERFORM _cm3_utils_store_and_drop_dependant_views(_domain);
		EXECUTE format('ALTER TABLE %s ALTER COLUMN "Id" SET DEFAULT _cm3_utils_new_card_id()', _domain); --TODO modify only if column uses sequence
		RAISE NOTICE 'upgrade currentid for domain = %', _domain;
		PERFORM _cm3_class_triggers_disable(_domain);
        EXECUTE format('SELECT COUNT(*) FROM %s WHERE "Status" <> ''U''', _cm3_utils_regclass_to_history(_domain)) INTO _inv_count;
        IF _inv_count > 0 THEN 
            RAISE WARNING 'cleaning up %s invalid history records for domain = %', _inv_count, _domain;
            EXECUTE format('DELETE FROM %s WHERE "Status" <> ''U''', _cm3_utils_regclass_to_history(_domain));
        END IF;
		EXECUTE format('UPDATE %s SET "CurrentId" = "Id" WHERE "Status" <> ''U''', _domain);
		EXECUTE format('UPDATE %s m SET "Id" = _cm3_utils_new_card_id()', _cm3_utils_regclass_to_history(_domain));
		EXECUTE format('UPDATE %s m SET "CurrentId" = cur."Id" 
            FROM (SELECT *,row_number() over(PARTITION BY "IdObj1","IdObj2" ORDER BY "BeginDate" desc) _rank FROM %s WHERE "Status" <> ''U'') cur 
            WHERE m."IdObj1" = cur."IdObj1" AND m."IdObj2" = cur."IdObj2" AND _rank = 1 AND m."Status" = ''U''', 
            _cm3_utils_regclass_to_history(_domain), _domain);
            -- NOTE: the above will join an history record with the latest active/deleted record; this is not necessarily correct, for example when 
            --       there is more than one deleted record, then the history might belong to a previous del record, and not to the latest
		EXECUTE format('COMMENT ON COLUMN %s."CurrentId" IS %L', _domain, 'MODE: reserved');
        EXECUTE format('SELECT COUNT(*) FROM %s WHERE "CurrentId" IS NULL', _cm3_utils_regclass_to_history(_domain)) INTO _orphans;
        IF _orphans > 0 THEN
            RAISE WARNING 'cleaning up % orphan history records for domain = %', _orphans, _domain;
            EXECUTE format('DELETE FROM %s WHERE "CurrentId" IS NULL', _cm3_utils_regclass_to_history(_domain));
        END IF;
		PERFORM _cm3_class_triggers_enable(_domain);
        PERFORM _cm3_utils_restore_dependant_views();
	END LOOP;
END $$ LANGUAGE PLPGSQL; 

ALTER TABLE "Map" ALTER COLUMN "CurrentId" SET NOT NULL;
