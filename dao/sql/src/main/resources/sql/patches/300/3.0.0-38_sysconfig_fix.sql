-- fix systemconfig constraints

SELECT _cm3_attribute_index_unique_create('"_SystemConfig"', 'Code');
SELECT _cm3_attribute_notnull_set('"_SystemConfig"', 'Code', TRUE);

