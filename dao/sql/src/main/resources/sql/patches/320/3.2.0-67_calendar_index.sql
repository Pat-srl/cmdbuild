-- add index for calendar event card, and check for cal event config
  
SELECT _cm3_attribute_index_create('"_CalendarEvent"','Card');

ALTER TABLE "_CalendarEvent" ADD CONSTRAINT "_cm3_Config_check" CHECK ( "Status" <> 'A' OR _cm3_calendar_event_config_check("Config") );