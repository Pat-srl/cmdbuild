-- calendar improvements
 
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: ProcessedNotifications|TYPE: varchar[]|DESCR: processed notifications|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]'); -- TODO |FKTARGETCLASS: _EmailTemplate)');

SELECT _cm3_attribute_index_create('"_CalendarEvent"','EventBegin');
SELECT _cm3_attribute_index_create('"_CalendarEvent"','EventEnd');
SELECT _cm3_attribute_index_create('"_CalendarEvent"','EventDate');

CREATE INDEX "_cm3__CalendarEvent_EventBegin_Active" ON "_CalendarEvent" ("EventBegin") WHERE "Status" = 'A' AND "EventStatus" = 'active';
CREATE INDEX "_cm3__CalendarEvent_EventEnd_Active" ON "_CalendarEvent" ("EventEnd") WHERE "Status" = 'A' AND "EventStatus" = 'active';
