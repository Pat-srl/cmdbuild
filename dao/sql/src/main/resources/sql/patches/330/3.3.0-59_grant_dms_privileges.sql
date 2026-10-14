-- adding dms privileges to grant table

SELECT _cm3_attribute_create('"_Grant"', 'DmsPrivileges', 'jsonb', 'DEFAULT: ''{}''::jsonb|MODE: read');