--  add fields to support multiple session expiration strategies

SELECT _cm3_attribute_create('"_Session"', 'LoginDate', 'TYPE: timestamptz|NOTNULL: true|DEFAULT: now()');
SELECT _cm3_attribute_create('"_Session"', 'ExpirationDate', 'TYPE: timestamptz');
SELECT _cm3_attribute_create('"_Session"', 'ExpirationStrategy', 'TYPE: varchar|NOTNULL: true|DEFAULT: default');

ALTER TABLE "_Session" ADD CONSTRAINT "_cm3_ExpirationStrategy_check" CHECK ( "ExpirationStrategy" IN ('default','expirationdate','never') );