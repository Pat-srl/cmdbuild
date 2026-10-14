-- improved event log

DROP TRIGGER IF EXISTS _cm3_trigger_session_events ON public."_Session";

CREATE TRIGGER _cm3_trigger_session_events AFTER INSERT OR UPDATE OR DELETE ON public."_Session" FOR EACH ROW EXECUTE PROCEDURE public._cm3_trigger_session_events();