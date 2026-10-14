-- waterway messages improvements

ALTER TABLE "_EtlMessage" DROP CONSTRAINT "_cm3_MessageStatus_check";
ALTER TABLE "_EtlMessage" ADD CONSTRAINT  "_cm3_MessageStatus_check" CHECK ("MessageStatus" = ANY (ARRAY['draft', 'queued', 'processing', 'standby', 'processed', 'error', 'failed', 'forwarded', 'completed']::varchar[]));

SELECT _cm3_attribute_index_create('"_EtlMessage"', 'MessageStatus'); 

ALTER TABLE "_Job" DROP CONSTRAINT "_cm3_Type_check";

