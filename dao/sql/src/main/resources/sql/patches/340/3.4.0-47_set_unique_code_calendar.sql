-- set calendar code not null and unique

SELECT _cm3_attribute_notnull_set('"_CalendarTrigger"', 'Code');
SELECT _cm3_attribute_unique_set('"_CalendarTrigger"', 'Code');