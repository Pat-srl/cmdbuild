-- postgis fix 2
-- PARAMS: REQUIRE_SUPERUSER=true

DO $$ DECLARE 
	_gis_master regclass;
	_gis_master_attr varchar;
	_gis_attr_type varchar;
	_class regclass;
BEGIN
	IF EXISTS (SELECT * FROM "_GisAttribute" WHERE "Status" = 'A') THEN
		CREATE SCHEMA gis;
		CREATE EXTENSION postgis SCHEMA gis;
		FOR _gis_master, _gis_master_attr, _gis_attr_type IN SELECT "Owner", "Code", "Type" FROM "_GisAttribute" WHERE "Status" = 'A' LOOP
			SELECT _cm3_gis_table_create(_gis_master, _gis_master_attr, _gis_attr_type) INTO _class;
			PERFORM _cm3_class_triggers_disable(_class);
			EXECUTE format('INSERT INTO %s ("Id","IdClass","User","BeginDate","Master","Geometry") SELECT id,%L,usr,begindate,master,gis.st_geomfromtext(geometry)::%s FROM _cm3_gis_patch_aux WHERE masterclass = %L::regclass AND masterattr = %L',
				_class, _class, _gis_attr_type, _gis_master, _gis_master_attr);
			PERFORM _cm3_class_triggers_enable(_class);
		END LOOP;
		DROP TABLE _cm3_gis_patch_aux;
	END IF;
END $$ LANGUAGE PLPGSQL;



 