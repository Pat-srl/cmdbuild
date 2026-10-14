-- migrate cleanup rules config

DO $$ BEGIN

    IF EXISTS (SELECT * FROM "_SystemConfig" WHERE "Status" = 'A' AND "Code" IN ('org.cmdbuild.audit.maxRecordsToKeep','org.cmdbuild.audit.maxRecordAgeToKeepSeconds')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.database.cleanup_rules', format(
            'systemstatuslog_default[maxRecordAgeToKeepSeconds=7776000]'
            || ',request_default[maxRecordsToKeep=%s&maxRecordAgeToKeepSeconds=%s]'
            || ',eventlog_default[maxRecordsToKeep=%s&maxRecordAgeToKeepSeconds=%s]'
            || ',jobrun_default[maxRecordsToKeep=%s&maxRecordAgeToKeepSeconds=%s]',--TODO run once jobs !!
            _cm3_system_config_get('org.cmdbuild.audit.maxRecordsToKeep', '100000'), 
            _cm3_system_config_get('org.cmdbuild.audit.maxRecordAgeToKeepSeconds', '-1'),
            _cm3_system_config_get('org.cmdbuild.eventlog.maxRecordsToKeep', '-1'), 
            _cm3_system_config_get('org.cmdbuild.eventlog.maxRecordAgeToKeepSeconds', '7776000'),
            _cm3_system_config_get('org.cmdbuild.job.run.history.maxRecordsToKeep', '100000'), 
            _cm3_system_config_get('org.cmdbuild.job.run.history.maxRecordAgeToKeepSeconds', '-1')));
    END IF;

END $$ LANGUAGE PLPGSQL;
 
--     @ConfigValue(key = "runOnceJobs.maxRecordsToKeep", defaultValue = "-1")
--     @ConfigValue(key = "runOnceJobs.maxRecordAgeToKeepSeconds", defaultValue = "7776000") //90gg
