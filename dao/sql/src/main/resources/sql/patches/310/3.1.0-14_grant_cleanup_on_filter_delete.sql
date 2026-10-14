-- grant cleanup triggers

CREATE TRIGGER _cm3_trigger_filter_cleanup AFTER UPDATE ON "_Filter" FOR EACH ROW WHEN ( OLD."Status" <> 'N' AND NEW."Status" = 'N' ) EXECUTE PROCEDURE _cm3_trigger_filter_cleanup();
CREATE TRIGGER _cm3_trigger_ietemplate_cleanup AFTER UPDATE ON "_ImportExportTemplate" FOR EACH ROW WHEN ( OLD."Status" <> 'N' AND NEW."Status" = 'N' ) EXECUTE PROCEDURE _cm3_trigger_ietemplate_cleanup();
CREATE TRIGGER _cm3_trigger_view_cleanup AFTER UPDATE ON "_View" FOR EACH ROW WHEN ( OLD."Status" <> 'N' AND NEW."Status" = 'N' ) EXECUTE PROCEDURE _cm3_trigger_view_cleanup();
CREATE TRIGGER _cm3_trigger_report_cleanup AFTER UPDATE ON "_Report" FOR EACH ROW WHEN ( OLD."Status" <> 'N' AND NEW."Status" = 'N' ) EXECUTE PROCEDURE _cm3_trigger_report_cleanup();
CREATE TRIGGER _cm3_trigger_custompage_cleanup AFTER UPDATE ON "_CustomPage" FOR EACH ROW WHEN ( OLD."Status" <> 'N' AND NEW."Status" = 'N' ) EXECUTE PROCEDURE _cm3_trigger_custompage_cleanup();
