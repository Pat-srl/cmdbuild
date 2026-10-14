-- improve email content and header handling

SELECT _cm3_attribute_create('"Email"', 'ContentType', 'varchar', 'NOTNULL: true|DEFAULT: text/plain|MODE: write');
SELECT _cm3_attribute_create('"Email"', 'ReplyTo', 'varchar', 'DESCR: Reply To address|MODE: write');
SELECT _cm3_attribute_create('"Email"', 'InReplyTo', 'varchar', 'DESCR: In Reply To Header value|MODE: hidden');
SELECT _cm3_attribute_create('"Email"', 'References', 'varchar[]', 'NOTNULL: true|DEFAULT: ARRAY[]::varchar[]|DESCR: References Header value|MODE: hidden');

SELECT _cm3_attribute_index_unique_create('"Email"', 'Code');
