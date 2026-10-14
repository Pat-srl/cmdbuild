-- add join view


ALTER TABLE "_View" DROP CONSTRAINT "_cm3_Type_check";
ALTER TABLE "_View" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('filter', 'sql', 'calendar', 'join') );

SELECT _cm3_attribute_create('OWNER: _View|NAME: Config|TYPE: jsonb');

