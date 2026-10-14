-- email fix

SELECT _cm3_attribute_index_delete('"Email"', 'Code');
SELECT _cm3_attribute_index_create('"Email"', 'Code');

