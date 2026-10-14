-- class structure tables refactoring, 2

SELECT _cm3_attribute_notnull_set('"_ClassMetadata"', 'Code');
SELECT _cm3_attribute_notnull_set('"_AttributeMetadata"', 'Owner');
SELECT _cm3_attribute_notnull_set('"_AttributeMetadata"', 'Code');
SELECT _cm3_attribute_notnull_set('"_AttributeGroup"', 'Owner');

SELECT _cm3_attribute_unique_set('"_ClassMetadata"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_AttributeMetadata"', 'Owner', 'Code'); 
SELECT _cm3_attribute_index_unique_create('"_AttributeGroup"', 'Owner', 'Code'); 

