-- etl gate noop mode

ALTER TABLE "_EtlGate" DROP CONSTRAINT "_cm3_ProcessingMode_check";
ALTER TABLE "_EtlGate" ADD CONSTRAINT "_cm3_ProcessingMode_check" CHECK ( "ProcessingMode" IN ('realtime','batch','noop') );
