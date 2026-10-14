-- lookup access fix

DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_LookUp_ParentId" ON "LookUp";

UPDATE "_LookupType" SET "Access" = 'protected' WHERE "Code" IN ('CalendarFrequency','CalendarPriority','CalendarCategory') AND "Status" = 'A';


