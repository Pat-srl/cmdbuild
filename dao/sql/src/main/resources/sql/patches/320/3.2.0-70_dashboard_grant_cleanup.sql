-- dashboard grant cleanup
  
CREATE TRIGGER _cm3_trigger_dashboard_cleanup AFTER UPDATE ON "_Dashboard" FOR EACH ROW WHEN ( OLD."Status" <> 'N' AND NEW."Status" = 'N' ) EXECUTE PROCEDURE _cm3_trigger_dashboard_cleanup();
