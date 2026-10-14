-- convert configs from cmis to alfresco

DO $$ BEGIN

    IF _cm3_system_config_get('org.cmdbuild.dms.enabled') ~* 'true' AND _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.dms.service.cmis.url')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.dms.service.alfresco.apiBaseUrl', REPLACE(_cm3_system_config_get('org.cmdbuild.dms.service.cmis.url'), 'alfresco/api/-default-/public/cmis/versions/1.1/atom', 'alfresco/api/-default-/public/alfresco/versions/1'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.dms.service.cmis.url');
    END IF;
    IF _cm3_system_config_get('org.cmdbuild.dms.enabled') ~* 'true' AND _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.dms.service.cmis.path')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.dms.service.alfresco.path', _cm3_system_config_get('org.cmdbuild.dms.service.cmis.path'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.dms.service.cmis.path');
    END IF;
    IF _cm3_system_config_get('org.cmdbuild.dms.enabled') ~* 'true' AND _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.dms.service.cmis.user')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.dms.service.alfresco.user', _cm3_system_config_get('org.cmdbuild.dms.service.cmis.user'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.dms.service.cmis.user');
    END IF;
    IF _cm3_system_config_get('org.cmdbuild.dms.enabled') ~* 'true' AND _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.dms.service.cmis.password')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.dms.service.alfresco.password', _cm3_system_config_get('org.cmdbuild.dms.service.cmis.password'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.dms.service.cmis.password');
    END IF;

END $$ LANGUAGE PLPGSQL;