-- fix calendar lookup
  
UPDATE "LookUp" SET "Code" = 'other', "Description" = 'Other/Auto' WHERE "Type" = 'CalendarEndType' AND "Code" = 'auto' AND "Status" = 'A';

