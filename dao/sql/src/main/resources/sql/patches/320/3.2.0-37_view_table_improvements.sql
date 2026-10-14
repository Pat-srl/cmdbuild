-- improve view table; add support for calendar views


ALTER TABLE "_View" DROP CONSTRAINT "_cm3_Type_check";

SELECT _cm3_class_triggers_disable('"_View"');

UPDATE "_View" SET "Type" = LOWER("Type");

SELECT _cm3_class_triggers_enable('"_View"');

ALTER TABLE "_View" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('filter', 'sql', 'calendar') );
