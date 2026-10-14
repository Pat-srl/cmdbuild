-- improve view table

ALTER TABLE "_View" ALTER COLUMN "Filter" TYPE jsonb USING "Filter"::jsonb;

SELECT _cm3_attribute_notnull_set('"_View"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_View"', 'Code'); 

ALTER TABLE "_View" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('FILTER', 'SQL') );
