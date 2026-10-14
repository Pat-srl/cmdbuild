-- calendar event status lookup
 
--TODO make this a system lookup
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarEventStatus', 'active', 'Active');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarEventStatus', 'expired', 'Expired');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarEventStatus', 'completed', 'Completed');
INSERT INTO "LookUp" ("Type", "Code", "Description") VALUES ('CalendarEventStatus', 'canceled', 'Canceled');
