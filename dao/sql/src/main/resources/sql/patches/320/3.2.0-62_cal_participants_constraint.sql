-- add check for participants
 
ALTER TABLE "_CalendarTrigger" ADD CONSTRAINT "_cm3_Participants_check" CHECK ( "Status" <> 'A' OR _cm3_participants_check("Participants") );
ALTER TABLE "_CalendarSequence" ADD CONSTRAINT "_cm3_Participants_check" CHECK ( "Status" <> 'A' OR _cm3_participants_check("Participants") );
ALTER TABLE "_CalendarEvent" ADD CONSTRAINT "_cm3_Participants_check" CHECK ( "Status" <> 'A' OR _cm3_participants_check("Participants") );

