-- session event logs

CREATE TRIGGER _cm3_trigger_session_events AFTER INSERT OR DELETE ON "_Session" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_session_events();

