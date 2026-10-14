-- fix tenant config
  
DO $$ BEGIN
    IF _cm3_system_config_get('org.cmdbuild.multitenant.mode') = 'CMDBUILD_CLASS' AND _cm3_utils_is_blank(_cm3_system_config_get('org.cmdbuild.multitenant.tenantClass')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.multitenant.tenantClass','Tenant');
    END IF;
END $$ LANGUAGE PLPGSQL;

