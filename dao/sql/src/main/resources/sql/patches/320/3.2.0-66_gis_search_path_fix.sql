-- fix gis search path if required
-- PARAMS: RELOAD_CONNECTION_AFTER=true

DO $$ BEGIN
    IF _cm3_system_config_get('org.cmdbuild.gis.enabled')::boolean = TRUE THEN
        BEGIN
            PERFORM postgis_lib_version();
        EXCEPTION WHEN OTHERS THEN
            EXECUTE format('ALTER DATABASE %I SET search_path = "$user", public, gis', current_database());
            SET search_path = "$user", public, gis;
            PERFORM postgis_lib_version();
        END;
    END IF;
END $$ LANGUAGE PLPGSQL;
