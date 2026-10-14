-- update gis configuration

DO $$ BEGIN

    IF _cm3_system_config_get('org.cmdbuild.gis.enabled') ~* 'true' THEN
        IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.gis.osm_minzoom')) THEN
            PERFORM _cm3_system_config_set('org.cmdbuild.gis.minZoomLevel', _cm3_system_config_get('org.cmdbuild.gis.osm_minzoom'));
            PERFORM _cm3_system_config_delete('org.cmdbuild.gis.osm_minzoom');
        END IF;

        IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.gis.osm_maxzoom')) THEN
            PERFORM _cm3_system_config_set('org.cmdbuild.gis.maxZoomLevel', _cm3_system_config_get('org.cmdbuild.gis.osm_maxzoom'));
            PERFORM _cm3_system_config_delete('org.cmdbuild.gis.osm_maxzoom');
        END IF;

        IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.gis.geoserver_workspace')) THEN
            PERFORM _cm3_system_config_set('org.cmdbuild.gis.geoserver.workspace', _cm3_system_config_get('org.cmdbuild.gis.geoserver_workspace'));
            PERFORM _cm3_system_config_delete('org.cmdbuild.gis.geoserver_workspace');
        END IF;

        IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.gis.geoserver_url')) THEN
            PERFORM _cm3_system_config_set('org.cmdbuild.gis.geoserver.url', _cm3_system_config_get('org.cmdbuild.gis.geoserver_url'));
            PERFORM _cm3_system_config_delete('org.cmdbuild.gis.geoserver_url');
        END IF;

        IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.gis.geoserver_admin_password')) THEN
            PERFORM _cm3_system_config_set('org.cmdbuild.gis.geoserver.admin.password', _cm3_system_config_get('org.cmdbuild.gis.geoserver_admin_password'));
            PERFORM _cm3_system_config_delete('org.cmdbuild.gis.geoserver_admin_password');
        END IF;

        IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.gis.geoserver_admin_user')) THEN
            PERFORM _cm3_system_config_set('org.cmdbuild.gis.geoserver.admin.user', _cm3_system_config_get('org.cmdbuild.gis.geoserver_admin_user'));
            PERFORM _cm3_system_config_delete('org.cmdbuild.gis.geoserver_admin_user');
        END IF;

    END IF;

END $$ LANGUAGE PLPGSQL;

