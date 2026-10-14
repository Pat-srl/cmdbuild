-- improve email content and header handling

SELECT _cm3_attribute_index_delete('"Email"', 'Code');

SELECT _cm3_attribute_create('"Email"', 'MessageId', 'varchar', 'MODE: hidden');
SELECT _cm3_attribute_index_create('"Email"', 'MessageId');
