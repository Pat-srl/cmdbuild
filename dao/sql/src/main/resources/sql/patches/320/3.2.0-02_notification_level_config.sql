-- request tracking binary content handling improvements

DO $$ BEGIN
    IF _cm3_system_config_get('org.cmdbuild.core.showInfoAndWarningMessages') = 'false' THEN
        PERFORM _cm3_system_config_delete('org.cmdbuild.core.showInfoAndWarningMessages');
        PERFORM _cm3_system_config_set('org.cmdbuild.core.notificationMessagesLevelThreshold', 'ERROR');
    END IF;
END $$ LANGUAGE PLPGSQL;

