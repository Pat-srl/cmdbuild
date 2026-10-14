-- create calendar tables
 
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarCategory', 'default', 'Default');

INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarPriority', 'default', 'Default');

INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarFrequency', 'once', 'Once');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarFrequency', 'daily', 'Daily');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarFrequency', 'weekly', 'Weekly');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarFrequency', 'monthly', 'Monthly');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarFrequency', 'yearly', 'Yearly');

INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarEndType', 'never', 'Never');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarEndType', 'date', 'End Date');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarEndType', 'number', 'Number of Events');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarEndType', 'auto', 'Auto');

SELECT _cm3_class_create('_CalendarTrigger','MODE: reserved'); 
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: OwnerClass|TYPE: regclass|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: OwnerAttr|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Active|TYPE: boolean|NOTNULL: true|DEFAULT: true'); 
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: TimeZone|TYPE: varchar|DESCR: default event timezone');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: EventType|TYPE: varchar|NOTNULL: true|VALUES: instant,date|DEFAULT: instant');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Category|TYPE: bigint|LOOKUP: CalendarCategory|NOTNULL: true|DEFAULT: _cm3_lookup(''CalendarCategory.default'')');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Priority|TYPE: bigint|LOOKUP: CalendarPriority|NOTNULL: true|DEFAULT: _cm3_lookup(''CalendarPriority.default'')');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Content|TYPE: text|DESCR: detailed/extended description'); 
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Participants|TYPE: varchar[]|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]|DESCR: users/groups involved with this item (will receive notifications and at least readonly access)');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Notifications|TYPE: varchar[]|DESCR: notification templates|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]'); -- TODO |FKTARGETCLASS: _EmailTemplate)');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Job|TYPE: bigint|FKTARGETCLASS: _Job|DESCR: job to execute when item is expired');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Time|TYPE: time|NOTNULL: true|DEFAULT: ''00:00''|DESCR: default event time');
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Delay|TYPE: interval|NOTNULL: true|DEFAULT: ''P0-0-0T0:0:0''|DESCR: first event delay'); 
SELECT _cm3_attribute_create('OWNER: _CalendarTrigger|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
 

SELECT _cm3_class_create('_CalendarSequence','MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Source|TYPE: varchar|NOTNULL: true|VALUES: user,system|DEFAULT: system'); 
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Trigger|TYPE: bigint|FKTARGETCLASS: _CalendarTrigger');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: FirstEvent|TYPE: date|DESCR: first item date|NOTNULL: true'); 
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: LastEvent|TYPE: date|DESCR: last item date (no events will be generated after this date');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: TimeZone|TYPE: varchar|DESCR: event timezone|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Time|TYPE: time|NOTNULL: true|DEFAULT: ''00:00''|DESCR: default event time');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: EventType|TYPE: varchar|NOTNULL: true|VALUES: instant,date|DEFAULT: instant');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Category|TYPE: bigint|LOOKUP: CalendarCategory|NOTNULL: true|DEFAULT: _cm3_lookup(''CalendarCategory.default'')');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Priority|TYPE: bigint|LOOKUP: CalendarPriority|NOTNULL: true|DEFAULT: _cm3_lookup(''CalendarPriority.default'')');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Content|TYPE: text|DESCR: detailed/extended description'); 
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Owner|TYPE: varchar|DESCR: user (group?) owner of this item; usually null for system generated events');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Participants|TYPE: varchar[]|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]|DESCR: users/groups involved with this item (will receive notifications and at least readonly access)');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Card|TYPE: bigint');--TODO domain ref? cascade?
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Notifications|TYPE: varchar[]|DESCR: notification templates|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]'); -- TODO |FKTARGETCLASS: _EmailTemplate)');
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Job|TYPE: bigint|FKTARGETCLASS: _Job|DESCR: job to execute when item is expired'); 
SELECT _cm3_attribute_create('OWNER: _CalendarSequence|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
 

SELECT _cm3_class_create('_CalendarEvent','MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Source|TYPE: varchar|NOTNULL: true|VALUES: user,system|DEFAULT: system'); --note: system implies a sequence (?)
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Sequence|TYPE: bigint|FKTARGETCLASS: _CalendarSequence');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: EventStatus|TYPE: varchar|NOTNULL: true|VALUES: active,expired,completed,canceled|DEFAULT: active|DESCR: `completed`/`canceled` states can only be set manually, normally an event lifecycle is `active`->`expired`');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: EventType|TYPE: varchar|NOTNULL: true|VALUES: instant,range,date|DEFAULT: instant');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: EventBegin|TYPE: timestamptz|DESCR: event begin time|NOTNULL: true'); 
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: EventEnd|TYPE: timestamptz|DESCR: event end time|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: EventDate|TYPE: date|DESCR: event date|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: TimeZone|TYPE: varchar|DESCR: event timezone|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Category|TYPE: bigint|LOOKUP: CalendarCategory|NOTNULL: true|DEFAULT: _cm3_lookup(''CalendarCategory.default'')');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Priority|TYPE: bigint|LOOKUP: CalendarPriority|NOTNULL: true|DEFAULT: _cm3_lookup(''CalendarPriority.default'')'); 
-- SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Tag|...'); --multi value lookup 
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Content|TYPE: text|DESCR: detailed/extended description');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Owner|TYPE: varchar|DESCR: user owner of this item; usually null for system generated events');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Participants|TYPE: varchar[]|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]|DESCR: users/groups involved with this item (will receive notifications and at least readonly access)');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Card|TYPE: bigint');--TODO domain ref? cascade?
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Notifications|TYPE: varchar[]|DESCR: notification templates|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]'); -- TODO |FKTARGETCLASS: _EmailTemplate)');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Job|TYPE: bigint|FKTARGETCLASS: _Job|DESCR: job to execute when item is expired');
SELECT _cm3_attribute_create('OWNER: _CalendarEvent|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');

ALTER TABLE "_CalendarEvent" ADD CONSTRAINT "_cm3_EventDate_check" CHECK ( "EventDate" = ( ( "EventBegin" AT TIME ZONE "TimeZone" )::date ));

