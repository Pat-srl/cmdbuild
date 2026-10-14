-- config defaults 

DO $$ BEGIN

    IF _cm3_system_config_get('org.cmdbuild.gis.geoserver.enabled') ~* 'true' AND _cm3_utils_is_blank(_cm3_system_config_get('org.cmdbuild.gis.geoserver_url')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.gis.geoserver_url', 'http://localhost:8080/geoserver');
    END IF;

    IF _cm3_system_config_get('org.cmdbuild.bim.enabled') ~* 'true' AND _cm3_utils_is_blank(_cm3_system_config_get('org.cmdbuild.bim.url')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.bim.url', 'http://localhost:8080/bimserver');
    END IF;

END $$ LANGUAGE PLPGSQL;