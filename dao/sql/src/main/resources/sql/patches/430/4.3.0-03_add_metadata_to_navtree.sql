-- Patch to add metadata to NavTree

ALTER TABLE "_NavTree" DROP CONSTRAINT "_cm3_Type_check";
ALTER TABLE "_NavTree" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('default', 'dependencytree', 'menu') );

SELECT _cm3_attribute_create('"_NavTree"', 'Metadata', 'jsonb', 'DEFAULT: ''{}''::jsonb');