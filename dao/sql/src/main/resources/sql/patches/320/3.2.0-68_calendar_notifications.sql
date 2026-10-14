-- imporve calendar notifications

ALTER TABLE "_CalendarTrigger" DROP COLUMN "Notifications";
ALTER TABLE "_CalendarSequence" DROP COLUMN "Notifications";
ALTER TABLE "_CalendarEvent" DROP COLUMN "Notifications";
-- ALTER TABLE "_CalendarEvent" DROP COLUMN "ProcessedNotifications";

SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Notifications|TYPE: jsonb|DESCR: notifications|NOTNULL: true|DEFAULT: ''[]''::jsonb');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Notifications|TYPE: jsonb|DESCR: notifications|NOTNULL: true|DEFAULT: ''[]''::jsonb');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Notifications|TYPE: jsonb|DESCR: notifications|NOTNULL: true|DEFAULT: ''[]''::jsonb');
-- SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: ProcessedNotifications|TYPE: jsonb|DESCR: processed notifications|NOTNULL: true|DEFAULT: ''[]''::jsonb');

ALTER TABLE "_CalendarTrigger" ADD CONSTRAINT "_cm3_Notifications_check" CHECK ( "Status" <> 'A' OR _cm3_notifications_check("Notifications") );
ALTER TABLE "_CalendarSequence" ADD CONSTRAINT "_cm3_Notifications_check" CHECK ( "Status" <> 'A' OR _cm3_notifications_check("Notifications") );
ALTER TABLE "_CalendarEvent" ADD CONSTRAINT "_cm3_Notifications_check" CHECK ( "Status" <> 'A' OR _cm3_notifications_check("Notifications") );
-- ALTER TABLE "_CalendarEvent" ADD CONSTRAINT "_cm3_ProcessedNotifications_check" CHECK (   ); TODO

SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: EventCompletion|TYPE: timestamptz|DESCR: event completion timestamp');

SELECT _cm3_attribute_create('OWNER: _EmailTemplate|NAME: Reports|TYPE: varchar[]|DESCR: attach reports|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]');
