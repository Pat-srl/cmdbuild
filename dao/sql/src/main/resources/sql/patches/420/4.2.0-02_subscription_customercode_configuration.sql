-- refactor configs: rename mobile.customer.code configuration keys to subscription.customer.code

DO $$ BEGIN

    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.mobile.customer.code')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.subscription.customer.code', _cm3_system_config_get('org.cmdbuild.mobile.customer.code'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.mobile.customer.code');
    END IF;

END $$ LANGUAGE PLPGSQL;