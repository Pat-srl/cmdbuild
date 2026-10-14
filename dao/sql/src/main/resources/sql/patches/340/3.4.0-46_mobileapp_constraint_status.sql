-- mobileApp change constraint status

ALTER TABLE "_MobileAppMessage" DROP CONSTRAINT IF EXISTS "_cm3_MessageStatus_check";
ALTER TABLE "_MobileAppMessage" ADD CONSTRAINT "_cm3_MessageStatus_check" CHECK ("MessageStatus"::text = ANY (ARRAY['outgoing'::character varying, 'new'::character varying, 'archived'::character varying, 'error'::character varying]::text[]));
SELECT _cm3_attribute_default_set(_cm3_utils_name_to_regclass('_MobileAppMessage'), 'MessageStatus', 'outgoing');