-- gis nav config fix
 
DO $$ BEGIN
    IF EXISTS (SELECT * FROM "_NavTree" WHERE "Code" = 'gisnavigation' AND "Status" = 'A' AND "Active" = TRUE) AND _cm3_system_config_get('org.cmdbuild.gis.navigation.enabled') IS NULL THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.gis.navigation.enabled', 'true');
    END IF;
END $$ LANGUAGE PLPGSQL;

