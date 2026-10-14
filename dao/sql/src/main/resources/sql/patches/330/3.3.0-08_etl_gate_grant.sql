-- etl gate grant

ALTER TABLE "_Grant" DROP CONSTRAINT "_cm3_Type_check";
ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('class','process','custompage','filter','view','report','etltemplate','etlgate','dashboard') );

CREATE TRIGGER _cm3_trigger_etlgate_cleanup AFTER UPDATE ON "_EtlGate" FOR EACH ROW WHEN ( OLD."Status" <> 'N' AND NEW."Status" = 'N' ) EXECUTE PROCEDURE _cm3_trigger_etlgate_cleanup();
