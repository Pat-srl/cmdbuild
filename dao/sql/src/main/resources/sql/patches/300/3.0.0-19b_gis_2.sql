-- postgis fix 2
-- PARAMS: REQUIRE_SUPERUSER=true


--- GIS SCHEMA UPGRADE (step 2) ---

DO $$ DECLARE 
	_gis_master regclass;
	_gis_master_attr varchar;
	_gis_attr_type varchar;
	_class regclass;
BEGIN
	IF EXISTS (SELECT * FROM  information_schema.tables  WHERE  table_schema = 'public' AND table_name = '_cm3_gis_patch_aux' ) THEN
		CREATE SCHEMA gis;
        IF NOT EXISTS (SELECT * FROM pg_extension WHERE extname = 'postgis') THEN
            CREATE EXTENSION postgis SCHEMA gis;
            EXECUTE format('ALTER DATABASE %I SET search_path = "$user", public, gis', current_database());
        END IF;
        PERFORM postgis_lib_version(); --check gis version and path
		FOR _gis_master, _gis_master_attr, _gis_attr_type IN SELECT "Owner", "Code", "Type" FROM "_GisAttribute" WHERE "Status" = 'A' LOOP
			SELECT _cm3_gis_table_create(_gis_master, _gis_master_attr, _gis_attr_type) INTO _class;
			PERFORM _cm3_class_triggers_disable(_class);
			EXECUTE format('INSERT INTO %s ("Id","IdClass","User","BeginDate","Master","Geometry") SELECT id,%L,usr,begindate,master,ST_GeomFromEWKB(geometry) FROM _cm3_gis_patch_aux WHERE masterclass = %L::regclass AND masterattr = %L',
				_class, _class, _gis_master, _gis_master_attr);
			PERFORM _cm3_class_triggers_enable(_class);
		END LOOP;
        EXECUTE (SELECT trigger_query FROM _cm3_gis_trigger_aux);
		DROP TABLE _cm3_gis_patch_aux;
		DROP TABLE _cm3_gis_trigger_aux;
	END IF;
END $$ LANGUAGE PLPGSQL;
 