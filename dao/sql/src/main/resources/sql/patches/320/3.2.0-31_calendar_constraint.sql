-- calendar constraint improvements

CREATE UNIQUE INDEX "_cm3__CalendarSequence_Card_Trigger" ON "_CalendarSequence" ("Card", "Trigger") WHERE "Status" = 'A';
