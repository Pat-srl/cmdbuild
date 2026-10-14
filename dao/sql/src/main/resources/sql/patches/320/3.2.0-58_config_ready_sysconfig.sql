-- set config ready

DO $$ BEGIN
    IF EXISTS (SELECT * FROM "_SystemConfig" WHERE "Status" = 'A') THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.core.config.ready', 'true');
    END IF;
END $$ LANGUAGE PLPGSQL;
