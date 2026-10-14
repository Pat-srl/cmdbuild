-- adding gis privileges to grant table

SELECT _cm3_attribute_create('"_Grant"', 'GisPrivileges', 'jsonb', 'DEFAULT: ''{}''::jsonb|MODE: read');