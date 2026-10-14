-- improve timezone handling


--- CLASS STRUCTURE UPGRADE ---

DO $$ DECLARE
	_class regclass;
	_attr varchar;
	_attrs varchar[];
BEGIN
    PERFORM _cm3_utils_store_and_drop_dependant_views('"Class"'::regclass); 
	RAISE NOTICE 'alter class begindate, add timezone info';
	ALTER TABLE "Class" ALTER COLUMN "BeginDate" TYPE timestamptz,
    ALTER COLUMN "EndDate" TYPE timestamptz;
    PERFORM _cm3_utils_restore_dependant_views();

    PERFORM _cm3_utils_store_and_drop_dependant_views('"Map"'::regclass); 
	ALTER TABLE "Map" ALTER COLUMN "BeginDate" TYPE timestamptz, 
    ALTER COLUMN "EndDate" TYPE timestamptz;
    PERFORM _cm3_utils_restore_dependant_views();

    PERFORM _cm3_utils_store_and_drop_dependant_views('"SimpleClass"'::regclass); 
	ALTER TABLE "SimpleClass" ALTER COLUMN "BeginDate" TYPE timestamptz; 
    PERFORM _cm3_utils_restore_dependant_views();

    FOR _class, _attrs IN WITH q AS (SELECT owner classe, name attr FROM _cm3_attribute_list_detailed() WHERE sql_type = 'timestamp' AND inherited = false) SELECT classe, array_agg(attr) FROM q GROUP BY classe LOOP
        PERFORM _cm3_utils_store_and_drop_dependant_views(_class); 
        EXECUTE format('ALTER TABLE %s %s', _class, (SELECT string_agg(format('ALTER COLUMN %I TYPE timestamptz', attr), ', ') FROM unnest(_attrs) attr));
        PERFORM _cm3_utils_restore_dependant_views();
    END LOOP;

END $$ LANGUAGE PLPGSQL;

