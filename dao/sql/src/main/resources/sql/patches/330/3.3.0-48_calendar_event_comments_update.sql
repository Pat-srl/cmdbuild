-- update calendar event attribute description

select _cm3_attribute_comment_set('"_CalendarEvent"', 'EventStatus', 'DESCR', 'Event status');
select _cm3_attribute_comment_set('"_CalendarEvent"', 'Owner', 'DESCR', 'Event owner');
select _cm3_attribute_comment_set('"_CalendarEvent"', 'Participants', 'DESCR', 'Event partecipants');